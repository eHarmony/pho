package com.eharmony.services.mymatchesservice.service.transform;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

@Component
public class HBASEToLegacyFeedTransformer {

	public void transformHBASEFeedToLegacyFeedIfRequired(MatchFeedRequestContext context) {
		// this mapping is required only when there is no feed from voldy and we got ALL info from HBASE as a fallback
    	if(!context.isFallbackRequest()) {
    		return;
    	}
    	
    	// transform the hbase data
    	LegacyMatchDataFeedDto legacyMatchFeedDto = LegacyMatchFeedTransformer.transform(context);
    	
    	// set it as though it was voldy data
    	LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(context.getUserId());
    	legacyMatchDataFeedDtoWrapper.setFeedAvailable(true);
    	legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(legacyMatchFeedDto);
    	context.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
    	
    	// clear up hbase records
    	context.setNewStoreFeed(new HashSet<MatchDataFeedItemDto>());
	}
}
