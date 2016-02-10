package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Component
public class MatchCountAsyncRequestHandler {
    @Resource
    private ExecutorServiceProvider executorServiceProvider;
    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;


    public void getMatchCounts(final MatchCountRequestContext matchCountRequestContext,
            final AsyncResponse asyncResponse) {

        Observable<MatchCountContext> matchCountRequestObservable = Observable
                .defer(() -> Observable.just(matchCountRequestContext))
                .map(fetchCount)
                .subscribeOn(
                        Schedulers.from(executorServiceProvider.getTaskExecutor()));
        matchCountRequestObservable.subscribe(response -> {

            ResponseBuilder builder = Response.ok().entity(response);

            asyncResponse.resume(builder.build());
        } , (throwable) -> {
            asyncResponse.resume(throwable);
        } , () -> {
            asyncResponse.resume("");
        });
    }

    
    private Func1<MatchCountRequestContext, MatchCountContext> fetchCount = request->{
        return hbaseStoreFeedService.getUserMatchesCount(request);
    };

}
