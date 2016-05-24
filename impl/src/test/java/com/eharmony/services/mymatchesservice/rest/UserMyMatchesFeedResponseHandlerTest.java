package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class UserMyMatchesFeedResponseHandlerTest {

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
				
		long userId = 62837673;

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance().build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(userId, "json/getMatches_40_matches.json", searchFilterCriteria);
			
		assertEquals(40, matchFeed.getMatches().size());
	}
	
	@Test
	public void testFilter_City() throws Exception{

		long userId = 62837673;

		MatchFeedSearchAndFilterCriteria searchFilterCriteria = 
				MatchFeedSearchAndFilterCriteriaBuilder.newInstance()
				.setCity("San Diego")
				.build();
		
		LegacyMatchDataFeedDto matchFeed = 
				testFilter(userId, "json/getMatches_40_matches.json", searchFilterCriteria);
			
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
