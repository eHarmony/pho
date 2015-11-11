package com.eharmony.services.mymatchesservice.service.filter.impl;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.service.filter.AbstractMatchFilter;
import com.eharmony.services.mymatchesservice.service.filter.MatchFeedFilterContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class MatchStatusFilter extends AbstractMatchFilter {

	private static final Logger logger = LoggerFactory.getLogger(MatchStatusFilter.class);

	@Override
	protected boolean processMatchSection(Map<String, Object> matchSection,
											MatchFeedFilterContext context) {

        Set<String> statuses = context.getMatchStatuses();
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
                  new Object[] {
                      matchSection.get(MatchFeedModel.MATCH.ID), accepted, status, accepted ? "is"
                                                                                     : "is not", statuses
                  });


        return accepted;	
   }

}
