package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.AbstractMatchFilter;

public class MatchStatusFilter extends AbstractMatchFilter {

	private static final Logger logger = LoggerFactory.getLogger(MatchStatusFilter.class);

	@Override
	protected boolean processMatchSection(Map<String, Object> matchSection,
											MatchFeedRequestContext context) {

        Set<String> statuses = context.getMatchFeedQueryContext().getStatuses();
        if ((statuses == null) || statuses.isEmpty()) {
        	logger.warn("Statuses set={} is empty, rejecting match={}", statuses, matchSection);
            return false;
        }
        
        if(statuses.contains(MatchFeedModel.STATUS.ALL)){
        	logger.info("Status allows ALL, match accepted={}", matchSection);
        	return true;
        }

        Object statusObj = matchSection.get(MatchFeedModel.MATCH.STATUS);
        String status =  (statusObj == null) ? null
                                : statusObj.toString()
                                           .toLowerCase();

        boolean accepted =
            status != null && statuses.contains(status.toString());

    	logger.debug("MatchId={} accepted={} as match status={} {} one of requested statuses={}",
                  matchSection.get(MatchFeedModel.MATCH.ID), accepted, status, 
                  			accepted ? "is" : "is not", statuses);


        return accepted;	
   }

}
