package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.google.common.base.Preconditions;

public class MatchFeedRequestContext {

    private LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper;
    private Set<MatchDataFeedItemDto> newStoreFeed;
    private FeedMergeStrategyType feedMergeType;
    final MatchFeedQueryContext matchFeedQueryContext;
    private boolean isFallbackRequest;

    public MatchFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
    }

    public boolean isFallbackRequest() {
        return isFallbackRequest;
    }

    public void setFallbackRequest(boolean isFallbackRequest) {
        this.isFallbackRequest = isFallbackRequest;
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

    public LegacyMatchDataFeedDtoWrapper getLegacyMatchDataFeedDtoWrapper() {
        return legacyMatchDataFeedDtoWrapper;
    }

    public void setLegacyMatchDataFeedDtoWrapper(LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper) {
        this.legacyMatchDataFeedDtoWrapper = legacyMatchDataFeedDtoWrapper;
    }

    public LegacyMatchDataFeedDto getLegacyMatchDataFeedDto() {
        if (legacyMatchDataFeedDtoWrapper != null) {
            return legacyMatchDataFeedDtoWrapper.getLegacyMatchDataFeedDto();
        }
        return null;
    }

}
