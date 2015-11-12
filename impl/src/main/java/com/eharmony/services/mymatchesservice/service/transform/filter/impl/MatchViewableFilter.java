package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.AbstractMatchFilter;
import com.eharmony.singles.common.status.MatchStatusUtilities;

public class MatchViewableFilter extends AbstractMatchFilter {

	private static final Logger logger = LoggerFactory.getLogger(MatchViewableFilter.class);

	@Override
	protected boolean processMatchSection(Map<String, Object> matchSection,
			MatchFeedRequestContext context) {
		
		//	null = 0 = OPEN so viewable
		//	4 = CLOSED_NOT_VIEWABLE, Not viewable
		//	If isUser and CLOSED_VIEWABLE_BY_CANDIDATE, not viewable
		//	If candidate and CLOSED_VIEWABLE_BY_USER, not viewable
		//	otherwise viewable
		
		boolean accepted = !MatchStatusUtilities.isNoViewable(matchSection);
        logger.debug("MatchId={} accepted={}", matchSection.get(MatchFeedModel.MATCH.ID), accepted);

		return accepted;
	}

}
