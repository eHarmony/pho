package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.eharmony.configuration.Configuration;
import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

@RunWith(MockitoJUnitRunner.class)
public class PhotoUrlEnricherTest {

	@Mock private Configuration 	configuration;
//	@Mock private PhotoItemBuilder 	photoBuilder;
	
	@InjectMocks private PhotoUrlEnricher enricher = new PhotoUrlEnricher();
	
	@Before
	public void setUp(){		
		MockitoAnnotations.initMocks(this);
	}
	
	private MatchFeedRequestContext doPhotoEnrichment(String fileName) throws Exception{
		
		// read in the feed...
		LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(fileName);
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance().build(); 
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
		LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(qctx.getUserId());
        legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(feed);
        ctx.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
				
		return enricher.processMatchFeed(ctx);
	}
	
	@Test
	public void testDelivered() throws Exception{ 
		
//		MatchFeedRequestContext ctx = 
//				doPhotoEnrichment("json/getMatches.json");		
//		assertNotNull(ctx);
//		assertEquals(1, ctx.getLegacyMatchDataFeedDto().getMatches().size());
//		
//		Map<String, Object> profileSection = ctx.getLegacyMatchDataFeedDto().getMatches().get("66531610").get(MatchFeedModel.SECTIONS.PROFILE);
//	
//		assertNull(profileSection.get(MatchFeedModel.PROFILE.PHOTO));
//		assertNotNull(profileSection.get(MatchFeedModel.PROFILE.PHOTOICON));
//		assertNotNull(profileSection.get(MatchFeedModel.PROFILE.PHOTOTHUMB));
//		assertEquals(Boolean.TRUE, (Boolean) profileSection.get(MatchFeedModel.PROFILE.HAS_PHOTO));
	}
}
