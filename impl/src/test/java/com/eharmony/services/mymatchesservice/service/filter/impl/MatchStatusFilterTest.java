package com.eharmony.services.mymatchesservice.service.filter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.service.filter.MatchFeedFilterContext;
import com.eharmony.services.mymatchesservice.service.filter.MatchFeedFilterContextBuilder;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class MatchStatusFilterTest {


	
	private MatchFeedFilterContext doMatchStatusFilter(String fileName, String statuses) throws Exception{
		
		// build status list...
		String[] statusArr = StringUtils.split(statuses, ',');
		Set<String> statSet = new HashSet<String>(Arrays.asList(statusArr));
				
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(fileName);
		
		// build the filter context...
		MatchFeedFilterContext ctx = 
				MatchFeedFilterContextBuilder.buildFilterContext(feed.getMatches(), statSet);
		
		MatchStatusFilter filter = new MatchStatusFilter();
		return filter.processMatchFeed(ctx);
	}
	
	@Test
	public void testAll() throws Exception{ 
		
		MatchFeedFilterContext ctx = 
				doMatchStatusFilter("json/getMatches.json", MatchFeedModel.STATUS.ALL);		
		assertNotNull(ctx);
		assertEquals(1, ctx.getFeedMap().size());
	}
	
	@Test
	public void testNew_isInFeed() throws Exception{

		MatchFeedFilterContext ctx = 
				doMatchStatusFilter("json/getMatches.json", "new");		
		assertNotNull(ctx);
		assertEquals(1, ctx.getFeedMap().size());
	}
	
	@Test
	public void testClosed_notInFeed() throws Exception{

		MatchFeedFilterContext ctx = 
				doMatchStatusFilter("json/getMatches.json", "closed");		
		assertNotNull(ctx);
		assertEquals(0, ctx.getFeedMap().size());
	}
}
