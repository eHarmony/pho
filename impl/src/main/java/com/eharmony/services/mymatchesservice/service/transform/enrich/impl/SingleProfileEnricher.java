package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.enrich.AbstractProfileEnricher;

public class SingleProfileEnricher extends AbstractProfileEnricher {

	// See MDS.ProfileBatchRetriever
	@Override
	protected boolean processMatchSection(Map<String, Object> matchSection,
			MatchFeedRequestContext context) {

		if(context == null){
			return false;
		}
		
		context.getUserId();
		
		return true;
	}

}
