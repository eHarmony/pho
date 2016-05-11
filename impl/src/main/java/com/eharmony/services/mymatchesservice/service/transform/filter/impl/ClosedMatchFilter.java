package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.singles.common.status.MatchStatusUtilities;

public class ClosedMatchFilter  implements IMatchTransformer{

	Logger logger = LoggerFactory.getLogger(ClosedMatchFilter.class);
	
	@Override
	public SingleMatchRequestContext processSingleMatch(
			SingleMatchRequestContext context) {

		Map<String, Map<String, Object>> match = context.getSingleMatch();
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		
    	if (MatchStatusUtilities.isClosed(matchSection)) {
    		
    		logger.info("user requested closed match {}, filtering out.", context.getQueryContext().getMatchId());
    		context.setSingleMatch(null);
    	}
    	
    	return context;
	}

}
