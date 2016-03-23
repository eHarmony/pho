package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

import com.eharmony.services.mymatchesservice.rest.internal.DataServiceStateEnum;

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

    // Internal test flags.
    public DataServiceStateEnum getVoldyState();
}
