package com.eharmony.services.mymatchesservice.service.filter;

import java.util.Map;
import java.util.Set;

public class MatchFeedFilterContext {

	private Set<String> matchStatuses;	
	
	// Key=matchId, Value=MatchInfoModel 
	private Map<String, Map<String,  Map<String, Object>>> feedMap;
	

	public MatchFeedFilterContext(Map<String, Map<String, Map<String, Object>>> feedMap) {
		this.feedMap = feedMap;
	}

	public Set<String> getMatchStatuses() {
		return matchStatuses;
	}

	public void setMatchStatuses(Set<String> matchStatuses) {
		this.matchStatuses = matchStatuses;
	} 
	
	public Map<String, Map<String, Map<String, Object>>> getFeedMap() {
		return feedMap;
	}

}
