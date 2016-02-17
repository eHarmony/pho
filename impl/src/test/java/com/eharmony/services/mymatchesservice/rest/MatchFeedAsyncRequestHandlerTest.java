package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.container.AsyncResponse;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import rx.Observable;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedVoldyStore;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.google.common.collect.ImmutableSet;

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
	@Test
	public void testRedisHbase(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder
				.newInstance()
				.setUserId(62837673)
				.setStatuses(ImmutableSet.of("all"))
				.build();
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		RedisStoreFeedService redisStoreFeedService = mock(RedisStoreFeedService.class);
		LegacyMatchDataFeedDtoWrapper feed = new LegacyMatchDataFeedDtoWrapper(100L);
		Observable<LegacyMatchDataFeedDtoWrapper> observable = Observable.just(feed);
		when(redisStoreFeedService.getUserMatchesSafe(any())).thenReturn(observable);
		
		//ExecutorServiceProvider executorServiceProvider = mock(ExecutorServiceProvider.class);
		ExecutorServiceProvider executorServiceProvider =new ExecutorServiceProvider(1);
		
		HBaseStoreFeedService hbaseStoreFeedService = mock(HBaseStoreFeedService.class);
		MatchStatusGroupResolver matchStatusGroupResolver = new MatchStatusGroupResolver();
		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);
		ReflectionTestUtils.setField(handler, "redisMergeMode", true);
		ReflectionTestUtils.setField(handler, "executorServiceProvider", executorServiceProvider);
		ReflectionTestUtils.setField(handler, "matchStatusGroupResolver", matchStatusGroupResolver);
		ReflectionTestUtils.setField(handler, "redisStoreFeedService", redisStoreFeedService);
		ReflectionTestUtils.setField(handler, "hbaseStoreFeedService", hbaseStoreFeedService);
		ReflectionTestUtils.setField(handler, "voldemortStore", voldemortStore);
		ReflectionTestUtils.setField(handler, "redisSamplingPct", 100);
		
		AsyncResponse httpAsycRes = mock(AsyncResponse.class);
		handler.getMatchesFeed(queryCtx, httpAsycRes);
		verify(redisStoreFeedService).getUserMatchesSafe(any());
		verify(voldemortStore, never()).getMatchesObservableSafe(any());
	}
	
	@Test
	public void testRedisHbaseFalse(){
		
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder
				.newInstance()
				.setUserId(62837673)
				.setStatuses(ImmutableSet.of("all"))
				.build();
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		RedisStoreFeedService redisStoreFeedService = mock(RedisStoreFeedService.class);
		LegacyMatchDataFeedDtoWrapper feed = new LegacyMatchDataFeedDtoWrapper(100L);
		Observable<LegacyMatchDataFeedDtoWrapper> observable = Observable.just(feed);
		when(redisStoreFeedService.getUserMatchesSafe(any())).thenReturn(observable);
		
		//ExecutorServiceProvider executorServiceProvider = mock(ExecutorServiceProvider.class);
		ExecutorServiceProvider executorServiceProvider =new ExecutorServiceProvider(1);
		
		HBaseStoreFeedService hbaseStoreFeedService = mock(HBaseStoreFeedService.class);
		MatchStatusGroupResolver matchStatusGroupResolver = new MatchStatusGroupResolver();
		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);
		ReflectionTestUtils.setField(handler, "redisMergeMode", false);
		ReflectionTestUtils.setField(handler, "executorServiceProvider", executorServiceProvider);
		ReflectionTestUtils.setField(handler, "matchStatusGroupResolver", matchStatusGroupResolver);
		ReflectionTestUtils.setField(handler, "redisStoreFeedService", redisStoreFeedService);
		ReflectionTestUtils.setField(handler, "hbaseStoreFeedService", hbaseStoreFeedService);
		ReflectionTestUtils.setField(handler, "voldemortStore", voldemortStore);
		
		AsyncResponse httpAsycRes = mock(AsyncResponse.class);
		handler.getMatchesFeed(queryCtx, httpAsycRes);
		verify(redisStoreFeedService, never()).getUserMatchesSafe(any());
		verify(voldemortStore).getMatchesObservableSafe(any());
	}
	
	@Test
	public void testRedisSamplingEnabled(){
		
		MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		
		assertFalse(handler.isRedisSamplingEnabled(false, 10, 12345L)); // flag is false
		
		assertTrue(handler.isRedisSamplingEnabled(true, 10, 10000L));   // flag is true, in range
		assertTrue(handler.isRedisSamplingEnabled(true, 10, 10002L));   // flag is true, in range
		assertTrue(handler.isRedisSamplingEnabled(true, 10, 10004L));   // flag is true, in range
		assertTrue(handler.isRedisSamplingEnabled(true, 10, 10006L));   // flag is true, in range
		assertTrue(handler.isRedisSamplingEnabled(true, 10, 10008L));   // flag is true, in range
		assertTrue(handler.isRedisSamplingEnabled(true, 10, 10009L));   // flag is true, border of range
		assertFalse(handler.isRedisSamplingEnabled(true, 10, 10010L));  // flag is true, one step over
		
		// all accepted
		assertTrue(handler.isRedisSamplingEnabled(true, 100, 100L));
		assertTrue(handler.isRedisSamplingEnabled(true, 100, 0L));
		assertTrue(handler.isRedisSamplingEnabled(true, 100, 50L));
		
		// none accepted
		assertFalse(handler.isRedisSamplingEnabled(true, 0, 100L));
		assertFalse(handler.isRedisSamplingEnabled(true, 0, 0L));
		assertFalse(handler.isRedisSamplingEnabled(true, 0, 50L));

	}
}
