package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public class FeedMergeStrategyManager {

	private static FeedMergeStrategy VOLDY_WITH_PROFILE_MERGE_STRATEGY = new DefaultFeedMergeStrategyImpl();
    
    public static FeedMergeStrategy getMergeStrategy(MatchFeedRequestContext requestContext){
    	
    	switch(requestContext.getFeedMergeType()){
    	
    	case VOLDY_FEED_WITH_PROFILE_MERGE:
    		
    		return VOLDY_WITH_PROFILE_MERGE_STRATEGY;
    		
    	case VOLDY_FEED_ONLY: 
    	case VOLDY_FEED_WITH_FULL_MERGE: 
    	case HBASE_FEED_ONLY: 
    	case HBASE_FEED_WITH_MATCH_MERGE:
    		
    		throw new UnsupportedOperationException("Unsupported Merge Strategy: " + requestContext.getFeedMergeType());
    	}
    	
    	return VOLDY_WITH_PROFILE_MERGE_STRATEGY;
    }
}