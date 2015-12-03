package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.model.MatchCommunicationElement;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchElement;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;

public class LegacyMatchFeedTransformer {

    private static final Logger logger = LoggerFactory.getLogger(LegacyMatchFeedTransformer.class);

    public static LegacyMatchDataFeedDto transform(MatchFeedRequestContext request) {
    	
    	Set<MatchDataFeedItemDto> hbaseFeedItems = request.getNewStoreFeed();
    	
        LegacyMatchDataFeedDto feedDto = new LegacyMatchDataFeedDto();
        Map<String, Map<String, Map<String, Object>>> matches = new HashMap<String, Map<String, Map<String, Object>>>();
        hbaseFeedItems.forEach(item -> {
            if (item != null && item.getMatch() != null) {
                String matchId = String.valueOf(item.getMatch().getMatchId());
                matches.put(matchId, buildLegacyFeedItem(request, item));
            } else {
                logger.warn("Skipping the invalid feed item..");
            }
        });
        
        feedDto.setMatches(matches);
        feedDto.setLocale(request.getMatchFeedQueryContext().getLocale());
        feedDto.setTotalMatches(matches.size());
        
        return feedDto;
    }

    private static Map<String, Map<String, Object>> buildLegacyFeedItem(MatchFeedRequestContext request, 
    															MatchDataFeedItemDto matchDataFeedItemDto) {
        Map<String, Map<String, Object>> feedItemMap = new HashMap<String, Map<String, Object>>();

        feedItemMap.put(MatchFeedModel.SECTIONS.MATCH, createMatchFeedMatch(matchDataFeedItemDto));
        feedItemMap.put(MatchFeedModel.SECTIONS.PROFILE, createMatchFeedProfile(matchDataFeedItemDto));
		feedItemMap.put(MatchFeedModel.SECTIONS.COMMUNICATION, createMatchFeedCommunication(matchDataFeedItemDto));
		
        return feedItemMap;
    }
    
	private static Map<String, Object> createMatchFeedProfile(MatchDataFeedItemDto item) {

    	Map<String, Object> profile = new HashMap<>();
    	MatchProfileElement elem = item.getMatchedUser();
    	
        profile.put(MatchFeedModel.PROFILE.CITY, elem.getCity());
        profile.put(MatchFeedModel.PROFILE.COUNTRY, elem.getCountry());
        profile.put(MatchFeedModel.PROFILE.FIRSTNAME, elem.getFirstName());
        profile.put(MatchFeedModel.PROFILE.GENDER, elem.getGender());
        profile.put(MatchFeedModel.PROFILE.LOCALE, elem.getLocale());
        profile.put(MatchFeedModel.PROFILE.STATE_CODE, elem.getStateCode());
        profile.put(MatchFeedModel.PROFILE.BIRTHDATE, getTimeNullSafe(elem.getBirthdate()));
        profile.put(MatchFeedModel.PROFILE.USERID, item.getMatch().getMatchedUserId());

        return profile;
	}
	
    private static Map<String, Object> createMatchFeedMatch(
			MatchDataFeedItemDto item) {

    	Map<String, Object> match = new HashMap<>();
    	MatchElement matchElem = item.getMatch();
    	MatchCommunicationElement commElem = item.getCommunication();
    	MatchProfileElement profileElem = item.getMatchedUser();
    	
    	// pulled from matchElement
        match.put(MatchFeedModel.MATCH.ARCHIVE_STATUS, matchElem.getArchiveStatus());
        match.put(MatchFeedModel.MATCH.CLOSED_DATE, getTimeNullSafe(matchElem.getClosedDate()));
        match.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchElem.getClosedStatus());
        match.put(MatchFeedModel.MATCH.DELIVERED_DATE, getTimeNullSafe(matchElem.getDeliveredDate()));
        match.put(MatchFeedModel.MATCH.DISTANCE, matchElem.getDistance());
        match.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchElem.getMatchedUserId());
        match.put(MatchFeedModel.MATCH.ID, matchElem.getMatchId());
        match.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchElem.getOneWayStatus());
        match.put(MatchFeedModel.MATCH.RELAXED, matchElem.getRelaxed());
        match.put(MatchFeedModel.MATCH.STATUS,  deriveTextStatus(matchElem.getMatchId(), matchElem.getStatus()));
        match.put(MatchFeedModel.MATCH.USER_ID, matchElem.getUserId());
        match.put(MatchFeedModel.MATCH.IS_USER, matchElem.isMatchUser());

        // pulled from commElement
        match.put(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE, getTimeNullSafe(commElem.getChooseMhcsDate()));
        match.put(MatchFeedModel.MATCH.COMM_LAST_SENT, commElem.getCommLastSent());
        match.put(MatchFeedModel.MATCH.DISPLAY_TAB, commElem.getDisplayTab());
        match.put(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, commElem.getFastTrackAvailable());
        match.put(MatchFeedModel.MATCH.FAST_TRACK_STAGE, commElem.getFastTrackStage());
        match.put(MatchFeedModel.MATCH.FAST_TRACK_STATUS, commElem.getFastTrackStatus());
        match.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, commElem.getIcebreakerStatus());      
        match.put(MatchFeedModel.MATCH.LAST_NUDGE_DATE, getTimeNullSafe(commElem.getLastNudgeDate()));
        match.put(MatchFeedModel.MATCH.STAGE, commElem.getStage());
        match.put(MatchFeedModel.MATCH.COMM_STARTED_DATE, getTimeNullSafe(commElem.getCommStartedDate()));
        match.put(MatchFeedModel.MATCH.NUDGE_STATUS, commElem.getNudgeStatus());
        match.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, getTimeNullSafe(commElem.getReadDetailsDate()));
        match.put(MatchFeedModel.MATCH.TURN_OWNER, commElem.getTurnOwner());
        match.put(MatchFeedModel.MATCH.INITIALIZER, commElem.getInitializer());
        match.put(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB, commElem.getDisplayTab());

        // pulled from profileElement
        match.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, profileElem.getFirstName());
        
        return match;
    }
    
    private static Long getTimeNullSafe(Date date){
    	if(date == null){
    		return null;
    	}
    	
    	return date.getTime();
    }
    
	private static String deriveTextStatus(long matchId, int status) {

		MatchStatusEnum ms = MatchStatusEnum.fromInt(status);
		if(ms == null){
			logger.warn("unknown match status {} for matchId {}, returning \"\"", matchId, status);
			return "";
		}
		
		return ms.getName();
	}

	private static Map<String, Object> createMatchFeedCommunication(
			MatchDataFeedItemDto item) {
		
    	Map<String, Object> comm = new HashMap<>();
    	MatchCommunicationElement elem = item.getCommunication();

	    comm.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, elem.getLastCommDate());
	    comm.put(MatchFeedModel.COMMUNICATION.VIEWED_PROFILE, elem.isViewedProfile()); 
    	
    	return comm;
	}
}
