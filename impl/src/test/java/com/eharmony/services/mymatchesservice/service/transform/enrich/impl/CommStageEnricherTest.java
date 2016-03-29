package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.CommunicationStageResolver;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class CommStageEnricherTest {

	@Test
	public void testEmptyMatchStageIsSetToDefault() throws MalformedURLException, FileNotFoundException
	{
		
		LegacyMatchDataFeedDto legacyMatchDataFeedDto = new LegacyMatchDataFeedDto();

		Map<String, Map<String, Map<String, Object>>> matchesFromLegacyDataFeed = new HashMap<String, Map<String, Map<String, Object>>>();		
		Map<String, Map<String, Object>> matchInfo = new HashMap<String, Map<String, Object>>();		
		Map<String, Object> matchSection = new HashMap<String,Object>();
		Map<String, Object> commSection = new HashMap<String,Object>();
		
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, 123);
		
		matchInfo.put(MatchFeedModel.SECTIONS.MATCH, matchSection);
		matchInfo.put(MatchFeedModel.SECTIONS.COMMUNICATION, commSection);
		
		matchesFromLegacyDataFeed.put("matchInfo", matchInfo);
		
		
		legacyMatchDataFeedDto.setMatches(matchesFromLegacyDataFeed);
		
		MatchFeedRequestContext context = mock(MatchFeedRequestContext.class);
		when(context.getLegacyMatchDataFeedDto()).thenReturn(legacyMatchDataFeedDto);
		
		CommStageEnricher enricher = new CommStageEnricher();
		Whitebox.setInternalState(enricher, "commStageResolver", new CommunicationStageResolver());
				
		MatchFeedRequestContext response = enricher.processMatchFeed(context);
		
		Map<String, Map<String, Object>> oneMatch = response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo");		

		Assert.assertEquals(0, oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.STAGE));

		
	}
}
