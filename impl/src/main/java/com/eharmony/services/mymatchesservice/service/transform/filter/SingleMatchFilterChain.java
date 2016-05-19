package com.eharmony.services.mymatchesservice.service.transform.filter;

import java.util.ArrayList;
import java.util.List;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;

public class SingleMatchFilterChain {

	private List<IMatchFilter> filters = new ArrayList<IMatchFilter>();
	
	public SingleMatchFilterChain(final List<IMatchFilter> filters){
		this.filters = filters;
	}
	
	public SingleMatchRequestContext execute(SingleMatchRequestContext context){
		
		for(IMatchFilter filter : filters){
			
			filter.filterSingleMatch(context);
			if(context.getSingleMatch() == null){
				break;
			}
		}
		
		return context;
	}
	
	public void addFilter(IMatchFilter transformer){
		filters.add(transformer);
	}
}
