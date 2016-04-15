package com.eharmony.services.mymatchesservice.rest;

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
import com.eharmony.services.mymatchesservice.service.merger.MatchCountRedisDataMerger;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.singles.common.status.MatchStatus;

@Component
public class MatchCountAsyncRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(MatchCountAsyncRequestHandler.class);

    private static final String METRICS_HIERARCHY_PREFIX = "com.eharmony.services.mymatchesservice.rest.MatchFeedAsyncRequestHandler";
    private static final String METRICS_COUNTMATCHES_ASYNC = "getUserMatchesCount";

    @Resource
    private RedisStoreFeedService redisStoreFeedService;

    @Resource
    private ExecutorServiceProvider executorServiceProvider;

    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;

    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;
    
    @Resource
    private MatchCountRedisDataMerger matchCountRedisDataMerger;

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
            if(status == MatchStatus.CLOSED) {
                logger.debug("filtering closed status as MQS is not responsible for closed matches...");
                continue;
            }
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
        matchCountRedisDataMerger.mergeRedisData(matchCountContext);
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
                //Ignoring the new status as client is only interested in recent new
                break;
            case ARCHIVED:
                matchCountDto.setArchived(matchesByStatus.get(matchStatus).size());
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
            if(matchStatus == MatchStatus.CLOSED) {
                //Ignoring closed status as MQS is not responsible for closed resources
                continue;
            }
            matchCountDto.setAll(matchCountDto.getAll() + matchesByStatus.get(matchStatus).size());
        }

        if (CollectionUtils.isNotEmpty(matchCountContext.getRecentNewMatches())) {
            matchCountDto.setNewCount(matchCountContext.getRecentNewMatches().size());
        }
    }

}
