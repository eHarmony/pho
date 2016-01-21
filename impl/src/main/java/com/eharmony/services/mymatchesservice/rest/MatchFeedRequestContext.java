package com.eharmony.services.mymatchesservice.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.google.common.base.Preconditions;

public class MatchFeedRequestContext {

    private LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper;
    private FeedMergeStrategyType feedMergeType;
    final MatchFeedQueryContext matchFeedQueryContext;
    private boolean isFallbackRequest;
    private Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> hbaseFeedItemsByStatusGroup = new HashMap<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>>();
    private Map<MatchStatusGroupEnum, LegacyMatchDataFeedDto> redisFeedItemsByStatusGroup = new HashMap<MatchStatusGroupEnum, LegacyMatchDataFeedDto>();

    public MatchFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
    }

    public MatchFeedRequestContext(final MatchFeedRequestContext matchFeedRequestContext) {
        Preconditions.checkNotNull(matchFeedRequestContext, "matchFeedRequestContext must not be null");
        this.matchFeedQueryContext = matchFeedRequestContext.getMatchFeedQueryContext();
        this.legacyMatchDataFeedDtoWrapper = matchFeedRequestContext.getLegacyMatchDataFeedDtoWrapper();
        this.feedMergeType = matchFeedRequestContext.getFeedMergeType();
        this.isFallbackRequest = matchFeedRequestContext.isFallbackRequest();
        this.hbaseFeedItemsByStatusGroup = matchFeedRequestContext.getHbaseFeedItemsByStatusGroup();
    }

    public Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> getHbaseFeedItemsByStatusGroup() {
        return hbaseFeedItemsByStatusGroup;
    }

    public void setHbaseFeedItemsByStatusGroup(
            Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> hbaseFeedItemsByStatusGroup) {
        this.hbaseFeedItemsByStatusGroup = hbaseFeedItemsByStatusGroup;
    }
    
    public void putFeedItemsInMapByStatusGroup(MatchStatusGroupEnum statusGroup, Set<MatchDataFeedItemDto> feedItems) {

        if (CollectionUtils.isNotEmpty(feedItems)) {
            hbaseFeedItemsByStatusGroup.put(statusGroup, feedItems);
        }
    }
    
    public void putFeedItemsInMapByStatusGroup(MatchStatusGroupEnum statusGroup, LegacyMatchDataFeedDto feed) {

        redisFeedItemsByStatusGroup.put(statusGroup, feed);
    }

    public boolean isFallbackRequest() {
        return isFallbackRequest;
    }

    public void setFallbackRequest(boolean isFallbackRequest) {
        this.isFallbackRequest = isFallbackRequest;
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
    
    public boolean hasHbaseMatches() {
        if (MapUtils.isEmpty(hbaseFeedItemsByStatusGroup)) {
            return false;
        }

        for (Entry<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedEntry : hbaseFeedItemsByStatusGroup.entrySet()) {
            if (CollectionUtils.isNotEmpty(feedEntry.getValue())) {
                return true;
            }
        }
        return false;
    }
    
    public Set<MatchDataFeedItemDto> getAggregateHBaseFeedItems() {
        Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedItemsByGroups = this
                .getHbaseFeedItemsByStatusGroup();
        Set<MatchDataFeedItemDto> storeFeedItems = new HashSet<MatchDataFeedItemDto>();
        if (MapUtils.isNotEmpty(feedItemsByGroups)) {
            feedItemsByGroups.forEach((k, v) -> {
                storeFeedItems.addAll(v);
            });
        }
        return storeFeedItems;
    }
    
}
