package com.eharmony.services.mymatchesservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eharmony.configuration.Configuration;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.repository.MatchDataFeedItemQueryRequest;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.MergeModeEnum;
import com.eharmony.services.mymatchesservice.service.merger.LegacyMatchDataFeedMergeStrategy;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

@Service
public class UserMatchesFeedServiceImpl implements UserMatchesFeedService {

    private static final Logger logger = LoggerFactory.getLogger(UserMatchesFeedServiceImpl.class);
    
    @Resource
    private MatchStoreQueryRepository repository;
    
    @Resource
    private MatchDataFeedStore voldemortStore;
    
    @Resource 
    private Configuration config;
    
    @Value("${feed.mergeMode}")
    private MergeModeEnum mergeMode;
    
    @Override
    public List<MatchDataFeedItemDto> getUserMatchesInternal(Integer userId) {
        MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest();
        request.setUserId(userId);
        try {
            Set<MatchDataFeedItemDto> matchDataFeeditems = repository.getMatchDataFeed(request);
            if(CollectionUtils.isNotEmpty(matchDataFeeditems)) {
                logger.debug("found {} matches for user {}", matchDataFeeditems.size(), userId);
                return new ArrayList<MatchDataFeedItemDto>(matchDataFeeditems);
            }
        } catch(Exception ex) {
            logger.warn("exception while fetching matches", ex);
            throw new RuntimeException(ex);
        }
        logger.debug("no matches found  for user {}", userId);
        return new ArrayList<MatchDataFeedItemDto>();
    }

    @Override
    public LegacyMatchDataFeedDto getUserMatches(Integer userId) {
    	
        MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest();
        request.setUserId(userId);
        try {
        	
        	LegacyMatchDataFeedMergeStrategy merger = 
        				LegacyMatchDataFeedMergeStrategy.getMergeInstance(mergeMode, repository, voldemortStore);
        	LegacyMatchDataFeedDto matchDataFeedItems = merger.merge(request);
            
        	if(matchDataFeedItems.getMatches().isEmpty()){
                logger.info("no matches found for user {}", userId);  
                return null;
        	}
        	
            logger.info("found {} matches for user {}", matchDataFeedItems.getMatches().size(), userId);
            return matchDataFeedItems;
            
            
        } catch(Exception ex) {
            logger.warn("exception while fetching matches", ex);
            throw new RuntimeException(ex);
        }
    }
    
//    protected LegacyMatchDataFeedDto mergeMatchFeed(MatchDataFeedQueryRequest request){
//    	
//    	LegacyMatchDataFeedDto feed = null;
//    	
//    	logger.info("mode is " + mergeMode);
//    	
//    	switch(mergeMode){
//    		
//    		case VOLDEMORT_ONLY:  		
//    		
//    			String userId = Integer.toString(request.getUserId());
//    			feed = voldemortStore.getMatches(userId);
//    			
//    			break;
//    		
//    		case VOLDEMORT_WITH_HBASE_PROFILE:
//
//    			try{
//    				Set<MatchDataFeedItemDto> hbaseFeed = repository.getMatchDataFeed(request);
//    				
//    				
//    			}catch(Exception ex){
//    				
//    			}
//    			
//    			break;
//    		
//    		case HBASE_ONLY:
//    			// TODO: fetch from HBase only.
//    			break;
//    	}
//    	
//    	return feed;
//    }

    @Override
    public MatchDataFeedItemDto getUserMatch(Integer userId, Long matchId) {
        MatchDataFeedItemQueryRequest request = new MatchDataFeedItemQueryRequest();
        request.setUserId(userId);
        request.setMatchId(matchId);
        try {
            MatchDataFeedItemDto matchDataFeeditem = repository.getMatchDataFeedItemDto(request);
            if(matchDataFeeditem != null) {
                logger.debug("found match for user {} and matchid {}", userId, matchId);
                return matchDataFeeditem;
            }
        } catch(Exception ex) {
            logger.warn("exception while fetching matches", ex);
            throw new RuntimeException(ex);
        }
        return null;
    }

}
