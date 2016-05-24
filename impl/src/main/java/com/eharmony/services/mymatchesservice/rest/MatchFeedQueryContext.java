package com.eharmony.services.mymatchesservice.rest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eharmony.datastore.query.criterion.Ordering;

public interface MatchFeedQueryContext {

    public long getUserId();
    public String getLocale();
    public int getStartPage();
    public int getPageSize();
    public Set<String> getStatuses();
    public boolean isViewHidden();
    public boolean isAllowedSeePhotos();
    public int getTeaserResultSize();
    public MatchFeedSearchAndFilterCriteria getSearchFilterCriteria();


    //platform and api request correlation info
    public Map<String, String> getRequestMetadata();
    
    public List<Ordering> getOrderings();
	public boolean isExcludeClosedMatches();
}
