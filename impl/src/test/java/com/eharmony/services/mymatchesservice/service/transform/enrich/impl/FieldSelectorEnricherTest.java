package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.enrich.impl.FieldSelectorEnricher.SelectionListType;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;


public class FieldSelectorEnricherTest {
    private MatchFeedRequestContext doFieldSelectorEnrichment(String feedFile,
													        String section, 
													        List<String> list, 
													        SelectionListType listType)
													        throws Exception {
    	
    	
        // read in the feed...
        LegacyMatchDataFeedDto feed = MatchTestUtils.getTestFeed(feedFile);

        // build the filter context...
        MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder.newInstance().setUserId(1234L)
                                                                 .build();

        MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);
        LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(qctx.getUserId());
        legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(feed);
        ctx.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);

        FieldSelectorEnricher enricher = new FieldSelectorEnricher(section, list, listType.toString());

        return enricher.processMatchFeed(ctx);
    }

    @Test
    public void testMatchedUser_Blacklist() throws Exception {
    	
    	String [] blacklist = {"stateCode", "photo"};
    	
        MatchFeedRequestContext blacklisted = doFieldSelectorEnrichment(
                "json/getMatches_40_matches.json", MatchFeedModel.SECTIONS.PROFILE, 
                Arrays.asList(blacklist), SelectionListType.BLACKLIST);
        
        assertEquals(40, blacklisted.getLegacyMatchDataFeedDto().getMatches().size());
        
        Set<String> matchIds = blacklisted.getLegacyMatchDataFeedDto().getMatches().keySet();
        for(String matchId: matchIds){
        	
        	Map<String, Map<String, Object>> match = blacklisted.getLegacyMatchDataFeedDto().getMatches().get(matchId);
        	
        	Map<String, Object> section = match.get(MatchFeedModel.SECTIONS.PROFILE);
        	assertNull(section.get("stateCode"));
        	assertNull(section.get("photo"));
        }
    }
    
    @Test
    public void testMatchedUser_Whitelist() throws Exception {
    	
    	String [] whitelist = {"userId", "birthdate", "age"};
    	
        MatchFeedRequestContext whitelisted = doFieldSelectorEnrichment(
                "json/getMatches_40_matches.json", MatchFeedModel.SECTIONS.PROFILE, 
                Arrays.asList(whitelist), SelectionListType.WHITELIST);    
        
        assertEquals(40, whitelisted.getLegacyMatchDataFeedDto().getMatches().size());
        Set<String> matchIds = whitelisted.getLegacyMatchDataFeedDto().getMatches().keySet();
        for(String matchId: matchIds){
        	
        	Map<String, Map<String, Object>> match = whitelisted.getLegacyMatchDataFeedDto().getMatches().get(matchId);
        	
        	Map<String, Object> section = match.get(MatchFeedModel.SECTIONS.PROFILE);
        	assertNotNull(section.get("userId"));
        	assertNotNull(section.get("birthdate"));
        	assertNotNull(section.get("age"));
        	
        	assertNull(section.get("city"));
        	assertNull(section.get("country"));
        	assertNull(section.get("firstName"));
        	assertNull(section.get("gender"));
        	assertNull(section.get("stateCode"));
        	assertNull(section.get("version"));
        	assertNull(section.get("photo"));
        }

    }
}
