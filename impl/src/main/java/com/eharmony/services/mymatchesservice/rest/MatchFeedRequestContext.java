package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class MatchFeedRequestContext {

    private final long userId;
    private LegacyMatchDataFeedDto legacyMatchDataFeedDto;
    private Set<MatchDataFeedItemDto> newStoreFeed;
    private FeedMergeStrategyType feedMergeType;
    
    public MatchFeedRequestContext(final long userId) {
        this.userId = userId;
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

    public long getUserId() {
        return userId;
    }

    public FeedMergeStrategyType getFeedMergeType() {
        return feedMergeType;
    }

    public void setFeedMergeType(FeedMergeStrategyType feedMergeType) {
        this.feedMergeType = feedMergeType;
    }
    
    
}
