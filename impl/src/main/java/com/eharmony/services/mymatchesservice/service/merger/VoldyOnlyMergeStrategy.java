package com.eharmony.services.mymatchesservice.service.merger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

public class VoldyOnlyMergeStrategy extends LegacyMatchDataFeedMergeStrategy {

    private MatchDataFeedStore voldemortStore;
	
    private Logger log = LoggerFactory.getLogger(VoldyOnlyMergeStrategy.class);
    
	public VoldyOnlyMergeStrategy(MatchDataFeedStore vstore) {
		this.voldemortStore = vstore;
	}

	@Override
	public LegacyMatchDataFeedDto merge(MatchFeedRequestContext request) {
		
		long userId = request.getUserId();
		
		log.info("merging feed for userId {}", userId);
		return voldemortStore.getMatches(userId);
		
	}
}
