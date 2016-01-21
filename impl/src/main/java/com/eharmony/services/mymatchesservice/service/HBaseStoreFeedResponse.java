package com.eharmony.services.mymatchesservice.service;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

public class HBaseStoreFeedResponse extends AbstractStoreFeedResponse{

    private Set<MatchDataFeedItemDto> hbaseStoreFeedItems;

    public HBaseStoreFeedResponse(final MatchStatusGroupEnum matchStatusGroup) {
    	super(matchStatusGroup);
    }

    public Set<MatchDataFeedItemDto> getHbaseStoreFeedItems() {
        return hbaseStoreFeedItems;
    }

    public void setHbaseStoreFeedItems(Set<MatchDataFeedItemDto> hbaseStoreFeedItems) {
        this.hbaseStoreFeedItems = hbaseStoreFeedItems;
    }
}
