package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.store.impl.AbstractJsonDataStoreImpl;


public class MatchDataFeedStore extends AbstractJsonDataStoreImpl<LegacyMatchDataFeedDto> {
	
    private static final Logger log = LoggerFactory.getLogger(MatchDataFeedStore.class);

    /**
    * Reads matches from Voldemort.
    *
    * @param   userId  feed userId
    *
    * @return  List of MatchDataFeedItemDto, or empty list if none found.
    */
    public LegacyMatchDataFeedDto getMatches(String userId) {


    	LegacyMatchDataFeedDto feed = fetchValue(userId);

    	if(feed != null){
	        log.debug("found {} matches in Voldemort for user {}",
	            					feed.getMatches().size(), userId);
    	}else{
    		log.debug("no matches in Voldemort for user {}", userId);
    		feed = new LegacyMatchDataFeedDto();
    	}
    	
        return feed;            

    }
}
