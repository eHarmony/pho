package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.google.common.base.Preconditions;

public class BasicStoreFeedRequestContext {

    private final MatchFeedQueryContext matchFeedQueryContext;

    public BasicStoreFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
    }

    public MatchFeedQueryContext getMatchFeedQueryContext() {
        return matchFeedQueryContext;
    }

}
