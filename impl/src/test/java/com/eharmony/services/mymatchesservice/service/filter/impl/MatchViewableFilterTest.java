package com.eharmony.services.mymatchesservice.service.filter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.singles.common.enumeration.MatchClosedStatusEnum;

public class MatchViewableFilterTest {

	//	null = 0 = OPEN so viewable
	//	4 = CLOSED_NOT_VIEWABLE, Not viewable
	//	If isUser and CLOSED_VIEWABLE_BY_CANDIDATE, not viewable
	//	If candidate and CLOSED_VIEWABLE_BY_USER, not viewable
	//	otherwise viewable
	
	private static final String MATCHID = "66531610";
	
	private MatchFeedRequestContext doMatchViewableFilter(String fileName, 
														MatchClosedStatusEnum matchStatus,
														boolean isUser,
														int expectedPostFilterFeedSize) throws Exception{
		
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(fileName);
		
		// prep the isUser, matchStatus attributes...
		Map<String,  Map<String, Object>> feedMap = feed.getMatches().get(MATCHID);
		Map<String, Object> matchSection = feedMap.get(MatchFeedModel.SECTIONS.MATCH);
		matchSection.put(MatchFeedModel.MATCH.IS_USER, isUser);
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchStatus.toInt());
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance()
										.build(); 
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
		ctx.setLegacyMatchDataFeedDto(feed);
		
		// run the filter...
		MatchViewableFilter filter = new MatchViewableFilter();
		MatchFeedRequestContext ret = filter.processMatchFeed(ctx);

		assertNotNull(ret);
		assertEquals(expectedPostFilterFeedSize, ret.getLegacyMatchDataFeedDto().getMatches().size());
		
		return ret;
	}
	

	//=======================================================================================//
	//  Non-Viewable Use Cases
	//=======================================================================================//

	@Test
	public void testLegacyClosedNotViewable() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_NOT_VIEWABLE, 
										true, 0);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_NOT_VIEWABLE, 
										false, 0);		
	}

	@Test
	public void testLegacyClosedUserViewable() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_USER_VIEWABLE, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_USER_VIEWABLE, 
										false, 0);		
	}

	@Test
	public void testLegacyClosedCandidateViewable() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_CANDIDATE_VIEWABLE, 
										true, 0);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_CANDIDATE_VIEWABLE, 
										false, 1);		
	}
	
	//=======================================================================================//
	//  Viewable Use Cases
	//=======================================================================================//

	@Test
	public void testClosedByNonInitializer() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_NON_INITIALIZER, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_NON_INITIALIZER, 
										false, 1);		
	}
	@Test
	public void testOpen() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.OPEN, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.OPEN, 
										false, 1);		
	}

	@Test
	public void testClosedByInitializer() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_INITIALIZER, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_INITIALIZER, 
										false, 1);		
	}

	@Test
	public void testClosedByBoth() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_BOTH, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_BOTH, 
										false, 1);		
	}

	@Test
	public void testLegacyClosedViewable() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_VIEWABLE, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.LEGACY_CLOSED_VIEWABLE, 
										false, 1);		
	}

	@Test
	public void testClosedByBothUserInitiated() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_BOTH_USER_INITIATED, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_BOTH_USER_INITIATED, 
										false, 1);		
	}

	@Test
	public void testClosedByBothCandidateInitiated() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_BOTH_CANDIDATE_INITIATED, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.CLOSED_BY_BOTH_CANDIDATE_INITIATED, 
										false, 1);		
	}

	@Test
	public void testReopenedByCandidate() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.REOPENED_BY_CANDIDATE, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.REOPENED_BY_CANDIDATE, 
										false, 1);		
	}

	@Test
	public void testReopenedByUser() throws Exception{ 
		
		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.REOPENED_BY_USER, 
										true, 1);		

		doMatchViewableFilter("json/getMatches.json", 
										MatchClosedStatusEnum.REOPENED_BY_USER, 
										false, 1);		
	}

	
}


