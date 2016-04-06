package com.eharmony.services.mymatchesservice.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.google.common.base.Preconditions;

public class MatchFeedRequestContext {

    private LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper;
    final MatchFeedQueryContext matchFeedQueryContext;
    private Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> hbaseFeedItemsByStatusGroup = new HashMap<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>>();
    private LegacyMatchDataFeedDto redisFeed = null;
    
    /**
     * Whether we should the new version of comm next steps which does not contain localized text
     */
    private boolean useV2CommNextSteps = false;
    private boolean excludeClosedMatches = false;

    public MatchFeedRequestContext(final MatchFeedQueryContext matchFeedQueryContext) {
        Preconditions.checkNotNull(matchFeedQueryContext, "matchFeedQueryContext must not be null");
        this.matchFeedQueryContext = matchFeedQueryContext;
        this.useV2CommNextSteps = matchFeedQueryContext.isUseV2CommNextSteps();
        this.excludeClosedMatches = matchFeedQueryContext.isExcludeClosedMatches();
    }

    public MatchFeedRequestContext(final MatchFeedRequestContext matchFeedRequestContext) {
        Preconditions.checkNotNull(matchFeedRequestContext, "matchFeedRequestContext must not be null");
        this.matchFeedQueryContext = matchFeedRequestContext.getMatchFeedQueryContext();
        this.legacyMatchDataFeedDtoWrapper = matchFeedRequestContext.getLegacyMatchDataFeedDtoWrapper();
        this.hbaseFeedItemsByStatusGroup = matchFeedRequestContext.getHbaseFeedItemsByStatusGroup();
        this.redisFeed = matchFeedRequestContext.getRedisFeed();
        this.useV2CommNextSteps = matchFeedRequestContext.isUseV2CommNextSteps();
        this.excludeClosedMatches = matchFeedRequestContext.isExcludeClosedMatches();
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
    
    public void setRedisFeed(LegacyMatchDataFeedDto feed) {
        this.redisFeed = feed;
    }
    
    public LegacyMatchDataFeedDto getRedisFeed() {
		return redisFeed;
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

    public boolean isUseV2CommNextSteps() {

        return useV2CommNextSteps;
    }

    public void setUseV2CommNextSteps(boolean useV2CommNextSteps) {

        this.useV2CommNextSteps = useV2CommNextSteps;
    }
    
	public boolean isExcludeClosedMatches() {
		return excludeClosedMatches;
	}

	public void setExcludeClosedMatches(boolean excludeClosedMatches) {
		this.excludeClosedMatches = excludeClosedMatches;
	}
}
