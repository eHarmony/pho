package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.IMatchFilter;
import com.eharmony.singles.common.enumeration.MatchClosedStatusEnum;

public class ClosedMatchFilter  implements IMatchFilter{

	Logger logger = LoggerFactory.getLogger(ClosedMatchFilter.class);
	
	@Override
	public SingleMatchRequestContext filterSingleMatch(
			SingleMatchRequestContext context) {

		Map<String, Map<String, Object>> match = context.getSingleMatch();
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		
    	//only if risk-flagged/anonymized
		if(MatchClosedStatusEnum.LEGACY_CLOSED_NOT_VIEWABLE.toInt() == (Integer) matchSection.get(MatchFeedModel.MATCH.CLOSED_STATUS)){
		
    		logger.debug("user requested closed match {}, filtering out.", context.getQueryContext().getMatchId());
    		context.setSingleMatch(null);
    	}
    	
    	return context;
	}

}
