package com.eharmony.services.mymatchesservice.store.data;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.profile.ProfileType;
import com.eharmony.services.profile.client.ProfileServiceClient;

public class MatchDoToMatchDataFeedItemDtoMapper {

    @Resource private ProfileServiceClient profileService;

	public Map<String, Map<String, Object>> transform(MatchDo match){
		
		Map<String, Map<String, Object>> target = new HashMap<String, Map<String, Object>>();
		
		mapMatch(target, match);
		//mapCommunication(target, match);
		//mapProfile(target, match);
		
		return target;
	}

	private void mapMatch(Map<String, Map<String, Object>> match, MatchDo matchDo) {

		match.put(MatchFeedModel.SECTIONS.MATCH, new HashMap<String, Object>());
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		
		matchSection.put(MatchFeedModel.MATCH.ARCHIVE_STATUS, matchDo.getArchiveStatus());
		matchSection.put(MatchFeedModel.MATCH.CLOSED_DATE, matchDo.getCandidateClosedDate());
		
		matchSection.put(MatchFeedModel.MATCH.ID, matchDo.getId());
		//matchSection.put(MatchFeedModel.MATCH.STATUS
		matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDo.getUserDeliveredDate());
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDo.getUserId());
		//matchSection.put(MatchFeedModel.MATCH.COMM_LAST_SENT, matchDo.getCommunication().getCommLastSentByUser());
		//matchSection.put(MatchFeedModel.MATCH.MATCH_COMM_LAST_SENT, matchDo.getCommunication().getCommLastSentByCandidate());;
		matchSection.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, matchDo.getUserReadDetails());
		matchSection.put(MatchFeedModel.MATCH.NEW_MESSAGE_COUNT, matchDo.getUserNewMessageCount());
		matchSection.put(MatchFeedModel.MATCH.FIRST_NAME, matchDo.getCandidateFirstName());
		matchSection.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, matchDo.getCandidateFirstName());
		matchSection.put( MatchFeedModel.MATCH.INITIALIZER, matchDo.getInitializer());
       
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchDo.getClosedStatus());
		//matchSection.put(MatchFeedModel.MATCH.DISPLAY_TAB
		//matchSection.put(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE
		//matchSection.put(MatchFeedModel.MATCH.CLOSED_DATE
		//matchSection.put(MatchFeedModel.MATCH.COMM_STARTED_DATE, matchDo.getCommunication().getCommLastSentByUser());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDo.getDistance());
		//matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, matchDo.getCommunication().getFastTrackAvailable());
		//matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STAGE, matchDo.getCommunication().getFastTrackStage());
		//matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STATUS, matchDo.getCommunication().getFastTrackStatus());
		matchSection.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, matchDo.getIcebreakerStatus());
		//matchSection.put(MatchFeedModel.MATCH.IS_USER
		matchSection.put(MatchFeedModel.MATCH.LAST_NUDGE_DATE, matchDo.getLastNudgeDate());
		matchSection.put(MatchFeedModel.MATCH.MATCH_CLOSED_COUNT, matchDo.getMatchClosedCount());
		matchSection.put(MatchFeedModel.MATCH.NEW_MATCH_MESSAGE_COUNT, matchDo.getUserNewMessageCount());
		matchSection.put(MatchFeedModel.MATCH.NUDGE_STATUS, matchDo.getNudgeStatus());
		matchSection.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchDo.getOneWayStatus());
		matchSection.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, matchDo.getUserReadDetailsDate());
		matchSection.put(MatchFeedModel.MATCH.RELAXED, matchDo.getCandidateRelaxed());
		matchSection.put(MatchFeedModel.MATCH.STAGE, matchDo.getStage());
		matchSection.put(MatchFeedModel.MATCH.TURN_OWNER, matchDo.getTurnOwner());
		matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDo.getUserId());
		//matchSection.put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE, matchDo.;
       
		//åmatchSection.put(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE = "matchAttractivenessScore";

	}
	
	private void mapCommunication(Map<String, Map<String, Object>> matchedUser, MatchDo matchDo) {
      
		matchedUser.put(MatchFeedModel.SECTIONS.COMMUNICATION, new HashMap<String, Object>());
		Map<String, Object> commSection = matchedUser.get(MatchFeedModel.SECTIONS.COMMUNICATION);
			
		CommunicationDo commDo = matchDo.getCommunication();
		
		long turnOwnerId = matchDo.getTurnOwner();
		if(turnOwnerId == matchDo.getUserId()){
			commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, commDo.getCommLastSentByCandidate());
		}else{
			commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, commDo.getCommLastSentByUser());
		}
		
		commSection.put(MatchFeedModel.COMMUNICATION.NEW_MESSAGE_COUNT, matchDo.getUserNewMessageCount());


	}

	private void mapProfile(Map<String, Map<String, Object>> matchedUser, MatchDo matchDo) {

		Map<String, Object> profile = profileService.findProfileForUserAsMap(Integer.valueOf(Long.toString(matchDo.getUserId())), ProfileType.BASIC);
	
		matchedUser.put(MatchFeedModel.SECTIONS.PROFILE, profile);
	}


}
