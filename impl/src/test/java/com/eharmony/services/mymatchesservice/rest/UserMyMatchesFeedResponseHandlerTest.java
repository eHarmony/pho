package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class UserMyMatchesFeedResponseHandlerTest {
	
	private static final long USER_ID_GETMATCHES_40 = 62599283;

	private LegacyMatchDataFeedDto testFilter(long userId, 
											String jsonFile, 
											MatchFeedSearchAndFilterCriteria criteria) throws Exception{
		
		UserMyMatchesFeedResponseHandler responseHandler = new UserMyMatchesFeedResponseHandler();				
		LegacyMatchDataFeedDtoWrapper wrapper = getLegacyMatchDataFeedDtoWrapper(userId,
													jsonFile);
		LegacyMatchDataFeedDto matchFeed = wrapper.getLegacyMatchDataFeedDto();
		
		responseHandler.applySearchAndFilterCriteria(matchFeed, criteria);		
		return matchFeed;
	}
	
	@Test
	public void testFilter_EmptyCriteria() throws Exception{
				
		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance().build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(40, matchFeed.getMatches().size());
	}
	
	@Test
	public void testFilter_City() throws Exception{

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setCity("San Diego")
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(1, matchFeed.getMatches().size());
		Map<String, Map<String, Object>> oneMatch = matchFeed.getMatches().values().iterator().next();
		Map<String, Object> profileSection = oneMatch.get(MatchFeedModel.SECTIONS.PROFILE);
		assertEquals("San Diego", profileSection.get(MatchFeedModel.PROFILE.CITY));
	}

	@Test
	public void testFilter_FirstName() throws Exception{

		// full name match...
		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setName("Matcheduser23")
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(1, matchFeed.getMatches().size());
		
		// partial match...
		searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setName("user")
				.build();
		matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(40, matchFeed.getMatches().size());
	}

	@Test
	public void testFilter_Age() throws Exception{

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setAge(25)
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(2, matchFeed.getMatches().size());
	}

	@Test
	public void testFilter_Distance() throws Exception{

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setDistance(50)
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(39, matchFeed.getMatches().size());
	}
	
	@Test
	public void testFilter_AllFieldsExceptWildcard() throws Exception{

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setCity("Diego")
				.setHasPhotos(Boolean.FALSE)
				.setName("Matcheduser1")
				.setAge(34)
				.setDistance(50)
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(1, matchFeed.getMatches().size());
	}

	@Test
	public void testFilter_HasPhotos() throws Exception{

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setHasPhotos(Boolean.TRUE)
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);			
		assertEquals(0, matchFeed.getMatches().size());
		
		// explicitly keep matches with no photos
		searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setHasPhotos(Boolean.FALSE)
				.build();
		
		matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);			
		assertEquals(40, matchFeed.getMatches().size());
	}
	
	@Test
	public void testFilter_AnyText() throws Exception{

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setAnyTextField("Matcheduser23")
				.build();
		
		// Test name...
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);			
		assertEquals(1, matchFeed.getMatches().size());
		
		// Test city...
		searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setAnyTextField("Diego")
				.build();
		matchFeed = 
				testFilter(USER_ID_GETMATCHES_40, "json/getMatches_40_matches.json", searchFilterCriteria);			
		assertEquals(1, matchFeed.getMatches().size());
	}
	
    private LegacyMatchDataFeedDtoWrapper getLegacyMatchDataFeedDtoWrapper(long userId, String fileName) throws Exception {
        LegacyMatchDataFeedDtoWrapper wrapper = new LegacyMatchDataFeedDtoWrapper(userId);

        //setLegacyMatchDataFeedDto
        LegacyMatchDataFeedDto oneMatch = MatchTestUtils.getTestFeed(fileName);
        wrapper.setLegacyMatchDataFeedDto(oneMatch);
        wrapper.setFeedAvailable(true);

        return wrapper;
    }
}
