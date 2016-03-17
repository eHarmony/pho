package com.eharmony.services.mymatchesservice.service.transform;


import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class MatchFeedModel {
	
	static Logger log = LoggerFactory.getLogger(MatchFeedModel.class);

   public interface COMMUNICATION {

       static final String NEXT_STEP = "nextStep";
       static final String CAPTION = "caption";
       static final String LINK = "link";
       static final String MESSAGE = "message";
       static final String LAST_COMM_DATE = "lastCommDate";
       static final String NEW_MESSAGE_COUNT = "newMessageCount";
       static final String VIEWED_PROFILE = "viewedProfile";
       static final String SECTION="section";
       static final String SUB_SECTION="subSection";
       static final String STATUS="status";
       static final String NEXT_COMMUNICATION_ACTION = "nextCommunicationAction";
       static final String ACTION="action";
       static final String AREA = "area";

   }

   public interface MATCH {

       static final String ID = "id";
       static final String STATUS = "status";
       static final String ARCHIVE_STATUS = "archiveStatus";
       static final String DELIVERED_DATE = "deliveredDate";
       static final String MATCHEDUSERID = "matchedUserId";
       static final String COMM_LAST_SENT = "commLastSent";
       static final String MATCH_COMM_LAST_SENT = "matchCommLastSent";
       static final String READ_MATCH_DETAILS = "readMatchDetails";
       static final String NEW_MESSAGE_COUNT = "newMessageCount";
       static final String FIRST_NAME = "firstName";
       static final String MATCH_FIRST_NAME = "matchFirstName";
       static final String INITIALIZER = "initializer";
       
       static final String CLOSED_STATUS = "closedStatus";
       static final String DISPLAY_TAB = "displayTab";
       static final String CHOOSE_MHCS_DATE = "chooseMhcsDate";
       static final String CLOSED_DATE = "closedDate";
       static final String COMM_STARTED_DATE = "commStartedDate";
       static final String DISTANCE = "distance";
       static final String FAST_TRACK_AVAILABLE = "fastTrackAvailable";
       static final String FAST_TRACK_STAGE = "fastTrackStage";
       static final String FAST_TRACK_STATUS = "fastTrackStatus";
       static final String ICEBREAKER_STATUS = "icebreakerStatus";
       static final String IS_USER = "isUser";
       static final String LAST_NUDGE_DATE = "lastNudgeDate";
       static final String MATCH_CLOSED_COUNT = "matchClosedCount";
       static final String MATCH_DISPLAY_TAB = "matchDisplayTab";
       static final String NEW_MATCH_MESSAGE_COUNT = "newMatchMessageCount";
       static final String NUDGE_STATUS = "nudgeStatus";
       static final String ONE_WAY_STATUS = "oneWayStatus";
       static final String READ_DETAILS_DATE = "readDetailsDate";
       static final String RELAXED = "relaxed";
       static final String STAGE = "stage";
       static final String TURN_OWNER = "turnOwner";
       static final String USER_ID = "userId";
       static final String LAST_MODIFIED_DATE = "lastModifiedDate";
       
       static final String MATCH_ATTRACTIVENESS_SCORE = "matchAttractivenessScore";
   }

   public interface PROFILE {

       static final String GENDER = "gender";
       static final String BIRTHDATE = "birthdate";
       static final String AGE = "age";
       static final String USERID = "userId";
       static final String FIRSTNAME = "firstName";
       static final String HAS_PHOTO = "hasPhoto";
       static final String PHOTOS = "photos";
       static final String PHOTOICON = "photoIcon";
       static final String PHOTOTHUMB = "photoThumbnail";
       static final String PHOTO = "photo";
       static final String PHOTO_INDEX = "index";
       static final String PHOTO_HEIGHT = "height";
       static final String PHOTO_WIDTH = "width";
       static final String PHOTO_CAPTION = "caption";
       static final String PHOTO_STATUS = "status";
       static final String PHOTO_PRIMARY = "primary";
       static final String LOCALE = "locale";
       
       static final String CITY = "city";
       static final String COUNTRY = "country";
       static final String SCALED = "scaled";
       static final String THUMBNAIL = "thumbnail";
       static final String ICON = "icon";
       static final String STATE_CODE = "stateCode";
       static final String VERSION = "version";
       
       static final String PHOTO_COUNT = "photoCount";

   }

   public interface SECTIONS {

       static final String COMMUNICATION = "communication";
       static final String MATCH = "match";
       static final String PROFILE = "matchedUser";

   }

   public interface STATUS {

       static final String ALL = "all";

   }

   private MatchFeedModel() {

   }

   public static Boolean getBooleanNullSafe(String property,
                                            Map<String, Object> map) {

       if (map == null) {

           return null;

       }
       Object result = map.get(property);
       return (result == null) ? null
                               : (Boolean) result;

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
       return (result == null) ? null
                               : (Integer) result;

   }

   public static Date getLongDateNullSafe(String property,
                                          Map<String, Object> map) {

       if (map == null) {

           return null;

       }

       Object result = map.get(property);

       log.debug("converting result [{}] to Long/Date", result);
       
       return (result == null) ? null
                               : new Date(Long.parseLong(result.toString()));

   }

   public static Long getLongNullSafe(String property,
                                      Map<String, Object> map) {

       if (map == null) {

           return null;

       }
       Object result = map.get(property);
       return (result == null) ? null
                               : Long.valueOf(result.toString()); // it can be stored as integer or long

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
       return (result == null) ? null
                               : result.toString();

   }

}

