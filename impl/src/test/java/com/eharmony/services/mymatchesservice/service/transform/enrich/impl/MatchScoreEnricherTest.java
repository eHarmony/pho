package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.eharmony.matching.common.value.ScoredUserListProtoBuffs.ScoredUserListProto;
import com.eharmony.matching.common.value.ScoredUserProtoBuffs.ScoredUserProto;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.client.ScoreServiceClient;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;


@RunWith(MockitoJUnitRunner.class)
public class MatchScoreEnricherTest {
	
	@Mock
	private ScoreServiceClient scoreServiceClient;
	
	@Mock
	private MatchFeedRequestContext context;
	
	private MatchScoreEnricher enricher;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		enricher = new MatchScoreEnricher(scoreServiceClient);
	}
	
	@Test
	public void testNullContext()
	{
		enricher.processMatchFeed(null);
		assertTrue(true);
	}
	
	@Test
	public void testNullDto() throws MalformedURLException
	{
		
		LegacyMatchDataFeedDto legacyMatchDataFeedDto = new LegacyMatchDataFeedDto();

		Map<String, Map<String, Map<String, Object>>> matchesFromLegacyDataFeed = new HashMap<String, Map<String, Map<String, Object>>>();
		
		Map<String, Map<String, Object>> matchInfo = new HashMap<String, Map<String, Object>>();
		
		Map<String, Object> matchSection = new HashMap<String,Object>();
		
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, 123);
		matchSection.put(MatchFeedModel.MATCH.RELAXED, false);
		
		matchInfo.put(MatchFeedModel.SECTIONS.MATCH,matchSection);
		
		matchesFromLegacyDataFeed.put("matchInfo", matchInfo);
		
		
		legacyMatchDataFeedDto.setMatches(matchesFromLegacyDataFeed);
		
		when(context.getLegacyMatchDataFeedDto()).thenReturn(legacyMatchDataFeedDto);
		
		ScoredUserProto userProto = ScoredUserProto.newBuilder().setUserId(123).setScore(666).build();
		
		when(scoreServiceClient.scoreMatches(any(), any(), any())).thenReturn(ScoredUserListProto.newBuilder().addScoredUsers(userProto).build());
		
		MatchFeedRequestContext response = enricher.processMatchFeed(context);
		Long matchScore = (Long)response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo").get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE);
		Assert.assertEquals(1, response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo").size());
		Assert.assertEquals(666, matchScore.intValue());
		
	}
	
	
	@Test
	public void processMatchFeed_LongMatchedUserId() throws MalformedURLException
	{
		
		LegacyMatchDataFeedDto legacyMatchDataFeedDto = new LegacyMatchDataFeedDto();

		Map<String, Map<String, Map<String, Object>>> matchesFromLegacyDataFeed = new HashMap<String, Map<String, Map<String, Object>>>();
		
		Map<String, Map<String, Object>> matchInfo = new HashMap<String, Map<String, Object>>();
		
		Map<String, Object> matchSection = new HashMap<String,Object>();
		
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, 123L);
		matchSection.put(MatchFeedModel.MATCH.RELAXED, false);
		
		matchInfo.put(MatchFeedModel.SECTIONS.MATCH,matchSection);
		
		matchesFromLegacyDataFeed.put("matchInfo", matchInfo);
		
		
		legacyMatchDataFeedDto.setMatches(matchesFromLegacyDataFeed);
		
		when(context.getLegacyMatchDataFeedDto()).thenReturn(legacyMatchDataFeedDto);
		
		ScoredUserProto userProto = ScoredUserProto.newBuilder().setUserId(123).setScore(666).build();
		
		when(scoreServiceClient.scoreMatches(any(), any(), any())).thenReturn(ScoredUserListProto.newBuilder().addScoredUsers(userProto).build());
		
		MatchFeedRequestContext response = enricher.processMatchFeed(context);
		
		Long matchScore = (Long)response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo").get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE);
		Assert.assertEquals(1, response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo").size());
		Assert.assertEquals(666, matchScore.intValue());
		
	}
	
	@Test
	public void processMatchFeed_MissingMatchedUserId() throws MalformedURLException
	{
		
		LegacyMatchDataFeedDto legacyMatchDataFeedDto = new LegacyMatchDataFeedDto();

		Map<String, Map<String, Map<String, Object>>> matchesFromLegacyDataFeed = new HashMap<String, Map<String, Map<String, Object>>>();
		
		Map<String, Map<String, Object>> matchInfo = new HashMap<String, Map<String, Object>>();
		
		Map<String, Object> matchSection = new HashMap<String,Object>();
		
		matchSection.put(MatchFeedModel.MATCH.RELAXED, false);
		
		matchInfo.put(MatchFeedModel.SECTIONS.MATCH,matchSection);
		
		matchesFromLegacyDataFeed.put("matchInfo", matchInfo);
		
		
		legacyMatchDataFeedDto.setMatches(matchesFromLegacyDataFeed);
		
		when(context.getLegacyMatchDataFeedDto()).thenReturn(legacyMatchDataFeedDto);
		
		ScoredUserProto userProto = ScoredUserProto.newBuilder().setUserId(123).setScore(666).build();
		
		when(scoreServiceClient.scoreMatches(any(), any(), any())).thenReturn(ScoredUserListProto.newBuilder().addScoredUsers(userProto).build());
		
		MatchFeedRequestContext response = enricher.processMatchFeed(context);
		
		Long matchScore = (Long)response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo").get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE);
		Assert.assertEquals(1, response.getLegacyMatchDataFeedDto().getMatches().get("matchInfo").size());
		Assert.assertNull(matchScore);
		
	}
}
