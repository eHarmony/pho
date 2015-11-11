package com.eharmony.services.mymatchesservice.service.filter;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public abstract class AbstractMatchFilter extends AbstractMatchFeedFilter {

	@Override
	protected String getMatchSectionName() {
		
		return MatchFeedModel.SECTIONS.MATCH;
	}

}
