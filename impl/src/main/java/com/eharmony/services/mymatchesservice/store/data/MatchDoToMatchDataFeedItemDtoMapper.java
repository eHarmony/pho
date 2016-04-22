package com.eharmony.services.mymatchesservice.store.data;

import java.util.HashMap;
import java.util.Map;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class MatchDoToMatchDataFeedItemDtoMapper {

	public Map<String, Map<String, Object>> transform(MatchDo match){
		
		Map<String, Map<String, Object>> target = new HashMap<String, Map<String, Object>>();
		
		mapMatch(target, match);
		
		return target;
	}

	private void mapMatch(Map<String, Map<String, Object>> match, MatchDo matchDo) {

		match.put(MatchFeedModel.SECTIONS.MATCH, new HashMap<String, Object>());
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		
		matchSection.put(MatchFeedModel.MATCH.ARCHIVE_STATUS, matchDo.getArchiveStatus());
		matchSection.put(MatchFeedModel.MATCH.CLOSED_DATE, matchDo.getCandidateClosedDate());
		
		matchSection.put(MatchFeedModel.MATCH.ID, matchDo.getId());
		matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDo.getUserDeliveredDate());
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDo.getUserId());
		matchSection.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, matchDo.getUserReadDetails());
		matchSection.put(MatchFeedModel.MATCH.NEW_MESSAGE_COUNT, matchDo.getUserNewMessageCount());
		matchSection.put(MatchFeedModel.MATCH.FIRST_NAME, matchDo.getCandidateFirstName());
		matchSection.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, matchDo.getCandidateFirstName());
		matchSection.put( MatchFeedModel.MATCH.INITIALIZER, matchDo.getInitializer());
       
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchDo.getClosedStatus());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDo.getDistance());
		matchSection.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, matchDo.getIcebreakerStatus());
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
	}
}
