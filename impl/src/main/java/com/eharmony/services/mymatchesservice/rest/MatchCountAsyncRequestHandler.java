package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchCountDto;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreCountResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.singles.common.status.MatchStatus;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Component
public class MatchCountAsyncRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(MatchCountAsyncRequestHandler.class);

    private static final String METRICS_HIERARCHY_PREFIX = MatchFeedAsyncRequestHandler.class.getCanonicalName();
    private static final String METRICS_COUNTMATCHES_ASYNC = "getUserMatchesCount";

    @Resource
    private ExecutorServiceProvider executorServiceProvider;
    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;

    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;

    private Func2<MatchCountDto, HBaseStoreCountResponse, MatchCountDto> zipFunction = (finalResponse,
            responsePerStatus) -> {
        int count = responsePerStatus.getMatchIds().size();
        if (responsePerStatus.isRecentNew()) {
            finalResponse.setNewCount(count);
            return finalResponse;
        }
        switch (responsePerStatus.getMatchStatus()) {
        case OPENCOMM:
            finalResponse.setOpenComm(count);
            break;
        case NEW:
            break;
        case ARCHIVED:
            finalResponse.setArchived(count);
            break;
        case CLOSED:
            finalResponse.setClosed(count);
            break;
        case MYTURN:
            finalResponse.setMyTurn(count);
            break;
        case THEIRTURN:
            finalResponse.setTheirTurn(count);
            break;
        default:
            break;
        }
        finalResponse.setAll(finalResponse.getAll() + count);
        return finalResponse;
    };

    public void getMatchCounts(final MatchCountRequestContext matchCountRequestContext,
            final AsyncResponse asyncResponse) {

        Timer.Context t = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX,
                METRICS_COUNTMATCHES_ASYNC);

        long userId = matchCountRequestContext.getUserId();
        Observable<MatchCountDto> matchCountRequestObservable = Observable
                .defer(() ->Observable.just(new MatchCountDto()));
        for (MatchStatus status : MatchStatus.values()) {
            MatchCountRequestContext perTypeRequest = new MatchCountRequestContext();
            perTypeRequest.setUserId(userId);
            perTypeRequest.setStatus(status);
            matchCountRequestObservable = matchCountRequestObservable
                    .zipWith(hbaseStoreFeedService.getUserMatchesCount(perTypeRequest), zipFunction)
                    .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
            
        }
        MatchCountRequestContext perTypeRequest = new MatchCountRequestContext();
        perTypeRequest.setUserId(userId);
        perTypeRequest.setStatus(MatchStatus.NEW);
        matchCountRequestObservable = matchCountRequestObservable
                .zipWith(hbaseStoreFeedService.getUserNewMatchesCount(perTypeRequest), zipFunction)
                .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
        matchCountRequestObservable.subscribe(response -> {
            long timeelapsed = t.stop()/1000000;
            logger.info("response time {}", timeelapsed);
            ResponseBuilder builder = Response.ok().entity(response);
            asyncResponse.resume(builder.build());
        } , (throwable) -> {
            long timeelapsed = t.stop()/1000000;
            logger.info("response time {}", timeelapsed);
            asyncResponse.resume(throwable);
        } , () -> {
            asyncResponse.resume("");
        });
    }
}
