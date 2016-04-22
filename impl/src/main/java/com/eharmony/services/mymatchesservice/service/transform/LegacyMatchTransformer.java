package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Map;

import com.eharmony.datastore.model.MatchDataFeedItemDto;

public class LegacyMatchTransformer extends LegacyMatchFeedTransformer{

	public Map<String, Map<String, Object>> transform(MatchDataFeedItemDto oneMatch){
		return buildLegacyFeedItem(oneMatch);
	}
}
