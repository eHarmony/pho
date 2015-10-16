package com.eharmony.services.mymatchesservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;

@Service
public class UserMatchesFeedServiceImpl implements UserMatchesFeedService {

    private static final Logger logger = LoggerFactory.getLogger(UserMatchesFeedServiceImpl.class);
    @Resource
    private MatchStoreQueryRepository repository;
    
    @Override
    public List<MatchDataFeedItemDto> getUserMatches(Integer userId) {
        // TODO Auto-generated method stub
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

    /*@Override
    public List<MatchDataFeedItemDto> getUserMatches(Integer userId) {
        // TODO Auto-generated method stub
        return new ArrayList<MatchDataFeedItemDto>();
    }

    @Override
    public MatchDataFeedItemDto getUserMatch(Integer userId, Long matchId) {
        // TODO Auto-generated method stub
        return new MatchDataFeedItemDto();
    }*/

	

}
