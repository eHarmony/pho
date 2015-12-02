package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class MatchStatusFilterTest {


	
	private MatchFeedRequestContext doMatchStatusFilter(String fileName, String statuses) throws Exception{
		
		// build status list...
		String[] statusArr = StringUtils.split(statuses, ',');
		Set<String> statSet = new HashSet<String>(Arrays.asList(statusArr));
				
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(fileName);
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance()
									.setStatuses(statSet).build(); 
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
		LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(qctx.getUserId());
        legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(feed);
        ctx.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
		
		MatchStatusFilter filter = new MatchStatusFilter();
		return filter.processMatchFeed(ctx);
	}
	
	@Test
	public void testAll() throws Exception{ 
		
		MatchFeedRequestContext ctx = 
				doMatchStatusFilter("json/getMatches.json", MatchFeedModel.STATUS.ALL);		
		assertNotNull(ctx);
		assertEquals(1, ctx.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testNew_isInFeed() throws Exception{

		MatchFeedRequestContext ctx = 
				doMatchStatusFilter("json/getMatches.json", "new");		
		assertNotNull(ctx);
		assertEquals(1, ctx.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testClosed_notInFeed() throws Exception{

		MatchFeedRequestContext ctx = 
				doMatchStatusFilter("json/getMatches.json", "closed");		
		assertNotNull(ctx);
		assertEquals(0, ctx.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testEmptyFilter() throws Exception{

		MatchFeedRequestContext ctx = 
				doMatchStatusFilter("json/getMatches.json", "");		
		assertNotNull(ctx);
		assertEquals(0, ctx.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testClosed_someInFeed() throws Exception{

		MatchFeedRequestContext ctx = 
				doMatchStatusFilter("json/getMatches_someClosed.json", "closed");		
		assertNotNull(ctx);
		assertEquals(2, ctx.getLegacyMatchDataFeedDto().getMatches().size());
		assertTrue(ctx.getLegacyMatchDataFeedDto().getMatches().keySet().contains("11790321241"));
		assertTrue(ctx.getLegacyMatchDataFeedDto().getMatches().keySet().contains("11790321243"));
		assertTrue(!ctx.getLegacyMatchDataFeedDto().getMatches().keySet().contains("11790321246"));
	}
	
	@Test
	public void testNewAndClosed() throws Exception{

		MatchFeedRequestContext ctx = 
				doMatchStatusFilter("json/getMatches_someClosed.json", "new,closed");		
		assertNotNull(ctx);
		assertEquals(3, ctx.getLegacyMatchDataFeedDto().getMatches().size());
		assertTrue(ctx.getLegacyMatchDataFeedDto().getMatches().keySet().contains("11790321241"));
		assertTrue(ctx.getLegacyMatchDataFeedDto().getMatches().keySet().contains("11790321243"));
		assertTrue(ctx.getLegacyMatchDataFeedDto().getMatches().keySet().contains("11790321246"));
	}
}
