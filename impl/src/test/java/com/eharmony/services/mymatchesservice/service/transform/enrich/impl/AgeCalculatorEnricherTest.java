package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class AgeCalculatorEnricherTest {

    
	private MatchFeedRequestContext doAgeCalculatorEnrichment(String fileName, long timestamp) throws Exception{
		
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(fileName);
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance().build(); 
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
		LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(qctx.getUserId());
        legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(feed);
        ctx.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
		
		AgeCalculatorEnricher filter = new AgeCalculatorEnricher();
		return filter.processMatchFeed(ctx);
	}
	
	@Test
	public void testDelivered() throws Exception{ 
		
		MatchFeedRequestContext ctx = 
				doAgeCalculatorEnrichment("json/getMatches.json", 1447360338555L);		
		assertNotNull(ctx);
		
		Map<String, Object> profileSection = 
				ctx.getLegacyMatchDataFeedDto().getMatches().get("66531610").get(MatchFeedModel.SECTIONS.PROFILE);
		//TODO FIXME
		//assertEquals(39, profileSection.get(MatchFeedModel.PROFILE.AGE));
	}
}
