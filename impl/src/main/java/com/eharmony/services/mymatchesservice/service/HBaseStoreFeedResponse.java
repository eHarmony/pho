package com.eharmony.services.mymatchesservice.service;

import java.util.Set;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

public class HBaseStoreFeedResponse {

    private Set<MatchDataFeedItemDto> hbaseStoreFeedItems;
    private final MatchStatusGroupEnum matchStatusGroup;
    private Throwable error;
    private boolean dataAvailable;

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

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public boolean isDataAvailable() {
        return dataAvailable;
    }

    public void setDataAvailable(boolean dataAvailable) {
        this.dataAvailable = dataAvailable;
    }
    

}
