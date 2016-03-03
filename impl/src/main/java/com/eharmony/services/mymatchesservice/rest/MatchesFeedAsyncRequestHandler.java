package com.eharmony.services.mymatchesservice.rest;

import javax.ws.rs.container.AsyncResponse;

public interface MatchesFeedAsyncRequestHandler {

    public void getMatchesFeed(final MatchFeedQueryContext matchFeedQueryContext, final AsyncResponse asyncResponse);
    
}
