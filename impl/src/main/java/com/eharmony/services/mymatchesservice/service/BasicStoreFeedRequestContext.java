package com.eharmony.services.mymatchesservice.service;

import java.util.Set;

import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.google.common.base.Preconditions;

public class BasicStoreFeedRequestContext {

    private FeedMergeStrategyType feedMergeType;
    private final MatchFeedQueryContext matchFeedQueryContext;
    private MatchStatusGroupEnum matchStatusGroup;
    private Set<MatchStatusEnum> matchStatuses;

    public BasicStoreFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
    }

    public FeedMergeStrategyType getFeedMergeType() {
        return feedMergeType;
    }

    public void setFeedMergeType(FeedMergeStrategyType feedMergeType) {
        this.feedMergeType = feedMergeType;
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
