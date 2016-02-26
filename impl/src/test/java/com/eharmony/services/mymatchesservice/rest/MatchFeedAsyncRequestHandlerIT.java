package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import rx.Observable;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.event.EventConstant;
import com.eharmony.services.mymatchesservice.rest.internal.DataServiceThrottleManager;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyManager;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.service.merger.HBaseRedisFeedMergeStrategyImpl;
import com.eharmony.services.mymatchesservice.service.transform.HBASEToLegacyFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.LegacyMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedVoldyStore;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.eharmony.services.profile.client.ProfileServiceClient;
import com.eharmony.singles.common.profile.BasicPublicProfileDto;
import com.eharmony.singles.common.status.MatchStatus;


public class MatchFeedAsyncRequestHandlerIT {
	
	private static final String MATCHID_FROM_singleMatchWithTimestamp_json = "66531610";
	
    private LegacyMatchDataFeedDtoWrapper getLegacyMatchDataFeedDtoWrapper(
        long userId, Date lastModifiedDate) throws Exception {
        LegacyMatchDataFeedDtoWrapper wrapper = new LegacyMatchDataFeedDtoWrapper(userId);

        //setLegacyMatchDataFeedDto
        LegacyMatchDataFeedDto oneMatch = MatchTestUtils.getTestFeed(
                "json/singleMatchWithTimestamp.json");
        oneMatch.setUpdatedAt(lastModifiedDate);
        oneMatch.getMatches().get(MATCHID_FROM_singleMatchWithTimestamp_json).get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE,lastModifiedDate.getTime());
        oneMatch.getMatches().get(MATCHID_FROM_singleMatchWithTimestamp_json).get(MatchFeedModel.SECTIONS.PROFILE).put(MatchFeedModel.PROFILE.PHOTO_COUNT,10);
        wrapper.setLegacyMatchDataFeedDto(oneMatch);
        wrapper.setFeedAvailable(true);

        return wrapper;
    }
 
    
    private Set<MatchDataFeedItemDto> getHBaseData(Date lastModifiedDate){
 
        Set<MatchDataFeedItemDto> hbaseStoreFeedItems = new HashSet<MatchDataFeedItemDto>();

        MatchDataFeedItemDto feedItem = new MatchDataFeedItemDto();
        feedItem.getMatch().setMatchId(66531610L); // from 'json/singleMatchWithTimestamp.json'
        feedItem.getMatch().setMatchedUserId(4425406L);
        feedItem.getMatch().setLastModifiedDate(lastModifiedDate);

        hbaseStoreFeedItems.add(feedItem);
        
        return hbaseStoreFeedItems;
    }

    private HBaseStoreFeedResponse getHBaseStoreResponseWithMatch(
        long userId, Date lastModifiedDate) {
        HBaseStoreFeedResponse resp = new HBaseStoreFeedResponse(MatchStatusGroupEnum.NEW);

        Set<MatchDataFeedItemDto> hbaseStoreFeedItems = getHBaseData(lastModifiedDate);
        
        resp.setHbaseStoreFeedItems(hbaseStoreFeedItems);
        resp.setDataAvailable(true);

        return resp;
    }

    @Test
    public void testGetTeaserMatches_NoVoldy() throws Exception {
        int userId = 12345;

        Set<String> statusSet = new HashSet<String>();
        statusSet.add(MatchStatus.NEW.name());

        MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance()
                                                                     .setAllowedSeePhotos(true)
                                                                     .setPageSize(100) // For phase 1 setting 100 as the default number of records to fetch from HBASE. In V2, there will be DAO service for this.
            .setStartPage(1) //There will be no pagination. There will be only one page and the resultSize param will decide how many items it consists of.
            .setStatuses(statusSet).setUserId(userId).setTeaserResultSize(100) // This is the number of results to be returned back to the client/user.              
            .build();

        MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();

        Map<String, String> eventContextInfo = new HashMap<String, String>();

        eventContextInfo.put(EventConstant.USER_AGENT, "someUserAgent");
        eventContextInfo.put(EventConstant.PLATFORM, "somePlatform");

        Date lastModifiedHBaseDate = new Date();
        lastModifiedHBaseDate.setTime(System.currentTimeMillis() -
            (60 * 60 * 1000));

        Date lastModifiedRedisDate = new Date();

        RedisStoreFeedService redisStore = mock(RedisStoreFeedService.class);
        when(redisStore.getUserMatchesSafe(any()))
            .thenReturn(Observable.just(getLegacyMatchDataFeedDtoWrapper(userId,
                lastModifiedRedisDate)));

        HBaseStoreFeedService hbaseStore = mock(HBaseStoreFeedService.class);
        when(hbaseStore.getUserMatchesByStatusGroupSafe(any()))
            .thenReturn(Observable.just(getHBaseStoreResponseWithMatch(userId,
                lastModifiedHBaseDate)));

        ProfileServiceClient profileSvcClient = mock(ProfileServiceClient.class);

        BasicPublicProfileDto publicProfile = new BasicPublicProfileDto();
        publicProfile.setUserId(userId);
        publicProfile.setGender(1);
        when(profileSvcClient.findBasicPublicProfileForUser(any()))
            .thenReturn(publicProfile);

		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);

		ExecutorServiceProvider executorSP = new ExecutorServiceProvider(1);

		DataServiceThrottleManager throttle = new DataServiceThrottleManager(true, 100, "");
		Whitebox.setInternalState(handler, "throttle", throttle);
        Whitebox.setInternalState(handler, "hbaseStoreFeedService", hbaseStore);
        Whitebox.setInternalState(handler, "redisStoreFeedService", redisStore);
        Whitebox.setInternalState(handler, "profileService", profileSvcClient);
        Whitebox.setInternalState(handler, "executorServiceProvider",
            executorSP);
        Whitebox.setInternalState(handler, "hbaseToLegacyFeedTransformer",
            new HBASEToLegacyFeedTransformer());
        Whitebox.setInternalState(handler, "matchStatusGroupResolver",
            new MatchStatusGroupResolver());
		ReflectionTestUtils.setField(handler, "voldemortStore", voldemortStore);

        MockAsyncResponse response = new MockAsyncResponse();
        handler.getTeaserMatchesFeed(queryCtx, response, eventContextInfo);
                
		verify(redisStore).getUserMatchesSafe(any());		
		verify(hbaseStore).getUserMatchesByStatusGroupSafe(any());
		verify(voldemortStore, never()).getMatchesObservableSafe(any());

    }

    @Test
    public void testGetTeaserMatches_RedisIsNewest() throws Exception {
        int userId = 12345;

        Set<String> statusSet = new HashSet<String>();
        statusSet.add(MatchStatus.NEW.name().toLowerCase());

        MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance()
                                                                     .setAllowedSeePhotos(true)
                                                                     .setPageSize(100) // For phase 1 setting 100 as the default number of records to fetch from HBASE. In V2, there will be DAO service for this.
            .setStartPage(1) //There will be no pagination. There will be only one page and the resultSize param will decide how many items it consists of.
            .setStatuses(statusSet).setUserId(userId).setTeaserResultSize(100) // This is the number of results to be returned back to the client/user.              
            .build();

        // Make the HBase timestamp one day old
        Date lastModifiedHBaseDate = new Date();
        lastModifiedHBaseDate.setTime(System.currentTimeMillis() -
            (24 * 60 * 1000));
		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> newMatches = new HashMap<>();
		newMatches.put(MatchStatusGroupEnum.NEW, getHBaseData(lastModifiedHBaseDate));

		// Make the Redis timestamp current time of day
        Date lastModifiedRedisDate = new Date();

		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
		ctx.setFeedMergeType(FeedMergeStrategyType.HBASE_FEED_WITH_MATCH_MERGE);	
		ctx.setHbaseFeedItemsByStatusGroup(newMatches);

		// modify the Redis data to compare merge later
		LegacyMatchDataFeedDto redisData = getLegacyMatchDataFeedDtoWrapper(userId, lastModifiedRedisDate).getLegacyMatchDataFeedDto();
		redisData.getMatches().get(MATCHID_FROM_singleMatchWithTimestamp_json).get(MatchFeedModel.SECTIONS.COMMUNICATION).put(MatchFeedModel.COMMUNICATION.CAPTION,"REDIS_COMM_CAPTION");
		redisData.getMatches().get(MATCHID_FROM_singleMatchWithTimestamp_json).get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.FIRST_NAME,"REDIS_MATCH_FIRSTNAME");
		ctx.setRedisFeed(redisData);

		ctx.setLegacyMatchDataFeedDtoWrapper(getLegacyMatchDataFeedDtoWrapper(userId, lastModifiedHBaseDate));
		ctx.setFallbackRequest(false);
		
        MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		DataServiceThrottleManager throttle = new DataServiceThrottleManager(true, 100, "");
		Whitebox.setInternalState(handler, "throttle", throttle);

		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);

        RedisStoreFeedService redisStore = mock(RedisStoreFeedService.class);
        when(redisStore.getUserMatchesSafe(any()))
            .thenReturn(Observable.just(getLegacyMatchDataFeedDtoWrapper(userId,
                lastModifiedRedisDate)));

        HBaseStoreFeedService hbaseStore = mock(HBaseStoreFeedService.class);
        when(hbaseStore.getUserMatchesByStatusGroupSafe(any()))
            .thenReturn(Observable.just(getHBaseStoreResponseWithMatch(userId,
                lastModifiedHBaseDate)));

        ProfileServiceClient profileSvcClient = mock(ProfileServiceClient.class);

        BasicPublicProfileDto publicProfile = new BasicPublicProfileDto();
        publicProfile.setUserId(userId);
        publicProfile.setGender(1);
        when(profileSvcClient.findBasicPublicProfileForUser(any()))
            .thenReturn(publicProfile);
        
        
        // mock up handler's dependent services
        HBASEToLegacyFeedTransformer hbaseTransformer= new HBASEToLegacyFeedTransformer();
		Whitebox.setInternalState(hbaseTransformer, "legacyMatchFeedTransformer", new LegacyMatchFeedTransformer());
		
		FeedMergeStrategyManager feedMergeStrategy = new FeedMergeStrategyManager();
		Whitebox.setInternalState(feedMergeStrategy, "HBASE_WITH_REDIS_MERGE_STRATEGY", new HBaseRedisFeedMergeStrategyImpl());
		
		Whitebox.setInternalState(handler, "feedMergeStrategyManager", feedMergeStrategy);
        Whitebox.setInternalState(handler, "hbaseStoreFeedService", hbaseStore);
        Whitebox.setInternalState(handler, "redisStoreFeedService", redisStore);
        Whitebox.setInternalState(handler, "profileService", profileSvcClient);
        ReflectionTestUtils.setField(handler, "hbaseToLegacyFeedTransformer", hbaseTransformer);
        Whitebox.setInternalState(handler, "executorServiceProvider",
            new ExecutorServiceProvider(1));
        Whitebox.setInternalState(handler, "matchStatusGroupResolver",
            new MatchStatusGroupResolver());
		ReflectionTestUtils.setField(handler, "voldemortStore", voldemortStore);

		// pull in context for filter chains
    	ApplicationContext context = new ClassPathXmlApplicationContext("data-transformation-context-test.xml");
    	Whitebox.setInternalState(handler, "getTeaserMatchesFeedFilterChain", context.getBean("getTeaserMatchesFeedFilterChain"));
    	Whitebox.setInternalState(handler, "getMatchesFeedEnricherChain", context.getBean("getMatchesFeedEnricherChain"));
		
    	// This is the test's target call
		Whitebox.invokeMethod(handler, "handleTeaserFeedResponse", ctx);

		Map<String, Map<String, Map<String, Object>>> matches = ctx.getLegacyMatchDataFeedDto().getMatches();
		Map<String, Map<String, Object>> oneMatch = matches.get(MATCHID_FROM_singleMatchWithTimestamp_json);
		assertNotNull(oneMatch);
		
		assertEquals(lastModifiedRedisDate.getTime(), oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.LAST_MODIFIED_DATE));
		
		// check that match and comm sections have Redis data.
		assertEquals("REDIS_MATCH_FIRSTNAME", oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.FIRST_NAME));
		assertEquals("REDIS_COMM_CAPTION", oneMatch.get(MatchFeedModel.SECTIONS.COMMUNICATION).get(MatchFeedModel.COMMUNICATION.CAPTION));
		
    }
    
    
    @Test
    public void testGetTeaserMatches_NoRedisData() throws Exception {
        int userId = 12345;

        Set<String> statusSet = new HashSet<String>();
        statusSet.add(MatchStatus.NEW.name().toLowerCase());

        MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance()
                                                                     .setAllowedSeePhotos(true)
                                                                     .setPageSize(100) 
														              .setStartPage(1)
														              .setStatuses(statusSet).setUserId(userId).setTeaserResultSize(100)               
														              .build();

        Date lastModifiedHBaseDate = new Date();
		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> newMatches = new HashMap<>();
		newMatches.put(MatchStatusGroupEnum.NEW, getHBaseData(lastModifiedHBaseDate));

		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
		ctx.setFeedMergeType(FeedMergeStrategyType.HBASE_FEED_WITH_MATCH_MERGE);	
		ctx.setHbaseFeedItemsByStatusGroup(newMatches);
		ctx.setRedisFeed(null);

		ctx.setLegacyMatchDataFeedDtoWrapper(getLegacyMatchDataFeedDtoWrapper(userId, lastModifiedHBaseDate));
		ctx.setFallbackRequest(false);
		
        MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();

		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);

		// redis returns empty data
        RedisStoreFeedService redisStore = mock(RedisStoreFeedService.class);
        when(redisStore.getUserMatchesSafe(any()))
            .thenReturn(Observable.just(new LegacyMatchDataFeedDtoWrapper(userId)));

        HBaseStoreFeedService hbaseStore = mock(HBaseStoreFeedService.class);
        when(hbaseStore.getUserMatchesByStatusGroupSafe(any()))
            .thenReturn(Observable.just(getHBaseStoreResponseWithMatch(userId,
                lastModifiedHBaseDate)));

        ProfileServiceClient profileSvcClient = mock(ProfileServiceClient.class);

        BasicPublicProfileDto publicProfile = new BasicPublicProfileDto();
        publicProfile.setUserId(userId);
        publicProfile.setGender(1);
        when(profileSvcClient.findBasicPublicProfileForUser(any()))
            .thenReturn(publicProfile);
        
        
        // mock up handler's dependent services
        HBASEToLegacyFeedTransformer hbaseTransformer= new HBASEToLegacyFeedTransformer();
		Whitebox.setInternalState(hbaseTransformer, "legacyMatchFeedTransformer", new LegacyMatchFeedTransformer());

		DataServiceThrottleManager throttle = new DataServiceThrottleManager(true, 100, "");
		Whitebox.setInternalState(handler, "throttle", throttle);
		
		FeedMergeStrategyManager feedMergeStrategy = new FeedMergeStrategyManager();
		Whitebox.setInternalState(feedMergeStrategy, "HBASE_WITH_REDIS_MERGE_STRATEGY", new HBaseRedisFeedMergeStrategyImpl());
		
		Whitebox.setInternalState(handler, "feedMergeStrategyManager", feedMergeStrategy);
        Whitebox.setInternalState(handler, "hbaseStoreFeedService", hbaseStore);
        Whitebox.setInternalState(handler, "redisStoreFeedService", redisStore);
        Whitebox.setInternalState(handler, "profileService", profileSvcClient);
        ReflectionTestUtils.setField(handler, "hbaseToLegacyFeedTransformer", hbaseTransformer);
        Whitebox.setInternalState(handler, "executorServiceProvider",
            new ExecutorServiceProvider(1));
        Whitebox.setInternalState(handler, "matchStatusGroupResolver",
            new MatchStatusGroupResolver());
		ReflectionTestUtils.setField(handler, "voldemortStore", voldemortStore);

		// pull in context for filter chains
    	ApplicationContext context = new ClassPathXmlApplicationContext("data-transformation-context-test.xml");
    	Whitebox.setInternalState(handler, "getTeaserMatchesFeedFilterChain", context.getBean("getTeaserMatchesFeedFilterChain"));
    	Whitebox.setInternalState(handler, "getMatchesFeedEnricherChain", context.getBean("getMatchesFeedEnricherChain"));
		
    	// This is the test's target call
		Whitebox.invokeMethod(handler, "handleTeaserFeedResponse", ctx);

		Map<String, Map<String, Map<String, Object>>> matches = ctx.getLegacyMatchDataFeedDto().getMatches();
		Map<String, Map<String, Object>> oneMatch = matches.get(MATCHID_FROM_singleMatchWithTimestamp_json);
		assertNotNull(oneMatch);
				
    }
    
    
    @Test
    public void testGetMatches_RedisIsNewestAndMatchClosed() throws Exception {
        int userId = 12345;

        Set<String> statusSet = new HashSet<String>();
        statusSet.add(MatchStatus.NEW.name().toLowerCase());

        MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance()
                                                                     .setAllowedSeePhotos(true)
                                                                     .setPageSize(100) // For phase 1 setting 100 as the default number of records to fetch from HBASE. In V2, there will be DAO service for this.
            .setStartPage(1) //There will be no pagination. There will be only one page and the resultSize param will decide how many items it consists of.
            .setStatuses(statusSet).setUserId(userId).setTeaserResultSize(100) // This is the number of results to be returned back to the client/user.              
            .build();

        // Make the HBase timestamp one day old
        Date lastModifiedHBaseDate = new Date();
        lastModifiedHBaseDate.setTime(System.currentTimeMillis() -
            (24 * 60 * 1000));
		Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> newMatches = new HashMap<>();
		newMatches.put(MatchStatusGroupEnum.NEW, getHBaseData(lastModifiedHBaseDate));

		// Make the Redis timestamp current time of day
        Date lastModifiedRedisDate = new Date();

		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
		ctx.setFeedMergeType(FeedMergeStrategyType.HBASE_FEED_WITH_MATCH_MERGE);	
		ctx.setHbaseFeedItemsByStatusGroup(newMatches);

		// Set the Redis match state to closed
		LegacyMatchDataFeedDto redisData = getLegacyMatchDataFeedDtoWrapper(userId, lastModifiedRedisDate).getLegacyMatchDataFeedDto();
		redisData.getMatches().get(MATCHID_FROM_singleMatchWithTimestamp_json)
								.get(MatchFeedModel.SECTIONS.MATCH)
									.put(MatchFeedModel.MATCH.STATUS,"closed");
		ctx.setRedisFeed(redisData);

		ctx.setLegacyMatchDataFeedDtoWrapper(getLegacyMatchDataFeedDtoWrapper(userId, lastModifiedHBaseDate));
		ctx.setFallbackRequest(false);
		
        MatchFeedAsyncRequestHandler handler = new MatchFeedAsyncRequestHandler();
		DataServiceThrottleManager throttle = new DataServiceThrottleManager(true, 100, "");
		Whitebox.setInternalState(handler, "throttle", throttle);

		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);

        RedisStoreFeedService redisStore = mock(RedisStoreFeedService.class);
        when(redisStore.getUserMatchesSafe(any()))
            .thenReturn(Observable.just(getLegacyMatchDataFeedDtoWrapper(userId,
                lastModifiedRedisDate)));

        HBaseStoreFeedService hbaseStore = mock(HBaseStoreFeedService.class);
        when(hbaseStore.getUserMatchesByStatusGroupSafe(any()))
            .thenReturn(Observable.just(getHBaseStoreResponseWithMatch(userId,
                lastModifiedHBaseDate)));

        ProfileServiceClient profileSvcClient = mock(ProfileServiceClient.class);

        BasicPublicProfileDto publicProfile = new BasicPublicProfileDto();
        publicProfile.setUserId(userId);
        publicProfile.setGender(1);
        when(profileSvcClient.findBasicPublicProfileForUser(any()))
            .thenReturn(publicProfile);
        
        
        // mock up handler's dependent services
        HBASEToLegacyFeedTransformer hbaseTransformer= new HBASEToLegacyFeedTransformer();
		Whitebox.setInternalState(hbaseTransformer, "legacyMatchFeedTransformer", new LegacyMatchFeedTransformer());
		
		FeedMergeStrategyManager feedMergeStrategy = new FeedMergeStrategyManager();
		Whitebox.setInternalState(feedMergeStrategy, "HBASE_WITH_REDIS_MERGE_STRATEGY", new HBaseRedisFeedMergeStrategyImpl());
		
		Whitebox.setInternalState(handler, "feedMergeStrategyManager", feedMergeStrategy);
        Whitebox.setInternalState(handler, "hbaseStoreFeedService", hbaseStore);
        Whitebox.setInternalState(handler, "redisStoreFeedService", redisStore);
        Whitebox.setInternalState(handler, "profileService", profileSvcClient);
        ReflectionTestUtils.setField(handler, "hbaseToLegacyFeedTransformer", hbaseTransformer);
        Whitebox.setInternalState(handler, "executorServiceProvider",
            new ExecutorServiceProvider(1));
        Whitebox.setInternalState(handler, "matchStatusGroupResolver",
            new MatchStatusGroupResolver());
		ReflectionTestUtils.setField(handler, "voldemortStore", voldemortStore);

		// pull in context for filter chains
    	ApplicationContext context = new ClassPathXmlApplicationContext("data-transformation-context-test.xml");
    	Whitebox.setInternalState(handler, "getMatchesFeedFilterChain", context.getBean("getMatchesFeedFilterChain"));
    	Whitebox.setInternalState(handler, "getMatchesFeedEnricherChain", context.getBean("getMatchesFeedEnricherChain"));
		
    	// This is the test's target call
		Whitebox.invokeMethod(handler, "handleFeedResponse", ctx);

		// match should have been filtered out.
		Map<String, Map<String, Map<String, Object>>> matches = ctx.getLegacyMatchDataFeedDto().getMatches();
		Map<String, Map<String, Object>> oneMatch = matches.get(MATCHID_FROM_singleMatchWithTimestamp_json);
		assertNull(oneMatch);
				
    }
}
