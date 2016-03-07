package com.eharmony.services.mymatchesservice.store;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;

import com.eharmony.services.mymatchesservice.service.BasicStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.dao.MatchDao;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchDoToMatchDataFeedItemDtoMapper;

@Component("soraStore")
public class MatchDataFeedSORAStore {

	@Resource
	private MatchDao matchDao;
	
	private MatchDoToMatchDataFeedItemDtoMapper mapper = new MatchDoToMatchDataFeedItemDtoMapper();
	
	private static final Logger logger = LoggerFactory.getLogger(MatchDataFeedSORAStore.class);
	
    @Transactional
	public LegacyMatchDataFeedDtoWrapper getSingleUserMatchSafe(
			BasicStoreFeedRequestContext request) {
		
    	long userId = request.getMatchFeedQueryContext().getUserId();
		long matchId = request.getMatchFeedQueryContext().getMatchId();
		
		logger.info("Fetch match {} from SORA", matchId);
		
		try{
		
			MatchDo match = null;//matchDao.findByPrimaryKey(matchId);
			if(match == null){
				logger.warn("no match found in SORA for matchId {}", matchId);
				return null;
			}
	
			Map<String,Map<String, Object>> oneMatchContent = mapper.transform(match);
			Map<String, Map<String,Map<String, Object>>> oneMatch = new HashMap<>();
			oneMatch.put(Long.toString(matchId), oneMatchContent);
			
			LegacyMatchDataFeedDtoWrapper result= new LegacyMatchDataFeedDtoWrapper(userId);
			LegacyMatchDataFeedDto dto = new LegacyMatchDataFeedDto();
			dto.setMatches(oneMatch);
			result.setLegacyMatchDataFeedDto(dto);
			
			return result;

		}catch(Throwable ex){
			
			logger.warn("Exception while getting match {} : {}", matchId, ex.getMessage());
			return null;
		}
		
	}
}
