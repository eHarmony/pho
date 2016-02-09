package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.AbstractMatchFilter;

public class OpenMatchFilter extends AbstractMatchFilter {

	private static final int OPEN = 0;

	private static final int REOPENED_CLOSED_MATCH_BY_CANDIDATE = 10;

	private static final int REOPENED_CLOSED_MATCH_BY_USER = 11;

	private Set<Integer> statusSet = new HashSet<Integer>(Arrays.asList(OPEN, REOPENED_CLOSED_MATCH_BY_CANDIDATE, REOPENED_CLOSED_MATCH_BY_USER));

	private static final Logger logger = LoggerFactory.getLogger(OpenMatchFilter.class);

	@Override
	protected boolean processMatchSection(Map<String, Object> matchSection, MatchFeedRequestContext context) {

		// Verify the status of the match.
		Object closedStatusObj = matchSection.get(MatchFeedModel.MATCH.CLOSED_STATUS);

		Integer closedStatus = (closedStatusObj == null) ? OPEN : (int) closedStatusObj;
		logger.debug("Match closed Status for match Id -{} = {}", matchSection.get(MatchFeedModel.MATCH.ID), closedStatus);
		return statusSet.contains(closedStatus);
	}

}
