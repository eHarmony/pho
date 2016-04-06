package com.eharmony.services.mymatchesservice.rest;

import java.util.Map;
import java.util.Set;

public interface MatchFeedQueryContext {

    public long getUserId();
    public String getLocale();
    public int getStartPage();
    public int getPageSize();
    public Set<String> getStatuses();
    public boolean isViewHidden();
    public boolean isAllowedSeePhotos();
    public int getTeaserResultSize();

    //platform and api request correlation info
    public Map<String, String> getRequestMetadata();
    
    public String getSortBy();
	public boolean isExcludeClosedMatches();
}
