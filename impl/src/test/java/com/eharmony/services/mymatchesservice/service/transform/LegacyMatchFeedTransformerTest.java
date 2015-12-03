package com.eharmony.services.mymatchesservice.service.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import com.eharmony.datastore.model.MatchCommunicationElement;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchElement;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class LegacyMatchFeedTransformerTest {

	@Test
	public void testTransform(){
		
		MatchDataFeedItemDto hbaseFeedItem = new MatchDataFeedItemDto();
		
		// fill up HBase dto with values...
		Field[] matchFields = hbaseFeedItem.getMatch().getClass().getDeclaredFields();
		buildExpectedFieldValues(hbaseFeedItem.getMatch(), matchFields);
		Field[] profileFields = hbaseFeedItem.getMatchedUser().getClass().getDeclaredFields();
		buildExpectedFieldValues(hbaseFeedItem.getMatchedUser(), profileFields);
		Field[] commFields = hbaseFeedItem.getCommunication().getClass().getDeclaredFields();
		buildExpectedFieldValues(hbaseFeedItem.getCommunication(), commFields);
		
		Set<MatchDataFeedItemDto> feedItems = new HashSet<>();
		feedItems.add(hbaseFeedItem);
		
		// Do transform here
		LegacyMatchDataFeedDto feed = 
				LegacyMatchFeedTransformer.transform(feedItems);
		
		// get the Legacy sections...
		Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
		assertNotNull(matches);
		
		String matchId = String.valueOf(hbaseFeedItem.getMatch().getMatchId());
		assertNotNull(matches.get(matchId));		
		Map<String, Map<String, Object>> oneMatch = matches.get(matchId);
		Map<String, Object> matchSection = oneMatch.get(MatchFeedModel.SECTIONS.MATCH);
		Map<String, Object> profileSection = oneMatch.get(MatchFeedModel.SECTIONS.PROFILE);
		
		// get the hbase sections...
    	MatchProfileElement profileElem = hbaseFeedItem.getMatchedUser();
    	MatchCommunicationElement commElem = hbaseFeedItem.getCommunication();
    	MatchElement matchElem = hbaseFeedItem.getMatch();
    	
    	// do comparisons.
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.CITY), profileElem.getCity());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.COUNTRY), profileElem.getCountry());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.FIRSTNAME), profileElem.getFirstName());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.GENDER), profileElem.getGender());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.LOCALE), profileElem.getLocale());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.STATE_CODE), profileElem.getStateCode());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.BIRTHDATE), profileElem.getBirthdate());

        assertEquals(matchSection.get(MatchFeedModel.MATCH.ARCHIVE_STATUS), matchElem.getArchiveStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.CLOSED_DATE), matchElem.getClosedDate());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.CLOSED_STATUS), matchElem.getClosedStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.DELIVERED_DATE), matchElem.getDeliveredDate());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.DISTANCE), matchElem.getDistance());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.MATCHEDUSERID), matchElem.getMatchedUserId());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.ID), matchElem.getMatchId());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.ONE_WAY_STATUS), matchElem.getOneWayStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.RELAXED), matchElem.getRelaxed());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.STATUS),  matchElem.getStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.USER_ID), matchElem.getUserId());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.IS_USER), matchElem.isMatchUser());

        assertEquals(matchSection.get(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE), commElem.getChooseMhcsDate());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.COMM_LAST_SENT), commElem.getCommLastSent());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.DISPLAY_TAB), commElem.getDisplayTab());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE), commElem.getFastTrackAvailable());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STAGE), commElem.getFastTrackStage());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STATUS), commElem.getFastTrackStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.ICEBREAKER_STATUS), commElem.getIcebreakerStatus());      
        assertEquals(matchSection.get(MatchFeedModel.MATCH.LAST_NUDGE_DATE), commElem.getLastNudgeDate());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.STAGE), commElem.getStage());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.COMM_STARTED_DATE), commElem.getCommStartedDate());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.NUDGE_STATUS), commElem.getNudgeStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.READ_DETAILS_DATE), commElem.getReadDetailsDate());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.TURN_OWNER), commElem.getTurnOwner());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB), commElem.getDisplayTab());

        // pulled from profileElement
       // assertEquals(matchSection.get(MatchFeedModel.MATCH.MATCH_FIRST_NAME, profileElem.getFirstName());
        
		System.err.println("DONE!");
	}
	

	private void buildExpectedFieldValues(Object hbaseFeedSection, Field[] fields){

		for(Field f : fields){
			Object value = generateValueOfType(f.getType());
			
			// set value in feed section
			try{
				BeanUtils.setProperty(hbaseFeedSection,f.getName(),value);
			}catch(InvocationTargetException ex){
				ex.printStackTrace();
			}catch(IllegalAccessException e2){
				e2.printStackTrace();
			}
		}
	}

	private Object generateValueOfType(Class<?> aType){
		
		switch(aType.getName()){
		
		case "java.lang.String":
			return "testString-" + ThreadLocalRandom.current().nextInt(1);
			
		case "Integer":
		case "int":
			return ThreadLocalRandom.current().nextInt(1);
			
		case "java.lang.Long":
		case "long":
		case "java.lang.Double":
			return ThreadLocalRandom.current().nextLong(1, Integer.MAX_VALUE);
			
		case "java.lang.Boolean":
		case "boolean":
			return true;
			
		case "java.util.Date":
			return new Date();		
		}
		
		return null;
	}
}
