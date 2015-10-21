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

@Service
public class UserMatchesFeedServiceImpl implements UserMatchesFeedService {

    private static final Logger logger = LoggerFactory.getLogger(UserMatchesFeedServiceImpl.class);
    
    @Resource
    private MatchStoreQueryRepository repository;
    
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
    public List<MatchDataFeedItemDto> getUserMatches(Integer userId) {
        MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest();
        request.setUserId(userId);
        try {
        	
            Set<MatchDataFeedItemDto> matchDataFeeditems = mergeMatchFeed(request);
            
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
    
    
    protected Set<MatchDataFeedItemDto> mergeMatchFeed(MatchDataFeedQueryRequest request){
    	
    	switch(mergeMode){
    		
    		case VOLDEMORT_ONLY:  		
    		// TODO: fetch feed from voldy.
    			break;
    		
    		case VOLDEMORT_WITH_HBASE_PROFILE:
    		// TODO: fetch from voldy, merge profile from HBase
    			break;
    		
    		case HBASE_ONLY:
    			// TODO: fetch from HBase only.
    			break;
    	}
    	
    	return null;
    }

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
