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
import com.eharmony.singles.common.enumeration.IcebreakerStateEnum;
import com.eharmony.singles.common.profile.BasicPublicProfileDto;
import com.eharmony.singles.common.status.MatchStatusUtilities;

public class MRSAndSORAMatchMerger {

	private MatchDoToMatchDataFeedItemDtoMapper mapper = new MatchDoToMatchDataFeedItemDtoMapper();
	
	private static final Logger log = LoggerFactory.getLogger(MRSAndSORAMatchMerger.class);
	
	private static final int GENDER_FEMALE = 2;
	private static final String LOCALE_CP = "en_US_10";

	public void mergeMatch(SingleMatchRequestContext request){
		
    	long userId = request.getQueryContext().getUserId();
		long matchId = request.getQueryContext().getMatchId();
		String matchIdAsStr = Long.toString(matchId);
		
		MatchDo matchDo = request.getMatchDo();
		MRSDto mrsDto = request.getMrsDto();
		MatchSummaryDo matchSummaryDo = request.getMatchSummaryDo();
		BasicPublicProfileDto userInfo = request.getPublicProfileDto();

		// If we have EHMATCHES record, use that only
		if(matchDo != null && request.getQueryContext().isSORAEnabled()){

			log.debug("Building match from EHMATCHES for userId {} matchId {}", userId, matchId);

			LegacyMatchDataFeedDtoWrapper match = buildMatchFromEHMatches(userId, matchId, matchDo);
			request.setSingleMatch(match.getLegacyMatchDataFeedDto().getMatches().get(matchIdAsStr));
			
		}else if(mrsDto != null){
							
			log.debug("Building match from merging matchSummaries + MRS for userId {} matchId {}", userId, matchId);
			
			if(matchSummaryDo == null){
				matchSummaryDo = new MatchSummaryDo();
				matchSummaryDo.setOwnerIsUser(ownerIsUser(userInfo, mrsDto.getMatchedUserId()));
				if(matchSummaryDo.getOwnerIsUser()){
					matchSummaryDo.setCandidateUserId(mrsDto.getMatchedUserId());
				}else{
					matchSummaryDo.setCandidateUserId(mrsDto.getUserId());
				}
			}
			
			Map<String, Map<String, Map<String, Object>>> oneMatch = new HashMap<>();
			buildMatch(oneMatch, matchSummaryDo, matchId, mrsDto);
			
			// Create empty Profile section, to be enriched later
			Map<String, Object> profileSection = new HashMap<String, Object>();
			profileSection.put(MatchFeedModel.PROFILE.USERID, matchSummaryDo.getCandidateUserId());
			oneMatch.get(matchIdAsStr).put(MatchFeedModel.SECTIONS.PROFILE, profileSection);		

			buildCommFromMatchSummaries(oneMatch, matchId, matchSummaryDo);

			request.setSingleMatch(oneMatch.get(matchIdAsStr));
		}
	}
	
	private boolean ownerIsUser(BasicPublicProfileDto profileInfo, long matchedUserId){
		
		if(LOCALE_CP.equals(profileInfo.getLocale())){
			
			// for CP, lower digit is owner.
			return(profileInfo.getUserId() < matchedUserId);
			
		}else{
			return profileInfo.getGender() == GENDER_FEMALE;
		}
	}
	
	private LegacyMatchDataFeedDtoWrapper buildMatchFromEHMatches(long userId, long matchId, MatchDo matchDo){
		
		log.debug("Building match from EHMATCHES for userId {} matchId {}", userId, matchId);
		
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

	private void buildMatch(
			Map<String, Map<String, Map<String, Object>>> oneMatch, MatchSummaryDo matchSummaryDo, long matchId,
															MRSDto matchDto) {
		
		Map<String, Map<String, Object>> oneMatchSection = new HashMap<>();
		
		Map<String, Object> matchSection = new HashMap<>();
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDto.getMatchedUserId());
		matchSection.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchDto.getOneWay());
		matchSection.put(MatchFeedModel.MATCH.RELAXED, matchDto.getRelaxed());
		matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDto.getUserId());
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDto.getMatchedUserId());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDto.getDistance());
		matchSection.put(MatchFeedModel.MATCH.ID, matchId);
		matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDto.getDeliveryDate());
		matchSection.put(MatchFeedModel.MATCH.IS_USER, matchSummaryDo.getOwnerIsUser());
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchDto.getClosedStatus());

		matchSection.put(MatchFeedModel.MATCH.STATUS, MatchStatusUtilities.getStatus(matchSection));

		buildMatchSectionDefaultValues(matchSection);
		 
		oneMatchSection.put(MatchFeedModel.SECTIONS.MATCH, matchSection);
		
		oneMatch.put(Long.toString(matchId), oneMatchSection);
	}
	
	private void buildMatchSectionDefaultValues(Map<String, Object> matchSection){

		matchSection.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, false);
		matchSection.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, IcebreakerStateEnum.NONE.toInt());
		matchSection.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, null);
		matchSection.put(MatchFeedModel.MATCH.STAGE, 27);

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
