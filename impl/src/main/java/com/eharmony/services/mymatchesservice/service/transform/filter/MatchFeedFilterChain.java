package com.eharmony.services.mymatchesservice.service.transform.filter;

import java.util.ArrayList;
import java.util.List;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public class MatchFeedFilterChain {

	private List<IMatchFeedFilter> filters = new ArrayList<IMatchFeedFilter>();
	
	public MatchFeedRequestContext execute(MatchFeedRequestContext context){
		
		for(IMatchFeedFilter filter : filters){
			
			filter.processMatchFeed(context);
		}
		
		return context;
	}
	
	public void addFilter(IMatchFeedFilter filter){
		filters.add(filter);
	}
	
}
