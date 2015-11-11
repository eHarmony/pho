package com.eharmony.services.mymatchesservice.service.filter;

import java.util.Map;
import java.util.Set;


public class MatchFeedFilterContextBuilder {

	public static MatchFeedFilterContext buildFilterContext(
			Map<String, Map<String,  Map<String, Object>>> feed,
			Set<String> filterStatuses){
		
		MatchFeedFilterContext ctx = new MatchFeedFilterContext(feed);
		ctx.setMatchStatuses(filterStatuses);
		
		return ctx;
	}

}
