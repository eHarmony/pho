package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class PaginationMatchFeedFilterTest {

	
	private MatchFeedRequestContext doPaginationFilter(String feedFile, 
														int startPage, int pageSize) throws Exception{
		
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(feedFile);
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance()
										.setPageSize(pageSize).
										setStartPage(startPage).build(); 
		
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
		ctx.setLegacyMatchDataFeedDto(feed);
		
		PaginationMatchFeedFilter filter = new PaginationMatchFeedFilter();
		
		
		return filter.processMatchFeed(ctx);
	}
	
	@Test
	public void testPage1_size10() throws Exception{
		
		MatchFeedRequestContext paginated = doPaginationFilter("json/getMatches_40_matches.json", 1, 10);
		assertEquals(10, paginated.getLegacyMatchDataFeedDto().getMatches().size());
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getTotalMatches().intValue());
	}
	
	@Test
	public void testPage1_size100() throws Exception{
		
		MatchFeedRequestContext paginated = doPaginationFilter("json/getMatches_40_matches.json", 1, 100);
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getMatches().size());
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getTotalMatches().intValue());
	}

	@Test
	public void testPage2_size10() throws Exception{
		
		MatchFeedRequestContext paginated = doPaginationFilter("json/getMatches_40_matches.json", 2, 10);
		assertEquals(10, paginated.getLegacyMatchDataFeedDto().getMatches().size());
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getTotalMatches().intValue());
	}
	
	@Test
	public void testPage0() throws Exception{
		
		MatchFeedRequestContext paginated = doPaginationFilter("json/getMatches_40_matches.json", 0, 15);
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getMatches().size());
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getTotalMatches().intValue());
	}
	
	@Test
	public void testPage100_size100() throws Exception{
		
		MatchFeedRequestContext paginated = doPaginationFilter("json/getMatches_40_matches.json", 100, 100);
		assertEquals(0, paginated.getLegacyMatchDataFeedDto().getMatches().size());
		assertEquals(40, paginated.getLegacyMatchDataFeedDto().getTotalMatches().intValue());
	}
	
	@Test
	public void testPageNoPageParameters() throws Exception{
		
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed("json/getMatches_40_matches.json");
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance().build(); 
		
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
		ctx.setLegacyMatchDataFeedDto(feed);
		
		PaginationMatchFeedFilter filter = new PaginationMatchFeedFilter();		
		
		ctx = filter.processMatchFeed(ctx);
		assertEquals(40, ctx.getLegacyMatchDataFeedDto().getMatches().size());
		assertEquals(40, ctx.getLegacyMatchDataFeedDto().getTotalMatches().intValue());

	}
}
