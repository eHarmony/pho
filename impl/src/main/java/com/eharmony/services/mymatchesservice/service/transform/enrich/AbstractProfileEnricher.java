package com.eharmony.services.mymatchesservice.service.transform.enrich;

import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedSectionTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public abstract class AbstractProfileEnricher extends AbstractMatchFeedSectionTransformer {

	@Override
	protected String getMatchSectionName() {
		
		return MatchFeedModel.SECTIONS.PROFILE;
	}

}
