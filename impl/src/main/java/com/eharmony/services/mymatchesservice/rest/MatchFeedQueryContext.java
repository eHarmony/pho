package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

public interface MatchFeedQueryContext {

    public long getUserId();
    public String getLocale();
    public int getStartPage();
    public int getPageSize();
    public Set<String> getStatuses();
    public boolean isViewHidden();
    public boolean isAllowedSeePhotos();
    
}
