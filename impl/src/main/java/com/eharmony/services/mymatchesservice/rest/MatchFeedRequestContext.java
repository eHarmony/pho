package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.google.common.base.Preconditions;

public class MatchFeedRequestContext {

    private LegacyMatchDataFeedDto legacyMatchDataFeedDto;
    private Set<MatchDataFeedItemDto> newStoreFeed;
    private FeedMergeStrategyType feedMergeType;
    final MatchFeedQueryContext matchFeedQueryContext;

    public MatchFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
    }

    public LegacyMatchDataFeedDto getLegacyMatchDataFeedDto() {
        return legacyMatchDataFeedDto;
    }

    public void setLegacyMatchDataFeedDto(LegacyMatchDataFeedDto legacyMatchDataFeedDto) {
        this.legacyMatchDataFeedDto = legacyMatchDataFeedDto;
    }

    public Set<MatchDataFeedItemDto> getNewStoreFeed() {
        return newStoreFeed;
    }

    public void setNewStoreFeed(Set<MatchDataFeedItemDto> newStoreFeed) {
        this.newStoreFeed = newStoreFeed;
    }

    public FeedMergeStrategyType getFeedMergeType() {
        return feedMergeType;
    }

    public void setFeedMergeType(FeedMergeStrategyType feedMergeType) {
        this.feedMergeType = feedMergeType;
    }

    public MatchFeedQueryContext getMatchFeedQueryContext() {
        return matchFeedQueryContext;
    }

    public long getUserId() {
        return this.matchFeedQueryContext.getUserId();
    }

}
