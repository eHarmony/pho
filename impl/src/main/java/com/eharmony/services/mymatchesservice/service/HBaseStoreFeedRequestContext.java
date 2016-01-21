package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;

public class HBaseStoreFeedRequestContext extends BasicStoreFeedRequestContext{

    private boolean isFallbackRequest;

    public HBaseStoreFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
    	super(matchFeedQueryContext);
    }

    public boolean isFallbackRequest() {
        return isFallbackRequest;
    }

    public void setFallbackRequest(boolean isFallbackRequest) {
        this.isFallbackRequest = isFallbackRequest;
    }

}
