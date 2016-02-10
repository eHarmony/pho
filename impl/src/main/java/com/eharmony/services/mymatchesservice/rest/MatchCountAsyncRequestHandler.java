package com.eharmony.services.mymatchesservice.rest;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Component;

import rx.Observable;

@Component
public class MatchCountAsyncRequestHandler {

    public void getMatchCounts(final MatchCountRequestContext matchCountRequestContext,
            final AsyncResponse asyncResponse) {

        Observable<MatchCountRequestContext> matchCountRequestObservable = Observable
                .defer(() -> Observable.just(matchCountRequestContext));
        matchCountRequestObservable.subscribe(response -> {

            ResponseBuilder builder = buildResponse(response);

            asyncResponse.resume(builder.build());
        } , (throwable) -> {
            asyncResponse.resume(throwable);
        } , () -> {
            asyncResponse.resume("");
        });
    }

    private ResponseBuilder buildResponse(MatchCountRequestContext requestContext) {

        ResponseBuilder builder = Response.ok().entity(requestContext);
        builder.status(Status.OK);
        return builder;
    }

}
