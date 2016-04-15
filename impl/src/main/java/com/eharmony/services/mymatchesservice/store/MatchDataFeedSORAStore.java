package com.eharmony.services.mymatchesservice.store;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.store.dao.MatchDao;
import com.eharmony.services.mymatchesservice.store.dao.MatchSummaryDao;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;

@Component("soraStore")
public class MatchDataFeedSORAStore {

	@Resource
	private MatchDao matchDao;
	
	@Resource
	private MatchSummaryDao matchSummaryDao;
		
	private static final Logger logger = LoggerFactory.getLogger(MatchDataFeedSORAStore.class);
	
    @Transactional
	public MatchDo getMatch(long matchId){
    	
    	try{
    		return matchDao.findByPrimaryKey(matchId);
    		
    	}catch(Exception ex){
    		logger.warn("Error getting EHMATCH record {}: {}", matchId, ex.getMessage());
    		return null;
    	}
    }
    
    @Transactional
	public MatchSummaryDo getMatchSummary(long userId, long matchId){
    	
    	try{
    		return matchSummaryDao.findMatchSummaryByUserAndMatch(userId, matchId);
    		
    	}catch(Exception ex){
    		logger.warn("Error getting MATCH_SUMMARIES record for user {} match {}: {}", userId, matchId, ex.getMessage());
    		return null;
    	}
    }
}
