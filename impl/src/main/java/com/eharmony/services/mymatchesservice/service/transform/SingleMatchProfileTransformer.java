package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Map;
import java.util.Set;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;

public class SingleMatchProfileTransformer implements IMatchTransformer{

	Set<String> fieldsToFilter;
	
	public SingleMatchProfileTransformer(Set<String> fields){
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
