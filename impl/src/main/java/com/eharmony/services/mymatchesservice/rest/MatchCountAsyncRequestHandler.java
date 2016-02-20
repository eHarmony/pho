package com.eharmony.services.mymatchesservice.rest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.singles.common.status.MatchStatus;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
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

    public void getMatchCounts(final MatchCountRequestContext matchCountRequestContext,
            final AsyncResponse asyncResponse) {
        
        Timer.Context t = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX,
                METRICS_COUNTMATCHES_ASYNC);

        CountDownLatch doneSignal=new CountDownLatch(MatchStatus.values().length);
        AtomicInteger total = new AtomicInteger();
        
        final MatchCountDto response = new MatchCountDto();
        Action1<MatchCountRequestContext> onNext = request -> {
            int count = hbaseStoreFeedService.getUserMatchesCount(request);
            total.addAndGet(count);
            switch(request.getStatus()) {
            case OPENCOMM:
                response.setOpenComm(count);
                break;
            case NEW:
                //set the real recent new count 
                count = hbaseStoreFeedService.getUserNewMatchesCount(request);
                response.setNewCount(count);
                break;
            case ARCHIVED:
                response.setArchived(count);
                break;
            case CLOSED:
                response.setClosed(count);
                break;
            case MYTURN:
                response.setMyTurn(count);
                break;
            case THEIRTURN:
                response.setTheirTurn(count);
                break;
            default:
                break;    
            }
        };
        
        Action1<Throwable> onException = (throwable) -> {
            doneSignal.countDown();
            asyncResponse.resume(throwable);
        };
        Action0 onStop = () -> {
            doneSignal.countDown();
        };

        long userId = matchCountRequestContext.getUserId();
        for (MatchStatus status : MatchStatus.values()) {
            MatchCountRequestContext perTypeRequest = new MatchCountRequestContext();
            perTypeRequest.setUserId(userId);
            perTypeRequest.setStatus(status);
            Observable<MatchCountRequestContext> matchCountRequestObservable = Observable.just(perTypeRequest)
                    .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
            matchCountRequestObservable.subscribe(onNext,onException,onStop);
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            logger.warn("Interrupted waitting for HBase response", e);
            asyncResponse.resume(e);
            return;
        } finally {
            long timeelapsed = t.stop()/1000000;
            logger.info("response time {}", timeelapsed);
        }
        response.setAll(total.get());
        ResponseBuilder builder = Response.ok().entity(response);
        asyncResponse.resume(builder.build());
        
    }
}
