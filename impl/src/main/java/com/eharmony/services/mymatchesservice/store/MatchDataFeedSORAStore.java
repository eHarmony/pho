package com.eharmony.services.mymatchesservice.store;

import java.util.List;

import javax.annotation.Resource;
//import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;

@Component("soraStore")
public class MatchDataFeedSORAStore {
		
	private static final Logger logger = LoggerFactory.getLogger(MatchDataFeedSORAStore.class);

	@Resource
	private HibernateTemplate hibernateTemplate;
	
	@Transactional(readOnly=true)
	public MatchDo getMatch(long userId, long matchId){
		
		List<?> matches = hibernateTemplate.find("from MatchDo where matchId = ? AND (userId = ? OR candidateUserId = ?)", matchId, 
									userId, userId);
		
		logger.debug("searched EHMATCH for matchId {}, found {} matches.", matchId, matches.size());
		if(matches.size() > 0){
			return (MatchDo)matches.get(0);
		}
		
		return null;
	}
	
	@Transactional(readOnly=true)
	public MatchSummaryDo getMatchSummary(long userId, long matchId){
		
		List<?> matches = hibernateTemplate.find("from MatchSummaryDo where userId = ? and matchId = ?", userId, matchId);
		logger.debug("searched MATCH_SUMMARIES for userId {} matchId {}, found {} matches.", userId, matchId, matches.size());
		
		if(matches.size() > 0){
			return (MatchSummaryDo)matches.get(0);
		}
		
		return null;
	}
}
