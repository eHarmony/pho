package com.eharmony.services.mymatchesservice.service.filter;

import java.util.ArrayList;
import java.util.List;

public class MatchFeedFilterChain {

	private List<IMatchFeedFilter> filters = new ArrayList<IMatchFeedFilter>();
	
	public void execute(MatchFeedFilterContext context){
		
		for(IMatchFeedFilter filter : filters){
			
			filter.processMatchFeed(context);
		}
	}
	
	public void addFilter(IMatchFeedFilter filter){
		filters.add(filter);
	}
	
}
