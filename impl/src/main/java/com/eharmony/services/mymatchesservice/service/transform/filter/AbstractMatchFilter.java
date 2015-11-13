package com.eharmony.services.mymatchesservice.service.transform.filter;

import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public abstract class AbstractMatchFilter extends AbstractMatchFeedTransformer {

	@Override
	protected String getMatchSectionName() {
		
		return MatchFeedModel.SECTIONS.MATCH;
	}

}
