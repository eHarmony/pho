package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

public abstract class AbstractStoreFeedResponse {

    private final MatchStatusGroupEnum matchStatusGroup;
    private Throwable error;
    private boolean dataAvailable;

    public AbstractStoreFeedResponse(final MatchStatusGroupEnum matchStatusGroup) {
        this.matchStatusGroup = matchStatusGroup;
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
