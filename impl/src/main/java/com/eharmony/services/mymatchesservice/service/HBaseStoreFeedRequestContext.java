package com.eharmony.services.mymatchesservice.service;

import java.util.Set;

import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.google.common.base.Preconditions;

public class HBaseStoreFeedRequestContext {

    private FeedMergeStrategyType feedMergeType;
    private final MatchFeedQueryContext matchFeedQueryContext;
    private boolean isFallbackRequest;
    private MatchStatusGroupEnum matchStatusGroup;
    private Set<MatchStatusEnum> matchStatuses;

    public HBaseStoreFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
    }

    public FeedMergeStrategyType getFeedMergeType() {
        return feedMergeType;
    }

    public void setFeedMergeType(FeedMergeStrategyType feedMergeType) {
        this.feedMergeType = feedMergeType;
    }

    public boolean isFallbackRequest() {
        return isFallbackRequest;
    }

    public void setFallbackRequest(boolean isFallbackRequest) {
        this.isFallbackRequest = isFallbackRequest;
    }

    public MatchStatusGroupEnum getMatchStatusGroup() {
        return matchStatusGroup;
    }

    public void setMatchStatusGroup(MatchStatusGroupEnum matchStatusGroup) {
        this.matchStatusGroup = matchStatusGroup;
    }

    public Set<MatchStatusEnum> getMatchStatuses() {
        return matchStatuses;
    }

    public void setMatchStatuses(Set<MatchStatusEnum> matchStatuses) {
        this.matchStatuses = matchStatuses;
    }

    public MatchFeedQueryContext getMatchFeedQueryContext() {
        return matchFeedQueryContext;
    }

}
