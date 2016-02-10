package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.event.EventConstant;
import com.eharmony.services.mymatchesservice.event.MatchQueryEventService;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.eharmony.singles.common.status.MatchStatus;

public class MatchFeedAsyncRequestHandlerTest {
	
	@Test
	public void testGetTeaserMatches(){
		
		long userId = 12345L;
		
		Set<String> statusSet = new HashSet<String>();
		statusSet.add(MatchStatus.NEW.name());
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setAllowedSeePhotos(true)
                .setPageSize(100)   // For phase 1 setting 100 as the default number of records to fetch from HBASE. In V2, there will be DAO service for this.
                .setStartPage(1)  //There will be no pagination. There will be only one page and the resultSize param will decide how many items it consists of.
                .setStatuses(statusSet)
                .setUserId(userId)
                .setTeaserResultSize(100)        // This is the number of results to be returned back to the client/user.              
                .build();
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();	
		
        Map<String,String> eventContextInfo = new HashMap<String,String>();
        
    	eventContextInfo.put(EventConstant.USER_AGENT, "someUserAgent");
    	eventContextInfo.put(EventConstant.PLATFORM, "somePlatform");
		
    	Whitebox.setInternalState(handler, "redisMergeMode", true);
    	Whitebox.setInternalState(handler, "matchQueryEventService", mock(MatchQueryEventService.class));
    	Whitebox.setInternalState(handler, "redisStoreFeedService", mock(RedisStoreFeedService.class));
 
		AsyncResponse response = mock(AsyncResponse.class);
		handler.getTeaserMatchesFeed(queryCtx, response, eventContextInfo);
	}
	
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
	public void testFallback_VoldyNoHbase_NO(){
		
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
	public void testFallback_VoldyEmptyNoHbase_NO(){
		
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
		feedsByStatusGroup.put(MatchStatusGroupEnum.ARCHIVE, feedSet);
		feedsByStatusGroup.put(MatchStatusGroupEnum.COMMUNICATION, feedSet);
		
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
	public void testFallback_VoldyEmptyAndHbaseEmpty_NO(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);

		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedsByStatusGroup 
													= ctx.getHbaseFeedItemsByStatusGroup();		

		feedsByStatusGroup.put(MatchStatusGroupEnum.NEW, null);
		feedsByStatusGroup.put(MatchStatusGroupEnum.ARCHIVE, null);
		feedsByStatusGroup.put(MatchStatusGroupEnum.COMMUNICATION, null);
		
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
	
	@Test
	public void testFallback_VoldyHasDataButErrorAndHbase_YES(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);

		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedsByStatusGroup 
													= ctx.getHbaseFeedItemsByStatusGroup();		
		Set<MatchDataFeedItemDto> feedSet = new HashSet<MatchDataFeedItemDto>();
		feedSet.add(new MatchDataFeedItemDto());
		feedsByStatusGroup.put(MatchStatusGroupEnum.NEW, feedSet);
		
		//long matchId = 11790420914L;
		LegacyMatchDataFeedDto legacy = new LegacyMatchDataFeedDto();
		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<>();
		//matches.put(String.valueOf(matchId), new HashMap<String, Map<String, Object>>());
		legacy.setMatches(matches);
		
		long userId = 62837673;
		
		ctx.setLegacyMatchDataFeedDtoWrapper(new LegacyMatchDataFeedDtoWrapper(userId));
		ctx.getLegacyMatchDataFeedDtoWrapper().setLegacyMatchDataFeedDto(legacy);
		ctx.getLegacyMatchDataFeedDtoWrapper().setFeedAvailable(false);
		ctx.getLegacyMatchDataFeedDtoWrapper().setError(new Exception());
		
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		boolean shouldFallback = handler.shouldFallbackToHBase(ctx);
		
		assertTrue(shouldFallback);
	}
}
