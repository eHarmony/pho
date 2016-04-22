package com.eharmony.services.mymatchesservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import rx.Observable;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.query.criterion.Ordering;
import com.eharmony.datastore.query.criterion.Ordering.NullOrdering;
import com.eharmony.datastore.query.criterion.Ordering.Order;
import com.eharmony.datastore.repository.MatchDataFeedItemCountQueryRequest;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.rest.MatchCountRequestContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

@Component
public class HBaseStoreFeedServiceImpl implements HBaseStoreFeedService {

    private static final Logger logger = LoggerFactory.getLogger(HBaseStoreFeedServiceImpl.class);

    @Resource
    private MatchStoreQueryRepository queryRepository;

    @Resource(name = "matchFeedProfileFieldsList")
    private List<String> selectedProfileFields;

    @Resource
    private MatchFeedLimitsByStatusConfiguration matchFeedLimitsByStatusConfiguration;

    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;
    @Value("${mqs.newmatch.threshold.days}")
    private int newMatchThresholdDays;

    private static final String DEFAULT_SORT_BY_FIELD = "deliveredDate";

    private static final List<Ordering> DEFAULT_ORDERINGS = Arrays.asList(new Ordering(DEFAULT_SORT_BY_FIELD, Order.DESCENDING, NullOrdering.LAST));
    private static final List<Ordering> SPOTLIT_ORDERINGS = Arrays.asList(new Ordering("spotlightEnd", Order.ASCENDING, NullOrdering.LAST));
    // private static final String COMM_SORT_BY_FIELD = "lastCommDate";
    // HBase has only limit clause, there is no rownum based browsing
    private static final int START_PAGE = 1;

    private static final String METRICS_HIERARCHY_PREFIX = HBaseStoreFeedServiceImpl.class.getCanonicalName();
    private static final String METRICS_GETBYSTATUS_METHOD = "getUserMatchesByStatusGroup";
    private static final String METRICS_GETSPOTLITBYSTATUS_METHOD = "getSpotlitUserMatchesByStatusGroup";
    private static final String METRICS_GETCOUNT_METHOD = "getUserMatchesCount";

    private static final int MAXIMUM_SPOTLIT_USERS = 4;

    @Override
    public Observable<HBaseStoreFeedResponse> getUserMatchesByStatusGroupSafe(HBaseStoreFeedRequestContext request) {
        Observable<HBaseStoreFeedResponse> hbaseStoreFeedResponse = Observable.defer(() -> Observable
                .just(getUserMatchesByStatusGroup(request)));
        hbaseStoreFeedResponse.onErrorReturn(ex -> {
            logger.warn(
                    "Exception while fetching data from hbase for user {} and returning empty feed for safe method",
                    request.getMatchFeedQueryContext().getUserId(), ex);
            HBaseStoreFeedResponse response = new HBaseStoreFeedResponse(request.getMatchStatusGroup());
            response.setError(ex);
            return response;
        });
        return hbaseStoreFeedResponse;
    }
    
    @Override
    public Observable<HBaseStoreFeedResponse> getSpotlitUserMatchesSafe(HBaseStoreFeedRequestContext request) {
        Observable<HBaseStoreFeedResponse> hbaseStoreFeedResponse = Observable.defer(() -> Observable
                .just(getSpotlitUserMatches(request)));
        hbaseStoreFeedResponse.onErrorReturn(ex -> {
            logger.warn(
                    "Exception while fetching data from hbase for user {} and returning empty feed for safe method",
                    request.getMatchFeedQueryContext().getUserId(), ex);
            HBaseStoreFeedResponse response = new HBaseStoreFeedResponse(request.getMatchStatusGroup());
            response.setError(ex);
            return response;
        });
        return hbaseStoreFeedResponse;
    }

    private HBaseStoreFeedResponse getUserMatchesByStatusGroup(final HBaseStoreFeedRequestContext request) {
        
        HBaseStoreFeedResponse response = new HBaseStoreFeedResponse(request.getMatchStatusGroup());
        MatchFeedQueryContext queryContext = request.getMatchFeedQueryContext();
        MatchStatusGroupEnum matchStatusGroup = request.getMatchStatusGroup();
        
        long startTime = System.currentTimeMillis();
        Timer.Context metricsTimer = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX,
                METRICS_GETBYSTATUS_METHOD, matchStatusGroup);
        Histogram metricsHistogram = matchQueryMetricsFactroy.getHistogram(METRICS_HIERARCHY_PREFIX,
                METRICS_GETBYSTATUS_METHOD, matchStatusGroup);
        try {
            MatchDataFeedQueryRequest requestQuery = new MatchDataFeedQueryRequest(queryContext.getUserId());
            populateRequestWithQueryParams(request, requestQuery);
            Set<MatchDataFeedItemDto> matchdataFeed = queryRepository.getMatchDataFeed(requestQuery);
            response.setHbaseStoreFeedItems(matchdataFeed);
            if (CollectionUtils.isNotEmpty(matchdataFeed)) {
                response.setDataAvailable(true);
                metricsHistogram.update(matchdataFeed.size());
            }
        } catch (Throwable e) {
            logger.warn("Exception while fetching the matches from HBase store for user {} and group {}",
                    queryContext.getUserId(), request.getMatchStatusGroup(), e);
            response.setError(e);
        } finally {
            metricsTimer.close();
            long endTime = System.currentTimeMillis();
            logger.info("HBase response time {} for user {} and statusgroup {}", (endTime - startTime), request
                    .getMatchFeedQueryContext().getUserId(), request.getMatchStatusGroup() != null ? request
                    .getMatchStatusGroup().getName() : "NONE");
        }
        return response;
    }
    
    
    @SuppressWarnings("resource")
    private HBaseStoreFeedResponse getSpotlitUserMatches(final HBaseStoreFeedRequestContext request) {
        
        HBaseStoreFeedResponse response = new HBaseStoreFeedResponse(null);
        MatchFeedQueryContext queryContext = request.getMatchFeedQueryContext();
        
        Timer.Context metricsTimer = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX,
                METRICS_GETSPOTLITBYSTATUS_METHOD, null);
        Histogram metricsHistogram = matchQueryMetricsFactroy.getHistogram(METRICS_HIERARCHY_PREFIX,
                METRICS_GETSPOTLITBYSTATUS_METHOD, null);
        try {
            MatchDataFeedQueryRequest requestQuery = new MatchDataFeedQueryRequest(queryContext.getUserId());
            populateSpotlitRequestWithQueryParams(request, requestQuery);
            Set<MatchDataFeedItemDto> matchdataFeed = queryRepository.getSpotlitMatchDataFeed(requestQuery);
            response.setHbaseStoreFeedItems(matchdataFeed);
            if (CollectionUtils.isNotEmpty(matchdataFeed)) {
                response.setDataAvailable(true);
                metricsHistogram.update(matchdataFeed.size());
            }
        } catch (Throwable e) {
            logger.warn("Exception while fetching the matches from HBase store for user {} and group {}",
                    queryContext.getUserId(), request.getMatchStatusGroup(), e);
            response.setError(e);
        } finally {
            long totalTimeNS = metricsTimer.stop();
            logger.info("HBase response time {} ms for user {} for spotlight matches", totalTimeNS/ 1000000, request
                    .getMatchFeedQueryContext().getUserId());
        }
        return response;
    }

    protected void populateRequestWithQueryParams(final HBaseStoreFeedRequestContext request,
            MatchDataFeedQueryRequest requestQuery) {
        
        List<Integer> statuses = new ArrayList<Integer>();
        Set<MatchStatusEnum> matchStuses = request.getMatchStatuses();
        if (CollectionUtils.isNotEmpty(matchStuses)) {
            for (MatchStatusEnum matchStatus : matchStuses) {
                statuses.add(matchStatus.toInt());
            }
            requestQuery.setMatchStatusFilters(statuses);
            populateQueryWithLimitParams(request, requestQuery);

        }
        requestQuery.setOrderings(resolveOrderings(request.getMatchStatusGroup()));
    }
    
    protected void populateSpotlitRequestWithQueryParams(final HBaseStoreFeedRequestContext request,
            MatchDataFeedQueryRequest requestQuery) {
        List<Integer> statuses = new ArrayList<Integer>();
        Set<MatchStatusEnum> matchStatuses = request.getMatchStatuses();
        if (CollectionUtils.isNotEmpty(matchStatuses)) {
            for (MatchStatusEnum matchStatus : matchStatuses) {
                statuses.add(matchStatus.toInt());
            }
            requestQuery.setMatchStatusFilters(statuses);
            requestQuery.setPageSize(MAXIMUM_SPOTLIT_USERS);

        }
        requestQuery.setOrderings(SPOTLIT_ORDERINGS);
        
    }

    private List<Ordering> resolveOrderings(MatchStatusGroupEnum matchStatusGroup) {
        if (matchStatusGroup == null) {
            return DEFAULT_ORDERINGS;
        }
        if (matchStatusGroup.equals(MatchStatusGroupEnum.COMMUNICATION)) {
            // return COMM_SORT_BY_FIELD;
            // TODO: fix when moved away from Voldy completely (Voldy does not sort by COMM Date).
            return DEFAULT_ORDERINGS;
        }

        return DEFAULT_ORDERINGS;
    }

    protected void populateQueryWithLimitParams(final HBaseStoreFeedRequestContext request,
            MatchDataFeedQueryRequest requestQuery) {

        Integer feedLimit = matchFeedLimitsByStatusConfiguration.getDefaultFeedLimitForGroup(request
                .getMatchStatusGroup());

        if (feedLimit != null) {
            requestQuery.setStartPage(START_PAGE);
            requestQuery.setPageSize(feedLimit);
        }
    }

    @SuppressWarnings("resource")
    protected Set<Long> getUserMatchesCountByStatus(MatchCountRequestContext request, boolean isRecentNewMatchesRequest) {
        Long userId = request.getUserId();
        MatchDataFeedItemCountQueryRequest queryRequest = new MatchDataFeedItemCountQueryRequest(userId);
        queryRequest.setNewMatchThresholdDays(newMatchThresholdDays);

        Timer.Context metricsTimer = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX,
                METRICS_GETCOUNT_METHOD);
        queryRequest.setMatchStatus(request.getStatus());
        Set<Long> matchIdSet = null;
        try {
            if (isRecentNewMatchesRequest) {
                matchIdSet = queryRepository.getNewMatchCount(queryRequest);
            } else {
                matchIdSet = queryRepository.getMatchCount(queryRequest);
            }
        } catch (Exception exp) {
            logger.warn("Exception while fetching the matches count from HBase store for user {}", userId, exp);
        } finally {
            long elapsed = metricsTimer.stop() / 1000000;
            logger.info("HBase response time {} for user {}", elapsed, userId);
        }
        return matchIdSet;
    }

    @Override
    public Observable<HBaseStoreCountResponse> getUserMatchesCount(MatchCountRequestContext request) {
        Observable<HBaseStoreCountResponse> HBaseStoreCountResponse = Observable.defer(() -> {
            HBaseStoreCountResponse response = new HBaseStoreCountResponse();
            response.setMatchStatus(request.getStatus());
            response.setMatchIds(getUserMatchesCountByStatus(request, false));
            return Observable.just(response);
        });

        return HBaseStoreCountResponse;
    }

    @Override
    public Observable<HBaseStoreCountResponse> getUserNewMatchesCount(MatchCountRequestContext request) {
        Observable<HBaseStoreCountResponse> HBaseStoreCountResponse = Observable.defer(() -> {
            HBaseStoreCountResponse response = new HBaseStoreCountResponse();
            response.setMatchStatus(request.getStatus());
            response.setRecentNew(true);
            response.setMatchIds(getUserMatchesCountByStatus(request, true));
            return Observable.just(response);
        });

        return HBaseStoreCountResponse;
    }
}
