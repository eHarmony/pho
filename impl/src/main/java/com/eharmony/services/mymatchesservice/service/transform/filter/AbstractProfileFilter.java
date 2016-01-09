package com.eharmony.services.mymatchesservice.service.transform.filter;

import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedSectionTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public abstract class AbstractProfileFilter extends AbstractMatchFeedSectionTransformer {

	@Override
	protected String getMatchSectionName() {

		return MatchFeedModel.SECTIONS.PROFILE;
	}

}
