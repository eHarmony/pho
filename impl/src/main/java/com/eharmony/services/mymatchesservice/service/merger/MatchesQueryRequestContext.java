package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class MatchesQueryRequestContext {

    private LegacyMatchDataFeedDto legacyMatchDataFeed;
    private Set<MatchDataFeedItemDto> matchDataFeed;
    private FeedMergeStrategyType feedMergeType;
    private long userId;

    public LegacyMatchDataFeedDto getLegacyMatchDataFeed() {
        return legacyMatchDataFeed;
    }

    public void setLegacyMatchDataFeed(LegacyMatchDataFeedDto legacyMatchDataFeed) {
        this.legacyMatchDataFeed = legacyMatchDataFeed;
    }

    public Set<MatchDataFeedItemDto> getMatchDataFeed() {
        return matchDataFeed;
    }

    public void setMatchDataFeed(Set<MatchDataFeedItemDto> matchDataFeed) {
        this.matchDataFeed = matchDataFeed;
    }

    public FeedMergeStrategyType getFeedMergeType() {
        return feedMergeType;
    }

    public void setFeedMergeType(FeedMergeStrategyType feedMergeType) {
        this.feedMergeType = feedMergeType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
