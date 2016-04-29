package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;
import java.util.Set;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class SingleMatchProfileFilter implements IMatchTransformer{

	Set<String> fieldsToFilter;
	
	public SingleMatchProfileFilter(Set<String> fields){
		this.fieldsToFilter = fields;
	}
	
	@Override
	public SingleMatchRequestContext processSingleMatch(
			SingleMatchRequestContext context) {

		Map<String, Object> profileSection = context.getSingleMatch().get(MatchFeedModel.SECTIONS.PROFILE);
		
		fieldsToFilter.forEach(field -> {
			profileSection.remove(field);
		});
		
		return context;
	}

}
