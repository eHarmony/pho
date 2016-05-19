package com.eharmony.services.mymatchesservice.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.service.BasicStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.serializer.LegacyMatchDataFeedDtoSerializer;

public class MatchFeedRedisStoreTest {
	MatchFeedRedisStore redisStore;

	@SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
	public void testGetUserMatchesSafeFromRedis() throws Exception {
		long userid = 1234l;
		MatchFeedQueryContext context = MatchFeedQueryContextBuilder.newInstance().setUserId(userid).build();
		BasicStoreFeedRequestContext request = new BasicStoreFeedRequestContext(context );
		
		RedisTemplate<String, String> redisMatchDataTemplate = mock(RedisTemplate.class);
		MatchQueryMetricsFactroy matchQueryMetricsFactroy = mock(MatchQueryMetricsFactroy.class);
		LegacyMatchDataFeedDtoSerializer matchDataFeedSerializer = mock(LegacyMatchDataFeedDtoSerializer.class);
		redisStore = new MatchFeedRedisStore(redisMatchDataTemplate, matchDataFeedSerializer);
		ReflectionTestUtils.setField(redisStore, "matchQueryMetricsFactroy", matchQueryMetricsFactroy);
		
		HashOperations<String, Object, Object> hashOps = mock(HashOperations.class);
		when(redisMatchDataTemplate.opsForHash()).thenReturn(hashOps);
		
		
		Map feedMap = new HashMap<String, String> ();
		feedMap.put("66531610", "fakeJsonObj");
		when (hashOps.entries("1234")).thenReturn(feedMap);
		LegacyMatchDataFeedDto feedDto = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");;
		feedDto.setCreatedAt(new Date());
		
		when(matchDataFeedSerializer.fromJson(anyString())).thenReturn(feedDto);
		Timer.Context timerContext = mock(Timer.Context.class);
		
		when(matchQueryMetricsFactroy.getTimerContext(anyString(), anyString())).thenReturn(timerContext);
		LegacyMatchDataFeedDtoWrapper result = redisStore.getUserMatches(userid);
		assertNotNull(result);
		assertEquals(Integer.valueOf(1), result.getLegacyMatchDataFeedDto().getTotalMatches());
		timerContext.close();
	}

}
