package com.eharmony.services.mymatchesservice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.repository.MatchDataFeedItemQueryRequest;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
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
    private MatchQueryMetricsFactroy matchQueryMetricsFactory;

    private static final String DEFAULT_SORT_BY_FIELD = "deliveredDate";
    //private static final String COMM_SORT_BY_FIELD = "lastCommDate";
    //HBase has only limit clause, there is no rownum based browsing
    private static final int START_PAGE = 1;

    private static final String METRICS_HIERARCHY_PREFIX = HBaseStoreFeedServiceImpl.class.getCanonicalName();
    private static final String METRICS_GETBYSTATUS_METHOD = "getUserMatchesByStatusGroup";
    private static final String METRICS_GETMATCH_METHOD = "getUserMatch";

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
	public Observable<HBaseStoreFeedResponse> getUserMatchSafe(
			HBaseStoreFeedRequestContext request) {
		
        Observable<HBaseStoreFeedResponse> hbaseStoreFeedResponse = Observable.defer(() -> Observable
                .just(getUserMatch(request)));
        
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
	
    private HBaseStoreFeedResponse getUserMatch(final HBaseStoreFeedRequestContext request) {
        HBaseStoreFeedResponse response = new HBaseStoreFeedResponse(request.getMatchStatusGroup());
        MatchFeedQueryContext queryContext = request.getMatchFeedQueryContext();
        MatchStatusGroupEnum matchStatusGroup = request.getMatchStatusGroup();
        long startTime = System.currentTimeMillis();
        Timer.Context metricsTimer = matchQueryMetricsFactory.getTimerContext(METRICS_HIERARCHY_PREFIX, 
        																		METRICS_GETMATCH_METHOD, 
                                                                              matchStatusGroup);
        Histogram metricsHistogram = matchQueryMetricsFactory.getHistogram(METRICS_HIERARCHY_PREFIX,
        																		METRICS_GETMATCH_METHOD,
                                                                             matchStatusGroup);
        
        long userId = queryContext.getUserId();
        long matchId = request.getMatchFeedQueryContext().getMatchId();
        
        try {
        	MatchDataFeedItemQueryRequest requestQuery = new MatchDataFeedItemQueryRequest(userId);       
        	requestQuery.setMatchId(matchId);
        	
            MatchDataFeedItemDto oneMatch = queryRepository.getMatchDataFeedItemDto(requestQuery);
            
            Set<MatchDataFeedItemDto> resultSet = new HashSet<MatchDataFeedItemDto>();
            if(oneMatch != null){
            	resultSet.add(oneMatch);
            }
            
            response.setHbaseStoreFeedItems(resultSet);
            if (CollectionUtils.isNotEmpty(resultSet)) {
                response.setDataAvailable(true);
                metricsHistogram.update(resultSet.size());
            }
        } catch (Throwable e) {
            logger.warn("Exception while fetching single match from HBase store for user {} and matchId {}",
                    userId, matchId, e);
            response.setError(e);
        } finally {
            metricsTimer.stop();
            long endTime = System.currentTimeMillis();
            logger.info("HBase response time {} for user {} and matchId {}", (endTime - startTime), userId, matchId);
        }
        return response;
    }

    private HBaseStoreFeedResponse getUserMatchesByStatusGroup(final HBaseStoreFeedRequestContext request) {
        HBaseStoreFeedResponse response = new HBaseStoreFeedResponse(request.getMatchStatusGroup());
        MatchFeedQueryContext queryContext = request.getMatchFeedQueryContext();
        MatchStatusGroupEnum matchStatusGroup = request.getMatchStatusGroup();
        long startTime = System.currentTimeMillis();
        Timer.Context metricsTimer = matchQueryMetricsFactory.getTimerContext(METRICS_HIERARCHY_PREFIX, 
                                                                              METRICS_GETBYSTATUS_METHOD, 
                                                                              matchStatusGroup);
        Histogram metricsHistogram = matchQueryMetricsFactory.getHistogram(METRICS_HIERARCHY_PREFIX,
                                                                             METRICS_GETBYSTATUS_METHOD,
                                                                             matchStatusGroup);
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
            metricsTimer.stop();
            long endTime = System.currentTimeMillis();
            logger.info("HBase response time {} for user {} and statusgroup {}", (endTime - startTime), request
                           .getMatchFeedQueryContext().getUserId(), request.getMatchStatusGroup() != null ? request
                           .getMatchStatusGroup().getName() : "NONE");
        }
        return response;
    }

    protected void populateRequestWithQueryParams(final HBaseStoreFeedRequestContext request,
            MatchDataFeedQueryRequest requestQuery) {
        FeedMergeStrategyType strategy = request.getFeedMergeType();
        if (strategy != null && strategy == FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE) {
            requestQuery.setSelectedFields(selectedProfileFields);
        } else {
            requestQuery.setSelectedFields(null);
        }
        List<Integer> statuses = new ArrayList<Integer>();
        Set<MatchStatusEnum> matchStuses = request.getMatchStatuses();
        if (CollectionUtils.isNotEmpty(matchStuses)) {
            for (MatchStatusEnum matchStatus : matchStuses) {
                statuses.add(matchStatus.toInt());
            }
            requestQuery.setMatchStatusFilters(statuses);
            populateQueryWithLimitParams(request, requestQuery);

        }
        requestQuery.setSortBy(resolveSortBy(request.getMatchStatusGroup()));
    }

    private String resolveSortBy(MatchStatusGroupEnum matchStatusGroup) {
        if (matchStatusGroup == null) {
            return DEFAULT_SORT_BY_FIELD;
        }
        if (matchStatusGroup.equals(MatchStatusGroupEnum.COMMUNICATION)) {
            //return COMM_SORT_BY_FIELD;
        	//TODO: fix when moved away from Voldy completely (Voldy does not sort by COMM Date).
        	return DEFAULT_SORT_BY_FIELD;
        }

        return DEFAULT_SORT_BY_FIELD;
    }

    protected void populateQueryWithLimitParams(final HBaseStoreFeedRequestContext request,
            MatchDataFeedQueryRequest requestQuery) {

            Integer feedLimit = null;  

            if (request.isFallbackRequest()) {
                feedLimit = matchFeedLimitsByStatusConfiguration.getFallbackFeedLimitForGroup(request
                        .getMatchStatusGroup());
            } else {
                feedLimit = matchFeedLimitsByStatusConfiguration.getDefaultFeedLimitForGroup(request
                        .getMatchStatusGroup());
            }
            
        if (feedLimit != null) {
            requestQuery.setStartPage(START_PAGE);
            requestQuery.setPageSize(feedLimit);
        }
    }

}
