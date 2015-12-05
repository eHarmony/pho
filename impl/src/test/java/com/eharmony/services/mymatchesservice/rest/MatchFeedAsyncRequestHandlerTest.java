package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

public class MatchFeedAsyncRequestHandlerTest {

	@Test
	public void testFallback_noVoldyNoHbase_NO(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(!shouldFallback);
	}
	
	@Test
	public void testFallback_noVoldyWithHbase_YES(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);

		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedsByStatusGroup 
													= ctx.getHbaseFeedItemsByStatusGroup();
		
		Set<MatchDataFeedItemDto> feedSet = new HashSet<MatchDataFeedItemDto>();
		feedSet.add(new MatchDataFeedItemDto());
		feedsByStatusGroup.put(MatchStatusGroupEnum.NEW, feedSet);
		
		ctx.setHbaseFeedItemsByStatusGroup(feedsByStatusGroup);
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(shouldFallback);
	}
	
	@Test
	public void testFallback_VoldyNoHBase_NO(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
		
		long matchId = 11790420914L;
		LegacyMatchDataFeedDto legacy = new LegacyMatchDataFeedDto();
		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<>();
		matches.put(String.valueOf(matchId), new HashMap<String, Map<String, Object>>());
		legacy.setMatches(matches);
		
		long userId = 62837673;
		
		ctx.setLegacyMatchDataFeedDtoWrapper(new LegacyMatchDataFeedDtoWrapper(userId));
		ctx.getLegacyMatchDataFeedDtoWrapper().setLegacyMatchDataFeedDto(legacy);
		ctx.getLegacyMatchDataFeedDtoWrapper().setFeedAvailable(true);
		
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(!shouldFallback);
	}
	
	@Test
	public void testFallback_VoldyEmptyNoHBase_NO(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
		
		LegacyMatchDataFeedDto legacy = new LegacyMatchDataFeedDto();
		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<>();
		legacy.setMatches(matches);
		
		long userId = 62837673;
		
		ctx.setLegacyMatchDataFeedDtoWrapper(new LegacyMatchDataFeedDtoWrapper(userId));
		ctx.getLegacyMatchDataFeedDtoWrapper().setLegacyMatchDataFeedDto(legacy);
		ctx.getLegacyMatchDataFeedDtoWrapper().setFeedAvailable(true);
		
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(!shouldFallback);
	}

	@Test
	public void testFallback_VoldyEmptyAndHbase_YES(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);

		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedsByStatusGroup 
													= ctx.getHbaseFeedItemsByStatusGroup();		
		Set<MatchDataFeedItemDto> feedSet = new HashSet<MatchDataFeedItemDto>();
		feedSet.add(new MatchDataFeedItemDto());
		feedsByStatusGroup.put(MatchStatusGroupEnum.NEW, feedSet);
		
		LegacyMatchDataFeedDto legacy = new LegacyMatchDataFeedDto();
		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<>();
		legacy.setMatches(matches);
		
		long userId = 62837673;
		
		ctx.setLegacyMatchDataFeedDtoWrapper(new LegacyMatchDataFeedDtoWrapper(userId));
		ctx.getLegacyMatchDataFeedDtoWrapper().setLegacyMatchDataFeedDto(legacy);
		ctx.getLegacyMatchDataFeedDtoWrapper().setFeedAvailable(true);
		
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(shouldFallback);
	}
	
	@Test
	public void testFallback_VoldyAndHbase_NO(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);

		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedsByStatusGroup 
													= ctx.getHbaseFeedItemsByStatusGroup();		
		Set<MatchDataFeedItemDto> feedSet = new HashSet<MatchDataFeedItemDto>();
		feedSet.add(new MatchDataFeedItemDto());
		feedsByStatusGroup.put(MatchStatusGroupEnum.NEW, feedSet);
		
		long matchId = 11790420914L;
		LegacyMatchDataFeedDto legacy = new LegacyMatchDataFeedDto();
		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<>();
		matches.put(String.valueOf(matchId), new HashMap<String, Map<String, Object>>());
		legacy.setMatches(matches);
		
		long userId = 62837673;
		
		ctx.setLegacyMatchDataFeedDtoWrapper(new LegacyMatchDataFeedDtoWrapper(userId));
		ctx.getLegacyMatchDataFeedDtoWrapper().setLegacyMatchDataFeedDto(legacy);
		ctx.getLegacyMatchDataFeedDtoWrapper().setFeedAvailable(true);
		
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(!shouldFallback);
	}
}
