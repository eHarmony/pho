package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.AbstractMatchFilter;

public class MatchDeliveredFilter extends AbstractMatchFilter {

	private static final Logger logger = LoggerFactory.getLogger(MatchDeliveredFilter.class);

	@Override
	protected boolean processMatchSection(Map<String, Object> matchSection,
											MatchFeedRequestContext context) {

	    Object deliveredDate = matchSection.get(MatchFeedModel.MATCH.DELIVERED_DATE);
	
	    boolean accepted = (deliveredDate != null);
		
    	logger.debug("MatchId={} accepted={} as match deliveredDate={}",
                  matchSection.get(MatchFeedModel.MATCH.ID), accepted, deliveredDate);
		
	    return accepted;
	}

}
