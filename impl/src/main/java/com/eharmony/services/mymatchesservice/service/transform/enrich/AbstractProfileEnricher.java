package com.eharmony.services.mymatchesservice.service.transform.enrich;

import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public abstract class AbstractProfileEnricher extends AbstractMatchFeedTransformer {

	@Override
	protected String getMatchSectionName() {
		
		return MatchFeedModel.SECTIONS.PROFILE;
	}

}
