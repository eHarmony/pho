package com.eharmony.services.mymatchesservice.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchCountDto;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreCountResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.singles.common.status.MatchStatus;

@Component
public class MatchCountAsyncRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(MatchCountAsyncRequestHandler.class);

    private static final String METRICS_HIERARCHY_PREFIX = MatchFeedAsyncRequestHandler.class.getCanonicalName();
    private static final String METRICS_COUNTMATCHES_ASYNC = "getUserMatchesCount";

    @Resource
    private RedisStoreFeedService redisStoreFeedService;

    @Resource
    private ExecutorServiceProvider executorServiceProvider;

    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;

    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;

    /**
     * Matches count will be returned after fetching the counts from HBase and merging with Redis results.
     * 
     * @param matchCountRequestContext
     *            MatchCountRequestContext
     * @param asyncResponse
     *            AsyncResponse
     */
    public void getMatchesCount(final MatchCountRequestContext matchCountRequestContext, AsyncResponse asyncResponse) {
        Timer.Context t = matchQueryMetricsFactroy
                .getTimerContext(METRICS_HIERARCHY_PREFIX, METRICS_COUNTMATCHES_ASYNC);
        long userId = matchCountRequestContext.getUserId();
        Observable<MatchCountContext> matchCountRequestObservable = makeMqsCountRequestObservable(matchCountRequestContext);

        matchCountRequestObservable.subscribe(response -> {
            handleCountResponse(response);
            long timeelapsed = t.stop() / 1000000;
            logger.info("match count response time {}", timeelapsed);
            ResponseBuilder builder = Response.ok().entity(response.getMatchCountDto());
            asyncResponse.resume(builder.build());
        }, (throwable) -> {
            long timeelapsed = t.stop() / 1000000;
            logger.error("Exception while fetching match count for user {}, duration {}", userId, timeelapsed,
                    throwable);
            asyncResponse.resume(throwable);
        }, () -> {
            asyncResponse.resume("");
        });

    }

    protected Observable<MatchCountContext> makeMqsCountRequestObservable(
            final MatchCountRequestContext matchCountRequestContext) {
        MatchCountDto matchCountDto = new MatchCountDto();
        MatchCountContext matchCountContext = new MatchCountContext();
        matchCountContext.setMatchCountDto(matchCountDto);
        matchCountContext.setMatchCountRequestContext(matchCountRequestContext);

        Observable<MatchCountContext> matchCountQueryRequestObservable = Observable.just(matchCountContext);
        Observable<LegacyMatchDataFeedDtoWrapper> redisStoreFeedObservable = redisStoreFeedService
                .getUserMatchesSafe(matchCountRequestContext.getUserId());

        matchCountQueryRequestObservable = matchCountQueryRequestObservable.zipWith(redisStoreFeedObservable,
                populateRediesStoreMatchesFeed).subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));

        matchCountQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchCountQueryRequestObservable,
                matchCountRequestContext);
        return matchCountQueryRequestObservable;
    }

    private Observable<MatchCountContext> chainHBaseFeedRequestsByStatus(
            Observable<MatchCountContext> matchCountContextObservable,
            final MatchCountRequestContext matchCountRequestContext) {

        long userId = matchCountRequestContext.getUserId();
        for (MatchStatus status : MatchStatus.values()) {
            MatchCountRequestContext perTypeRequest = new MatchCountRequestContext();
            perTypeRequest.setUserId(userId);
            perTypeRequest.setStatus(status);
            matchCountContextObservable = matchCountContextObservable.zipWith(
                    hbaseStoreFeedService.getUserMatchesCount(perTypeRequest), zipFunction).subscribeOn(
                    Schedulers.from(executorServiceProvider.getTaskExecutor()));

        }
        MatchCountRequestContext perTypeRequest = new MatchCountRequestContext();
        perTypeRequest.setUserId(userId);
        perTypeRequest.setStatus(MatchStatus.NEW);
        matchCountContextObservable = matchCountContextObservable.zipWith(
                hbaseStoreFeedService.getUserNewMatchesCount(perTypeRequest), recentNewZipFunction).subscribeOn(
                Schedulers.from(executorServiceProvider.getTaskExecutor()));
        return matchCountContextObservable;
    }

    private Func2<MatchCountContext, LegacyMatchDataFeedDtoWrapper, MatchCountContext> populateRediesStoreMatchesFeed = (
            matchCountContext, redisMatchDataFeedDtoWrapper) -> {

        if (redisMatchDataFeedDtoWrapper != null) {
            matchCountContext.setRedisMatchDataFeedDto(redisMatchDataFeedDtoWrapper.getLegacyMatchDataFeedDto());
        }

        return matchCountContext;
    };

    private Func2<MatchCountContext, HBaseStoreCountResponse, MatchCountContext> zipFunction = (matchCountContext,
            responsePerStatus) -> {

        if (responsePerStatus != null) {
            matchCountContext.putMatchCountsByStatus(responsePerStatus.getMatchStatus(),
                    responsePerStatus.getMatchIds());
        }

        return matchCountContext;
    };

    private Func2<MatchCountContext, HBaseStoreCountResponse, MatchCountContext> recentNewZipFunction = (
            matchCountContext, responsePerStatus) -> {

        if (responsePerStatus != null) {
            matchCountContext.setRecentNewMatches(responsePerStatus.getMatchIds());
        }

        return matchCountContext;
    };

    private void handleCountResponse(MatchCountContext matchCountContext) {
        mergeRedisData(matchCountContext);
        calculateFinalCounts(matchCountContext);

    }

    private void calculateFinalCounts(MatchCountContext matchCountContext) {
        MatchCountDto matchCountDto = matchCountContext.getMatchCountDto();
        Map<MatchStatus, Set<Long>> matchesByStatus = matchCountContext.getMatchesByStatus();

        if (MapUtils.isEmpty(matchesByStatus)) {
            return;
        }
        for (MatchStatus matchStatus : matchesByStatus.keySet()) {
            switch (matchStatus) {
            case OPENCOMM:
                matchCountDto.setOpenComm(matchesByStatus.get(matchStatus).size());
                break;
            case NEW:
                break;
            case ARCHIVED:
                matchCountDto.setArchived(matchesByStatus.get(matchStatus).size());
                break;
            case CLOSED:
                matchCountDto.setClosed(matchesByStatus.get(matchStatus).size());
                break;
            case MYTURN:
                matchCountDto.setMyTurn(matchesByStatus.get(matchStatus).size());
                break;
            case THEIRTURN:
                matchCountDto.setTheirTurn(matchesByStatus.get(matchStatus).size());
                break;
            default:
                break;
            }
            matchCountDto.setAll(matchCountDto.getAll() + matchesByStatus.get(matchStatus).size());
        }

        if (CollectionUtils.isNotEmpty(matchCountContext.getRecentNewMatches())) {
            matchCountDto.setNewCount(matchCountContext.getRecentNewMatches().size());
        }
    }

    private MatchCountContext mergeRedisData(MatchCountContext matchCountContext) {

        Map<MatchStatus, Set<Long>> hbaseMatchesByStatus = matchCountContext.getMatchesByStatus();
        LegacyMatchDataFeedDto redisDeltaFeedDto = matchCountContext.getRedisMatchDataFeedDto();

        // redis or hbase feed is empty, no need to merge the results.
        if (redisDeltaFeedDto == null || MapUtils.isEmpty(hbaseMatchesByStatus)) {
            return matchCountContext;
        }

        Map<MatchStatus, Set<Long>> matchesByStatus = matchCountContext.getMatchesByStatus();
        Map<String, Map<String, Map<String, Object>>> redisStoreMatches = redisDeltaFeedDto.getMatches();
        
        if(MapUtils.isEmpty(redisStoreMatches)) {
            return matchCountContext;
        }

        Map<MatchStatus, Set<Long>> changedMatchesMap = new HashMap<MatchStatus, Set<Long>>();

        if (MapUtils.isNotEmpty(matchesByStatus)) {
            for (MatchStatus matchStatus : matchesByStatus.keySet()) {
                Set<Long> matchIdsByStatus = matchesByStatus.get(matchStatus);
                Iterator<Long> matchIdsIte = matchIdsByStatus.iterator();
                matchIdsIte.forEachRemaining(mid -> {
                    if (redisStoreMatches != null && redisStoreMatches.get(mid) != null) {
                        Map<String, Map<String, Object>> redisMatch = redisStoreMatches.get(mid);
                        Map<String, Object> deltaMatchSection = redisMatch.get(MatchFeedModel.SECTIONS.MATCH);
                        Object matchStatusObj = deltaMatchSection != null ? deltaMatchSection
                                .get(MatchFeedModel.MATCH.STATUS) : null;
                        // TODO get rid of ordinal comparison
                        if (matchStatusObj != null
                                && Integer.valueOf(matchStatusObj.toString()) != matchStatus.ordinal()) {
                            Set<Long> changedMatchesByStatusSet = changedMatchesMap.get(matchStatus);
                            if (changedMatchesByStatusSet == null) {
                                changedMatchesByStatusSet = new HashSet<Long>();
                                changedMatchesMap.put(matchStatus, changedMatchesByStatusSet);
                            }
                            changedMatchesByStatusSet.add(mid);
                            matchIdsIte.remove();
                            if (matchStatus == MatchStatus.NEW) {
                                Set<Long> recentNewMatches = matchCountContext.getRecentNewMatches();
                                if (CollectionUtils.isNotEmpty(recentNewMatches)) {
                                    recentNewMatches.remove(mid);
                                }
                            }
                        }
                    }
                });
            }

        }

        if (MapUtils.isNotEmpty(changedMatchesMap)) {
            changedMatchesMap.forEach((a, b) -> {
                matchesByStatus.get(a).addAll(b);
            });
        }
        return matchCountContext;
    }
}
