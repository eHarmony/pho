package com.eharmony.services.mymatchesservice.service.merger;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.MRSDto;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchDoToMatchDataFeedItemDtoMapper;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;

@Component("mrsAndSoraMatchMerger")
public class MRSAndSORAMatchMerger {

	private MatchDoToMatchDataFeedItemDtoMapper mapper = new MatchDoToMatchDataFeedItemDtoMapper();

	public LegacyMatchDataFeedDtoWrapper mergeMatch(MatchFeedRequestContext request, 
									MRSDto mrsDto, MatchDo matchDo, MatchSummaryDo matchSummaryDo){
		
    	long userId = request.getMatchFeedQueryContext().getUserId();
		long matchId = request.getMatchFeedQueryContext().getMatchId();

		// If we have EHMATCHES record, use that only
		if(matchDo != null){
			return buildMatchFromEHMatches(userId, matchId, matchDo);
		}
		
		// If we have MATCH_SUMMARIES record, merge with MRS
		if(mrsDto != null && matchSummaryDo != null){
			LegacyMatchDataFeedDtoWrapper resultWrapper = new LegacyMatchDataFeedDtoWrapper(userId);
			LegacyMatchDataFeedDto dto = new LegacyMatchDataFeedDto();
			
			Map<String, Map<String, Map<String, Object>>> oneMatch = new HashMap<>();
			buildMatchFromMRSDto(oneMatch, matchId, mrsDto);
			buildCommFromMatchSummaries(oneMatch, matchId, matchSummaryDo);
			
			dto.setMatches(oneMatch);
			
			resultWrapper.setLegacyMatchDataFeedDto(dto);
			resultWrapper.setFeedAvailable(true);
			resultWrapper.setMatchesCount(oneMatch.size());
			
			request.setLegacyMatchDataFeedDtoWrapper(resultWrapper);
			
			return resultWrapper;
		}
		return null;
	}
	
	private LegacyMatchDataFeedDtoWrapper buildMatchFromEHMatches(long userId, long matchId, MatchDo matchDo){
		
		Map<String,Map<String, Object>> oneMatchContent = mapper.transform(matchDo);
		Map<String, Map<String,Map<String, Object>>> oneMatch = new HashMap<>();
		oneMatch.put(Long.toString(matchId), oneMatchContent);
		
		LegacyMatchDataFeedDtoWrapper resultWrapper = new LegacyMatchDataFeedDtoWrapper(userId);
		LegacyMatchDataFeedDto dto = new LegacyMatchDataFeedDto();
		dto.setMatches(oneMatch);
		dto.setTotalMatches(oneMatch.size());
		
		resultWrapper.setLegacyMatchDataFeedDto(dto);
		resultWrapper.setFeedAvailable(true);
		resultWrapper.setMatchesCount(oneMatch.size());
		
		return resultWrapper;

	}

	private void buildMatchFromMRSDto(
			Map<String, Map<String, Map<String, Object>>> oneMatch, long matchId,
															MRSDto matchDto) {
		
		Map<String, Map<String, Object>> oneMatchSection = new HashMap<>();
		
		Map<String, Object> matchSection = new HashMap<>();
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDto.getMatchedUserId());
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchDto.getCloseFlag());
		matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDto.getDeliveryDate());
		matchSection.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchDto.getOneWay());
		matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDto.getUserId());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDto.getDistance());
		
		oneMatchSection.put(MatchFeedModel.SECTIONS.MATCH, matchSection);
		
		oneMatch.put(Long.toString(matchId), oneMatchSection);
	}
	
	private void buildCommFromMatchSummaries(
			Map<String, Map<String, Map<String, Object>>> oneMatch, long matchId,
			MatchSummaryDo comm) {
				
		Map<String, Object> commSection = new HashMap<>();
		commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, comm.getLastCommDate());
				
		oneMatch.get(Long.toString(matchId)).put(MatchFeedModel.SECTIONS.COMMUNICATION, commSection);		
	}
}
