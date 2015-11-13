package com.eharmony.services.mymatchesservice.service.transform;

import com.eharmony.datastore.model.MatchCommunicationElement;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchElement;
import com.eharmony.datastore.model.MatchProfileElement;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Transform a feed from old Maps-of-Maps format into new Model.
 * @author kmunroe
 *
 */
public class FeedDtoTranslator {
	
	
    public static MatchDataFeedItemDto mapFeedtoMatchDataFeedItemList(
        Map<String, Map<String, Object>> match) {
        MatchDataFeedItemDto feedItem = new MatchDataFeedItemDto();

        Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
        feedItem.setMatch(mapMatchElement(matchSection));

        Map<String, Object> profileSection = match.get(MatchFeedModel.SECTIONS.PROFILE);
        feedItem.setMatchedUser(mapMatchedUser(profileSection));

        Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);
        feedItem.setCommunication(mapMatchCommunication(commSection, matchSection));

        return feedItem;
    }

    private static MatchElement mapMatchElement(
        Map<String, Object> matchSection) {
        MatchElement elem = new MatchElement();
        elem.setArchiveStatus(getIntegerNullSafe(MatchFeedModel.MATCH.ARCHIVE_STATUS, matchSection));
        elem.setClosedDate(getLongDateNullSafe(MatchFeedModel.MATCH.CLOSED_DATE, matchSection));
        elem.setClosedStatus(getIntegerNullSafe(MatchFeedModel.MATCH.CLOSED_STATUS, matchSection));
        elem.setDeliveredDate(getLongDateNullSafe(MatchFeedModel.MATCH.DELIVERED_DATE, matchSection));
        elem.setDistance(getIntegerNullSafe(MatchFeedModel.MATCH.DISTANCE, matchSection));
        elem.setMatchedUserId(getLongNullSafe(MatchFeedModel.MATCH.MATCHEDUSERID, matchSection));
        elem.setMatchId(getLongNullSafe(MatchFeedModel.MATCH.ID, matchSection));
        elem.setOneWayStatus(getIntegerNullSafe(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchSection));
        elem.setRelaxed(getBooleanNullSafe(MatchFeedModel.MATCH.RELAXED, matchSection) == true ? 1 : 0);
        elem.setStatus(translateMatchStatus(getStringNullSafe(MatchFeedModel.MATCH.STATUS, matchSection)));
        elem.setUserId(getLongNullSafe(MatchFeedModel.MATCH.USER_ID, matchSection));
        elem.setMatchUser(getBooleanNullSafe(MatchFeedModel.MATCH.IS_USER, matchSection));

        return elem;
    }
    
    private static Integer translateMatchStatus(String statusStr){
    	
    	// TODO: implement.
    	return 0;
    }

    private static MatchProfileElement mapMatchedUser(
        Map<String, Object> profileSection) {
        MatchProfileElement elem = new MatchProfileElement();

        elem.setCity(getStringNullSafe(MatchFeedModel.PROFILE.CITY, profileSection));
        elem.setCountry(getIntegerNullSafe(MatchFeedModel.PROFILE.COUNTRY, profileSection));
        elem.setFirstName(getStringNullSafe(MatchFeedModel.PROFILE.FIRSTNAME, profileSection));
        elem.setGender(getIntegerNullSafe(MatchFeedModel.PROFILE.GENDER, profileSection));
        elem.setLocale(getStringNullSafe(MatchFeedModel.PROFILE.LOCALE, profileSection));
        elem.setStateCode(getStringNullSafe(MatchFeedModel.PROFILE.STATE_CODE, profileSection));
        elem.setBirthdate(getLongDateNullSafe(MatchFeedModel.PROFILE.BIRTHDATE, profileSection));
        
        Object obj = profileSection.get(MatchFeedModel.PROFILE.PHOTO);
        elem.setPhotos(obj == null ? 0 : 1);

        return elem;
    }

    private static MatchCommunicationElement mapMatchCommunication(
        Map<String, Object> commSection, Map<String, Object> matchSection) {
        MatchCommunicationElement elem = new MatchCommunicationElement();

//        elem.setLastCommDate(getLongDateNullSafe(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, commSection));
//        elem.setViewedProfile(getBooleanNullSafe(MatchFeedModel.COMMUNICATION.VIEWED_PROFILE, commSection));        
//        elem.setChooseMhcsDate(getLongDateNullSafe(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE, matchSection));
//        elem.setCommLastSent(getLongNullSafe(MatchFeedModel.MATCH.COMM_LAST_SENT, matchSection));
//        elem.setDisplayTab(getIntegerNullSafe(MatchFeedModel.MATCH.DISPLAY_TAB, matchSection));
//        elem.setFastTrackAvailable(getBooleanNullSafe(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, matchSection));
//        elem.setFastTrackStage(getIntegerNullSafe(MatchFeedModel.MATCH.FAST_TRACK_STAGE, matchSection));
        
        //TODO
        //elem.setFastTrackStatus(getIntegerNullSafe(MatchFeedModel.MATCH.FAST_TRACK_STATUS, commSection));
        //elem.setIcebreakerStatus(getIntegerNullSafe(MatchFeedModel.MATCH.ICEBREAKER_STATUS, commSection));
        //elem.setInitializer(initializer);
        //elem.setLastCommDate(lastCommDate);
        //elem.setLastNudgeDate(lastNudgeDate);
        //elem.setNudgeStatus(nudgeStatus);
        elem.setStage(getIntegerNullSafe(MatchFeedModel.MATCH.STAGE, matchSection));

        

        return elem;
    }

    public static Boolean getBooleanNullSafe(String property,
        Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Object result = map.get(property);

        return (result == null) ? null : (Boolean) result;
    }

    public static Collection<?> getCollectionNullSafe(String property,
        Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        return (Collection<?>) map.get(property);
    }

    public static Integer getIntegerNullSafe(String property,
        Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Object result = map.get(property);

        if (result == null) {
            return null;
        }

        if (result instanceof Integer) {
            return (Integer) result;
        }

        if (result instanceof String) {
            return Integer.valueOf((String) result);
        }

        return null;
    }

    public static Date getLongDateNullSafe(String property,
        Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Object result = map.get(property);
        
        return (result == null) ? null
                                : new Date(Long.parseLong(result.toString()));
    }

    public static Long getLongNullSafe(String property, Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Object result = map.get(property);

        return (result == null) ? null : Long.valueOf(result.toString()); // it can be stored as integer or long
    }

    
    public static Double getDoubleNullSafe(String property, Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Object result = map.get(property);

        return (result == null) ? null : Double.valueOf(result.toString()); 
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapNullSafe(String property,
        Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        return (Map<String, Object>) map.get(property);
    }

    public static String getStringNullSafe(String property,
        Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        Object result = map.get(property);

        return (result == null) ? null : result.toString();
    }
}
