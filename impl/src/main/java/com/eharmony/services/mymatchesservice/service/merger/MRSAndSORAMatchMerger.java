package com.eharmony.services.mymatchesservice.service.merger;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.MRSDto;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchDoToMatchDataFeedItemDtoMapper;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;
import com.eharmony.singles.common.enumeration.FastTrackAvailableEnum;
import com.eharmony.singles.common.enumeration.FastTrackStatusEnum;
import com.eharmony.singles.common.enumeration.IcebreakerStateEnum;
import com.eharmony.singles.common.enumeration.MatchArchiveStatusEnum;
import com.eharmony.singles.common.enumeration.MatchDisplayTabEnum;
import com.eharmony.singles.common.enumeration.MatchInitializerEnum;

public class MRSAndSORAMatchMerger {

	private MatchDoToMatchDataFeedItemDtoMapper mapper = new MatchDoToMatchDataFeedItemDtoMapper();
	
	private static final Logger log = LoggerFactory.getLogger(MRSAndSORAMatchMerger.class);

	public void mergeMatch(SingleMatchRequestContext request){
		
    	long userId = request.getQueryContext().getUserId();
		long matchId = request.getQueryContext().getMatchId();
		String matchIdAsStr = Long.toString(matchId);
		
		MatchDo matchDo = request.getMatchDo();
		MRSDto mrsDto = request.getMrsDto();
		MatchSummaryDo matchSummaryDo = request.getMatchSummaryDo();

		// If we have EHMATCHES record, use that only
		if(matchDo != null){
			
			LegacyMatchDataFeedDtoWrapper match = buildMatchFromEHMatches(userId, matchId, matchDo);
			request.setSingleMatch(match.getLegacyMatchDataFeedDto().getMatches().get(matchIdAsStr));
			
		}else{
		
			// If we have MATCH_SUMMARIES record, merge with MRS
			if(mrsDto != null && matchSummaryDo != null){
				
				log.info("Building match from merging matchSummaries + MRS for userId {} matchId {}", userId, matchId);
				
				Map<String, Map<String, Map<String, Object>>> oneMatch = new HashMap<>();
				buildMatchFromMRSDto(oneMatch, matchId, mrsDto);
				
				// Create empty Profile section, to be enriched later
				Map<String, Object> profileSection = new HashMap<String, Object>();
				profileSection.put(MatchFeedModel.PROFILE.USERID, matchSummaryDo.getCandidateUserId());
				oneMatch.get(matchIdAsStr).put(MatchFeedModel.SECTIONS.PROFILE, profileSection);		
	
				buildCommFromMatchSummaries(oneMatch, matchId, matchSummaryDo);

				request.setSingleMatch(oneMatch.get(matchIdAsStr));
			}
		}
	}
	
	private LegacyMatchDataFeedDtoWrapper buildMatchFromEHMatches(long userId, long matchId, MatchDo matchDo){
		
		log.info("Building match from EHMATCHES for userId {} matchId {}", userId, matchId);
		
		Map<String,Map<String, Object>> oneMatchContent = mapper.transform(userId, matchDo);
		
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
		matchSection.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchDto.getOneWay());
		matchSection.put(MatchFeedModel.MATCH.RELAXED, matchDto.getRelaxed());
		matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDto.getUserId());
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDto.getMatchedUserId());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDto.getDistance());
		matchSection.put(MatchFeedModel.MATCH.ID, matchId);
		matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDto.getDeliveryDate());
		
		buildMatchSectionDefaultValues(matchSection);
		 
		oneMatchSection.put(MatchFeedModel.SECTIONS.MATCH, matchSection);
		
		oneMatch.put(Long.toString(matchId), oneMatchSection);
	}
	
	private void buildMatchSectionDefaultValues(Map<String, Object> matchSection){

		matchSection.put(MatchFeedModel.MATCH.STATUS, "new");
		matchSection.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, IcebreakerStateEnum.NONE.toInt());
		matchSection.put(MatchFeedModel.MATCH.ARCHIVE_STATUS, MatchArchiveStatusEnum.OPEN.toInt());
		matchSection.put(MatchFeedModel.MATCH.COMM_LAST_SENT, null); 
		matchSection.put(MatchFeedModel.MATCH.MATCH_COMM_LAST_SENT, null);
		matchSection.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, false);
		matchSection.put(MatchFeedModel.MATCH.NEW_MESSAGE_COUNT, 0);
		matchSection.put(MatchFeedModel.MATCH.INITIALIZER, MatchInitializerEnum.UNINITIALIZED.toInt());
		matchSection.put(MatchFeedModel.MATCH.DISPLAY_TAB, MatchDisplayTabEnum.NEW_TAB.toInt());
		matchSection.put(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE, null);
		matchSection.put(MatchFeedModel.MATCH.COMM_STARTED_DATE, null);
		matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, FastTrackAvailableEnum.AVAILABLE_TO_BOTH.toInt());
		matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STATUS, FastTrackStatusEnum.NONE.toInt());
		matchSection.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, IcebreakerStateEnum.NONE.toInt());
		matchSection.put(MatchFeedModel.MATCH.LAST_NUDGE_DATE, null);
		matchSection.put(MatchFeedModel.MATCH.MATCH_CLOSED_COUNT, null);
		matchSection.put(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB, MatchDisplayTabEnum.NEW_TAB.toInt());
		matchSection.put(MatchFeedModel.MATCH.NEW_MATCH_MESSAGE_COUNT, 0);
		matchSection.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, null);
		matchSection.put(MatchFeedModel.MATCH.STAGE, 27);
		matchSection.put(MatchFeedModel.MATCH.TURN_OWNER, 0);

	}
 
	
	private void buildCommFromMatchSummaries(
			Map<String, Map<String, Map<String, Object>>> oneMatch, long matchId,
			MatchSummaryDo comm) {
				
		Map<String, Object> commSection = new HashMap<>();
		commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, comm.getLastCommDate());
		
		// Default values
		commSection.put(MatchFeedModel.COMMUNICATION.NEW_MESSAGE_COUNT, 0);
		commSection.put(MatchFeedModel.COMMUNICATION.VIEWED_PROFILE, false);
				
		oneMatch.get(Long.toString(matchId)).put(MatchFeedModel.SECTIONS.COMMUNICATION, commSection);		
	}
}
