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
    
    /**
     * Should we use the new versions of comm next steps
     * @return <tt>true</tt> if we should use V2, <tt>false</tt> if we should use V1
     */
    public boolean isUseV2CommNextSteps();

    //platform and api request correlation info
    public Map<String, String> getRequestMetadata();
    
    public String getSortBy();
	public boolean isExcludeClosedMatches();
}
