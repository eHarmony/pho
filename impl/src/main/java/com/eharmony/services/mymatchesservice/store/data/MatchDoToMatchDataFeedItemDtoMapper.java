package com.eharmony.services.mymatchesservice.store.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class MatchDoToMatchDataFeedItemDtoMapper {

	public Map<String, Map<String, Object>> transform(long userId, MatchDo match){
		
		Map<String, Map<String, Object>> target = new HashMap<String, Map<String, Object>>();
		
		mapMatch(userId, target, match);
		
		return target;
	}

	private void mapMatch(long userId, Map<String, Map<String, Object>> match, MatchDo matchDo) {

		match.put(MatchFeedModel.SECTIONS.MATCH, new HashMap<String, Object>());
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		
		matchSection.put(MatchFeedModel.MATCH.ARCHIVE_STATUS, 
				(matchDo.getArchiveStatus() == null ? null :matchDo.getArchiveStatus().toInt()));
		matchSection.put(MatchFeedModel.MATCH.ID, matchDo.getId());

		matchSection.put(MatchFeedModel.MATCH.INITIALIZER, 
				(matchDo.getInitializer() == null ? null : matchDo.getInitializer().toInt()));
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchDo.getClosedStatus());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDo.getDistance());
		matchSection.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, 
				(matchDo.getIcebreakerStatus() == null ? null : matchDo.getIcebreakerStatus().toInt()));
		matchSection.put(MatchFeedModel.MATCH.LAST_NUDGE_DATE, matchDo.getLastNudgeDate());
		matchSection.put(MatchFeedModel.MATCH.MATCH_CLOSED_COUNT, matchDo.getMatchClosedCount());
		matchSection.put(MatchFeedModel.MATCH.NUDGE_STATUS, 
				(matchDo.getNudgeStatus() == null ? null : matchDo.getNudgeStatus().toInt()));
		matchSection.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, 
				(matchDo.getOneWayStatus() == null ? null : matchDo.getOneWayStatus().toInt()));
		matchSection.put(MatchFeedModel.MATCH.STAGE, matchDo.getStage());
		matchSection.put(MatchFeedModel.MATCH.TURN_OWNER, matchDo.getTurnOwner());
		
		boolean isUser = deriveIsUser(userId, matchDo);
		matchSection.put(MatchFeedModel.MATCH.IS_USER, isUser);
		
		mapMatchFieldsWithOrientation(matchSection, isUser, userId, matchDo);

		match.put(MatchFeedModel.SECTIONS.PROFILE, new HashMap<String, Object>());
		Map<String, Object> profileSection = match.get(MatchFeedModel.SECTIONS.PROFILE);
		mapProfileFieldsWithOrientation(profileSection, isUser, matchDo);
		
		match.put(MatchFeedModel.SECTIONS.COMMUNICATION, new HashMap<String, Object>());
		Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);
		commSection.put(MatchFeedModel.COMMUNICATION.NEW_MESSAGE_COUNT, matchDo.getUserNewMessageCount());
		mapCommFieldsWithOrientation(commSection, isUser, matchDo);

	}
	
	private void mapMatchFieldsWithOrientation(Map<String, Object> matchSection,
									boolean isUser, 
									long userId, MatchDo matchDo) {
		
		if(isUser){
			matchSection.put(MatchFeedModel.MATCH.FIRST_NAME, matchDo.getUserFirstName());
			matchSection.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, matchDo.getCandidateFirstName());
			matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDo.getCandidateUserId());
			matchSection.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, matchDo.getUserReadDetailsDate());
			matchSection.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, matchDo.getUserReadDetails());
			matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDo.getUserId());
			matchSection.put(MatchFeedModel.MATCH.CLOSED_DATE, matchDo.getUserClosedDate());
			matchSection.put(MatchFeedModel.MATCH.DISPLAY_TAB, 
					(matchDo.getUserDisplayTab() == null? null: matchDo.getUserDisplayTab().toInt()));
			matchSection.put(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB, 
					(matchDo.getCandidateDisplayTab() == null ? null :matchDo.getCandidateDisplayTab().toInt()));
			matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDo.getUserDeliveredDate());
			matchSection.put(MatchFeedModel.MATCH.NEW_MESSAGE_COUNT, matchDo.getUserNewMessageCount());
			matchSection.put(MatchFeedModel.MATCH.NEW_MATCH_MESSAGE_COUNT, matchDo.getCandidateNewMessageCount());
			matchSection.put(MatchFeedModel.MATCH.RELAXED, matchDo.getUserRelaxed());
		}else{
			matchSection.put(MatchFeedModel.MATCH.FIRST_NAME, matchDo.getCandidateFirstName());
			matchSection.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, matchDo.getUserFirstName());
			matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDo.getUserId());
			matchSection.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, matchDo.getCandidateReadDetailsDate());
			matchSection.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, matchDo.getCandidateReadDetails());
			matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDo.getCandidateUserId());
			matchSection.put(MatchFeedModel.MATCH.CLOSED_DATE, matchDo.getCandidateClosedDate());
			matchSection.put(MatchFeedModel.MATCH.DISPLAY_TAB, 
					(matchDo.getCandidateDisplayTab() == null? null: matchDo.getCandidateDisplayTab().toInt()));
			matchSection.put(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB, 
					(matchDo.getUserDisplayTab() == null ? null :matchDo.getUserDisplayTab().toInt()));
			matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDo.getCandidateDeliveredDate());
			matchSection.put(MatchFeedModel.MATCH.NEW_MESSAGE_COUNT, matchDo.getCandidateNewMessageCount());		
			matchSection.put(MatchFeedModel.MATCH.NEW_MATCH_MESSAGE_COUNT, matchDo.getUserNewMessageCount());
			matchSection.put(MatchFeedModel.MATCH.RELAXED, matchDo.getCandidateRelaxed());
		}
	}
	
	private void mapProfileFieldsWithOrientation(Map<String, Object> profileSection,
									boolean isUser, MatchDo matchDo) {

		// profile is for the other user
		if(isUser){
			profileSection.put(MatchFeedModel.PROFILE.USERID, matchDo.getCandidateUserId());
		}else{
			profileSection.put(MatchFeedModel.PROFILE.USERID, matchDo.getUserId());
		}
	}

	private void mapCommFieldsWithOrientation(Map<String, Object> commSection,
			boolean isUser, MatchDo matchDo) {

		if(isUser){
			commSection.put(MatchFeedModel.COMMUNICATION.VIEWED_PROFILE, matchDo.getUserReadDetails());		
		}else{
			commSection.put(MatchFeedModel.COMMUNICATION.VIEWED_PROFILE, matchDo.getCandidateReadDetails());
		}
}
	private boolean deriveIsUser(long userId, MatchDo matchDo){
		
		// From VDS.MatchDoToMatchDtoTransformer
        if (matchDo.getCandidateUserId().equals(userId)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
	}
}
