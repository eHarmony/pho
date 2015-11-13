package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.eharmony.datastore.store.impl.AbstractJsonDataStoreImpl;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

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
    
    public Observable<LegacyMatchDataFeedDto> getMatchesObservableSafe(MatchFeedRequestContext request) {
        Observable<LegacyMatchDataFeedDto> voldyFeed =  Observable.defer(() -> Observable.just(getMatches(request.getUserId())));
        voldyFeed.onErrorReturn(ex -> {
            logger.warn("Exception while fetching data from voldemort for user {} and returning null feed object for safe method", request.getUserId(), ex);
            return null;
        });
        return voldyFeed;
    }
}
