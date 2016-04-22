package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.datastore.model.MatchCommunicationElement;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchElement;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;

@Component
public class LegacyMatchFeedTransformer {

    private static final Logger logger = LoggerFactory.getLogger(LegacyMatchFeedTransformer.class);

    public LegacyMatchDataFeedDto transform(MatchFeedRequestContext request) {

        Set<MatchDataFeedItemDto> hbaseFeedItems = request.getAggregateHBaseFeedItems();
        long userId = request.getUserId();
        String locale = request.getMatchFeedQueryContext() != null ? request.getMatchFeedQueryContext().getLocale() : null;

        return transform(hbaseFeedItems, userId, locale);
    }
    
    public LegacyMatchDataFeedDto transform(Set<MatchDataFeedItemDto> hbaseFeedItems, long userId, String locale) {
 
        LegacyMatchDataFeedDto feedDto = new LegacyMatchDataFeedDto();
        Map<String, Map<String, Map<String, Object>>> matches = new HashMap<String, Map<String, Map<String, Object>>>();
        if(CollectionUtils.isNotEmpty(hbaseFeedItems)) {
            hbaseFeedItems.forEach(item -> {
                if (item != null && item.getMatch() != null) {
                    String matchId = String.valueOf(item.getMatch().getMatchId());
                    matches.put(matchId, buildLegacyFeedItem(item));
                } else {
                    logger.warn("Skipping the invalid feed item for user {}", userId);
                }
            });
        }

        feedDto.setMatches(matches);
        feedDto.setLocale(locale);
        //Move this after all filters
        feedDto.setTotalMatches(matches.size());
        return feedDto;
    }

    protected Map<String, Map<String, Object>> buildLegacyFeedItem(MatchDataFeedItemDto matchDataFeedItemDto) {
        Map<String, Map<String, Object>> feedItemMap = new HashMap<String, Map<String, Object>>();

        feedItemMap.put(MatchFeedModel.SECTIONS.MATCH, createMatchFeedMatch(matchDataFeedItemDto));
        feedItemMap.put(MatchFeedModel.SECTIONS.PROFILE, createMatchFeedProfile(matchDataFeedItemDto));
        feedItemMap.put(MatchFeedModel.SECTIONS.COMMUNICATION, createMatchFeedCommunication(matchDataFeedItemDto));

        return feedItemMap;
    }

    private Map<String, Object> createMatchFeedProfile(MatchDataFeedItemDto item) {

        Map<String, Object> profile = new HashMap<>();
        MatchProfileElement elem = item.getMatchedUser();
        if (elem == null) {
            logger.warn("Null MatchProfileElement, returning empty profile.");
            return profile;
        }

        profile.put(MatchFeedModel.PROFILE.CITY, elem.getCity());
        profile.put(MatchFeedModel.PROFILE.COUNTRY, elem.getCountry());
        profile.put(MatchFeedModel.PROFILE.FIRSTNAME, elem.getFirstName());
        profile.put(MatchFeedModel.PROFILE.GENDER, elem.getGender());
        profile.put(MatchFeedModel.PROFILE.STATE_CODE, emptyStringIfNull(elem.getStateCode()));
        profile.put(MatchFeedModel.PROFILE.BIRTHDATE, getTimeInMillisNullSafe(elem.getBirthdate()));
        profile.put(MatchFeedModel.PROFILE.USERID, item.getMatch().getMatchedUserId());
        profile.put(MatchFeedModel.PROFILE.LOCALE, elem.getLocale());
        if(elem.getPhotos() != 0 ){
        	
        	profile.put(MatchFeedModel.PROFILE.PHOTO_COUNT, elem.getPhotos());
        }
        return profile;
    }

    private Map<String, Object> createMatchFeedMatch(MatchDataFeedItemDto item) {

        Map<String, Object> match = new HashMap<>();
        MatchElement matchElem = item.getMatch();
        MatchCommunicationElement commElem = item.getCommunication();
        MatchProfileElement profileElem = item.getMatchedUser();

        if (matchElem == null || commElem == null || profileElem == null) {
            logger.warn("Found null elements in feed, returning empty match.");
            return match;
        }

        // pulled from matchElement
        match.put(MatchFeedModel.MATCH.ARCHIVE_STATUS, matchElem.getArchiveStatus());
        match.put(MatchFeedModel.MATCH.CLOSED_DATE, getTimeInMillisNullSafe(matchElem.getClosedDate()));
        match.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchElem.getClosedStatus());
        match.put(MatchFeedModel.MATCH.DELIVERED_DATE, getTimeInMillisNullSafe(matchElem.getDeliveredDate()));
        match.put(MatchFeedModel.MATCH.DISTANCE, matchElem.getDistance());
        match.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchElem.getMatchedUserId());
        match.put(MatchFeedModel.MATCH.ID, matchElem.getMatchId());
        match.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchElem.getOneWayStatus());
        match.put(MatchFeedModel.MATCH.RELAXED, deriveRelaxedState(matchElem.getRelaxed()));
        match.put(MatchFeedModel.MATCH.STATUS, deriveTextStatus(matchElem.getMatchId(), matchElem.getStatus()));
        match.put(MatchFeedModel.MATCH.USER_ID, matchElem.getUserId());
        match.put(MatchFeedModel.MATCH.IS_USER, matchElem.isMatchUser());
        Date lastModifiedDate = matchElem.getLastModifiedDate();
        match.put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE, lastModifiedDate != null ? lastModifiedDate.getTime() : 0L);

        // pulled from commElement
        match.put(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE, getTimeInMillisNullSafe(commElem.getChooseMhcsDate()));
        match.put(MatchFeedModel.MATCH.COMM_LAST_SENT, commElem.getCommLastSent());
        match.put(MatchFeedModel.MATCH.DISPLAY_TAB, commElem.getDisplayTab());
        match.put(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, commElem.getFastTrackAvailable());
        match.put(MatchFeedModel.MATCH.FAST_TRACK_STAGE, commElem.getFastTrackStage());
        match.put(MatchFeedModel.MATCH.FAST_TRACK_STATUS, commElem.getFastTrackStatus());
        match.put(MatchFeedModel.MATCH.ICEBREAKER_STATUS, commElem.getIcebreakerStatus());
        match.put(MatchFeedModel.MATCH.LAST_NUDGE_DATE, getTimeInMillisNullSafe(commElem.getLastNudgeDate()));
        match.put(MatchFeedModel.MATCH.STAGE, commElem.getStage());
        match.put(MatchFeedModel.MATCH.COMM_STARTED_DATE, getTimeInMillisNullSafe(commElem.getCommStartedDate()));
        match.put(MatchFeedModel.MATCH.NUDGE_STATUS, commElem.getNudgeStatus());
        match.put(MatchFeedModel.MATCH.READ_MATCH_DETAILS, (commElem.getReadDetailsDate() != null));
        match.put(MatchFeedModel.MATCH.READ_DETAILS_DATE, getTimeInMillisNullSafe(commElem.getReadDetailsDate()));
        match.put(MatchFeedModel.MATCH.TURN_OWNER, commElem.getTurnOwner());
        match.put(MatchFeedModel.MATCH.INITIALIZER, commElem.getInitializer());
        match.put(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB, commElem.getDisplayTab());

        // pulled from profileElement
        match.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, profileElem.getFirstName());

        return match;
    }
    
    private String emptyStringIfNull(String value){
    	return value == null ? "" : value;
    }

    private Map<String, Object> createMatchFeedCommunication(MatchDataFeedItemDto item) {

        Map<String, Object> comm = new HashMap<>();
        MatchCommunicationElement elem = item.getCommunication();

        comm.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, elem.getLastCommDate());
        comm.put(MatchFeedModel.COMMUNICATION.VIEWED_PROFILE, elem.isViewedProfile());

        // KDM: not used, but set anyway.
        comm.put(MatchFeedModel.COMMUNICATION.NEW_MESSAGE_COUNT, 0);

        return comm;
    }

    private Boolean deriveRelaxedState(int relaxed) {
        if (relaxed == 0) {
            return false;
        }
        return true;
    }

    private Long getTimeInMillisNullSafe(Date date) {
        if (date == null) {
            return null;
        }

        return date.getTime();
    }

    private String deriveTextStatus(long matchId, int status) {

        MatchStatusEnum ms = MatchStatusEnum.fromInt(status);
        if (ms == null) {
            logger.warn("unknown match status {} for matchId {}, returning \"\"", matchId, status);
            return "";
        }

        return ms.getName().toLowerCase();
    }

}
