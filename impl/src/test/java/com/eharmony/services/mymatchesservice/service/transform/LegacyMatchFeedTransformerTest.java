package com.eharmony.services.mymatchesservice.service.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class LegacyMatchFeedTransformerTest {

	@Test
	public void testMatchRelaxedIsBoolean(){

		Long matchId = 9999L;

		Map<Integer, Boolean> relaxedCodes = new HashMap<>();
		relaxedCodes.put(0, false);
		relaxedCodes.put(1, true);


		MatchDataFeedItemDto hbaseFeedItem = new MatchDataFeedItemDto();
		hbaseFeedItem.getMatch().setMatchId(matchId);

		Set<MatchDataFeedItemDto> feedItems = new HashSet<>();
		feedItems.add(hbaseFeedItem);

		MatchFeedQueryContext query = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(1).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(query);
		request.setNewStoreFeed(feedItems);
		
		for(int relaxedCode : relaxedCodes.keySet()){
			hbaseFeedItem.getMatch().setRelaxed(relaxedCode);
	
			LegacyMatchDataFeedDto feed = 
					LegacyMatchFeedTransformer.transform( request);
			
			// get the Legacy sections...
			Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
			assertNotNull(matches);
			
			Map<String, Map<String, Object>> aMatch = matches.get(String.valueOf(matchId));
			assertNotNull(aMatch);
			
			Map<String, Object> section = aMatch.get(MatchFeedModel.SECTIONS.MATCH);
			assertNotNull(section);
			
			assertEquals(relaxedCodes.get(relaxedCode), section.get(MatchFeedModel.MATCH.RELAXED));
		}

	}
	
	
	@Test 
	public void testMatchedUserIdInMatch(){
		
		Long matchId = 9999L;
		
		MatchDataFeedItemDto hbaseFeedItem = new MatchDataFeedItemDto();
		hbaseFeedItem.getMatch().setUserId(1000L);
		hbaseFeedItem.getMatch().setMatchedUserId(1001L);
		hbaseFeedItem.getMatch().setMatchId(matchId);

		Set<MatchDataFeedItemDto> feedItems = new HashSet<>();
		feedItems.add(hbaseFeedItem);

		MatchFeedQueryContext query = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(1).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(query);
		request.setNewStoreFeed(feedItems);
		LegacyMatchDataFeedDto feed = 
				LegacyMatchFeedTransformer.transform( request);
		
		// get the Legacy sections...
		Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
		assertNotNull(matches);
		
		Map<String, Map<String, Object>> aMatch = matches.get(String.valueOf(matchId));
		assertNotNull(aMatch);
		
		Map<String, Object> section = aMatch.get(MatchFeedModel.SECTIONS.MATCH);
		assertNotNull(section);
		
		assertEquals(1001L, section.get(MatchFeedModel.MATCH.MATCHEDUSERID));
	}
	
	@Test 
	public void testAllFieldsNull(){
				
		MatchDataFeedItemDto hbaseFeedItem = new MatchDataFeedItemDto();

		Set<MatchDataFeedItemDto> feedItems = new HashSet<>();
		feedItems.add(hbaseFeedItem);

		MatchFeedQueryContext query = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(1).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(query);
		request.setNewStoreFeed(feedItems);
		LegacyMatchDataFeedDto feed = 
				LegacyMatchFeedTransformer.transform( request);
		
		// get the Legacy sections...
		Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
		assertNotNull(matches);

	}
	
	@Test
	public void testMatchStatusEnumMapping(){
		
		Long matchId = 9999L;
		
		Map<Integer, String> statusCodes = new HashMap<>();
	    //NEW(0, "NEW"), MYTURN(1, "MYTURN"), THEIRTURN(2, "THEIRTURN"), OPENCOMM(3, "OPENCOMM"), CLOSED(4, "CLOSED"), ARCHIVED(5, "ARCHIVED");

		statusCodes.put(0, "new");
		statusCodes.put(1, "myturn");
		statusCodes.put(2, "theirturn");
		statusCodes.put(3, "opencomm");
		statusCodes.put(4, "closed");
		statusCodes.put(5, "archived");
			
		MatchDataFeedItemDto hbaseFeedItem = new MatchDataFeedItemDto();
		hbaseFeedItem.getMatch().setMatchId(matchId);

		Set<MatchDataFeedItemDto> feedItems = new HashSet<>();
		feedItems.add(hbaseFeedItem);

		MatchFeedQueryContext query = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(1).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(query);
		request.setNewStoreFeed(feedItems);

		for(Integer statusId: statusCodes.keySet()){
			
			hbaseFeedItem.getMatch().setStatus(statusId);
			
			LegacyMatchDataFeedDto feed = 
					LegacyMatchFeedTransformer.transform( request);
			
			// get the Legacy sections...
			Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
			assertNotNull(matches);
			
			Map<String, Map<String, Object>> aMatch = matches.get(String.valueOf(matchId));
			assertNotNull(aMatch);
			
			Map<String, Object> section = aMatch.get(MatchFeedModel.SECTIONS.MATCH);
			assertNotNull(section);
			
			assertEquals(statusCodes.get(statusId).toLowerCase(), toLowerCase(section.get(MatchFeedModel.MATCH.STATUS)));
		}
	}
	
	private String toLowerCase(Object obj){
		
		return ((String)obj).toLowerCase();
	}
	
	@Test
	public void testBirthDateIsMillis(){

		Long matchId = 9999L;
		
		MatchDataFeedItemDto hbaseFeedItem = new MatchDataFeedItemDto();
		hbaseFeedItem.getMatch().setMatchId(matchId);

		Calendar birthDate = Calendar.getInstance();
		birthDate.roll(Calendar.YEAR, -21);
		hbaseFeedItem.getMatchedUser().setBirthdate(birthDate.getTime());
		
		Set<MatchDataFeedItemDto> feedItems = new HashSet<>();
		feedItems.add(hbaseFeedItem);

		MatchFeedQueryContext query = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(1).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(query);
		request.setNewStoreFeed(feedItems);
		LegacyMatchDataFeedDto feed = 
				LegacyMatchFeedTransformer.transform( request);
		
		// get the Legacy sections...
		Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
		assertNotNull(matches);
		
		Map<String, Map<String, Object>> aMatch = matches.get(String.valueOf(matchId));
		assertNotNull(aMatch);

		Map<String, Object> section = aMatch.get(MatchFeedModel.SECTIONS.PROFILE);
		assertNotNull(section);

		assertEquals(birthDate.getTimeInMillis(), section.get(MatchFeedModel.PROFILE.BIRTHDATE));

	}
	
	@Test
	public void testTransformCoversAllFields(){
		
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
		MatchFeedQueryContext query = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(1).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(query);
		request.setNewStoreFeed(feedItems);
		LegacyMatchDataFeedDto feed = 
				LegacyMatchFeedTransformer.transform( request);
		
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
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.LOCALE), null);
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.STATE_CODE), profileElem.getStateCode());
        assertEquals(profileSection.get(MatchFeedModel.PROFILE.BIRTHDATE), profileElem.getBirthdate().getTime());

        assertEquals(matchSection.get(MatchFeedModel.MATCH.ARCHIVE_STATUS), matchElem.getArchiveStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.CLOSED_DATE), matchElem.getClosedDate().getTime());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.CLOSED_STATUS), matchElem.getClosedStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.DELIVERED_DATE), matchElem.getDeliveredDate().getTime());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.DISTANCE), matchElem.getDistance());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.MATCHEDUSERID), matchElem.getMatchedUserId());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.ID), matchElem.getMatchId());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.ONE_WAY_STATUS), matchElem.getOneWayStatus());
        //assertEquals(matchSection.get(MatchFeedModel.MATCH.RELAXED), matchElem.getRelaxed());
        //assertEquals(matchSection.get(MatchFeedModel.MATCH.STATUS),  matchElem.getStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.USER_ID), matchElem.getUserId());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.IS_USER), matchElem.isMatchUser());

        assertEquals(matchSection.get(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE), commElem.getChooseMhcsDate().getTime());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.COMM_LAST_SENT), commElem.getCommLastSent());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.DISPLAY_TAB), commElem.getDisplayTab());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE), commElem.getFastTrackAvailable());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STAGE), commElem.getFastTrackStage());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STATUS), commElem.getFastTrackStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.ICEBREAKER_STATUS), commElem.getIcebreakerStatus());      
        assertEquals(matchSection.get(MatchFeedModel.MATCH.LAST_NUDGE_DATE), commElem.getLastNudgeDate().getTime());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.STAGE), commElem.getStage());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.COMM_STARTED_DATE), commElem.getCommStartedDate().getTime());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.NUDGE_STATUS), commElem.getNudgeStatus());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.READ_DETAILS_DATE), commElem.getReadDetailsDate().getTime());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.TURN_OWNER), commElem.getTurnOwner());
        assertEquals(matchSection.get(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB), commElem.getDisplayTab());

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
