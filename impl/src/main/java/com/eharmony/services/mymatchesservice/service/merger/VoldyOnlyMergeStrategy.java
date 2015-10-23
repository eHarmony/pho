package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

public class VoldyOnlyMergeStrategy extends LegacyMatchDataFeedMergeStrategy {

    private MatchDataFeedStore voldemortStore;
	
	public VoldyOnlyMergeStrategy(MatchDataFeedStore vstore) {
		this.voldemortStore = vstore;
	}

	@Override
	public LegacyMatchDataFeedDto merge(MatchDataFeedQueryRequest request) {
		
		String userId = Integer.toString(request.getUserId());
		return voldemortStore.getMatches(userId);
		
	}
}
