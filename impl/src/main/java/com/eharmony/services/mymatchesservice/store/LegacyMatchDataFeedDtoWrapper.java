package com.eharmony.services.mymatchesservice.store;

public class LegacyMatchDataFeedDtoWrapper {

    private LegacyMatchDataFeedDto legacyMatchDataFeedDto;
    private boolean feedAvailable;
    private Throwable error;
    private final long userId;
    private int matchesCount;
    
    public int getMatchesCount() {
        return matchesCount;
    }
    public void setMatchesCount(int count) {
        this.matchesCount = count;
    }
    public LegacyMatchDataFeedDtoWrapper(final long userId) {
        this.userId = userId;
    }
    public LegacyMatchDataFeedDto getLegacyMatchDataFeedDto() {
        return legacyMatchDataFeedDto;
    }
    public void setLegacyMatchDataFeedDto(LegacyMatchDataFeedDto legacyMatchDataFeedDto) {
        this.legacyMatchDataFeedDto = legacyMatchDataFeedDto;
    }
    public boolean isFeedAvailable() {
        return feedAvailable;
    }
    public void setFeedAvailable(boolean feedAvailable) {
        this.feedAvailable = feedAvailable;
    }
    public Throwable getError() {
        return error;
    }
    public void setError(Throwable error) {
        this.error = error;
    }
    public long getUserId() {
        return userId;
    }
    
}
