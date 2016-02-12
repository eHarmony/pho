package com.eharmony.services.mymatchesservice.service;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

public class HBaseStoreFeedResponse extends AbstractStoreFeedResponse{

    private Set<MatchDataFeedItemDto> hbaseStoreFeedItems;
    private final MatchStatusGroupEnum matchStatusGroup;

    public HBaseStoreFeedResponse(final MatchStatusGroupEnum matchStatusGroup) {
    	this.matchStatusGroup = matchStatusGroup;
    }

    public Set<MatchDataFeedItemDto> getHbaseStoreFeedItems() {
        return hbaseStoreFeedItems;
    }

    public void setHbaseStoreFeedItems(Set<MatchDataFeedItemDto> hbaseStoreFeedItems) {
        this.hbaseStoreFeedItems = hbaseStoreFeedItems;
    }
    
    public MatchStatusGroupEnum getMatchStatusGroup() {
        return matchStatusGroup;
    }
}
