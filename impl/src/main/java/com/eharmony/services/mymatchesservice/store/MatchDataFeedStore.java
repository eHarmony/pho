package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.eharmony.datastore.store.impl.AbstractJsonDataStoreImpl;

public class MatchDataFeedStore extends AbstractJsonDataStoreImpl<LegacyMatchDataFeedDto> {
	
    private static final Logger logger = LoggerFactory.getLogger(MatchDataFeedStore.class);

    /**
    * Reads matches from Voldemort.
    *
    * @param   userId  feed userId
    *
    * @return  List of MatchDataFeedItemDto, or empty list if none found.
    */
    public LegacyMatchDataFeedDto getMatches(long userId) {


        long startTime = System.currentTimeMillis();
    	LegacyMatchDataFeedDto feed = fetchValue(String.valueOf(userId));

    	if(feed != null){
    	    logger.debug("found {} matches in Voldemort for user {}",
	            					feed.getMatches().size(), userId);
    	}else{
    	    logger.debug("no matches in Voldemort for user {}", userId);
    		return null;
    	}
    	long endTime = System.currentTimeMillis();
        logger.info("Total time to get the feed from voldy is {} MS", endTime - startTime);
        return feed;            

    }
    
    public Observable<LegacyMatchDataFeedDto> getMatchesObservable(long userId) {
        return Observable.defer(() -> Observable.just(getMatches(userId)));
    }
}
