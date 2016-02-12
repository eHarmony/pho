package com.eharmony.services.mymatchesservice.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.test.util.ReflectionTestUtils;

import rx.Observable;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.event.EventConstant;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.transform.HBASEToLegacyFeedTransformer;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedVoldyStore;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.eharmony.services.profile.client.ProfileServiceClient;
import com.eharmony.singles.common.profile.BasicPublicProfileDto;
import com.eharmony.singles.common.status.MatchStatus;
import com.google.common.collect.ImmutableSet;


public class MatchFeedAsyncRequestHandlerIT {
    private Observable<LegacyMatchDataFeedDtoWrapper> getObservableContextWithMatch(
        long userId, Date lastModifiedDate) throws Exception {
        LegacyMatchDataFeedDtoWrapper wrapper = new LegacyMatchDataFeedDtoWrapper(userId);

        //setLegacyMatchDataFeedDto
        LegacyMatchDataFeedDto oneMatch = MatchTestUtils.getTestFeed(
                "json/singleMatchWithTimestamp.json");
        oneMatch.setUpdatedAt(lastModifiedDate);
        wrapper.setLegacyMatchDataFeedDto(oneMatch);
        wrapper.setFeedAvailable(true);

        return Observable.just(wrapper);
    }

    private Observable<HBaseStoreFeedResponse> getObservableHBaseStoreResponseWithMatch(
        long userId, Date lastModifiedDate) {
        HBaseStoreFeedResponse resp = new HBaseStoreFeedResponse(MatchStatusGroupEnum.NEW);

        Set<MatchDataFeedItemDto> hbaseStoreFeedItems = new HashSet<MatchDataFeedItemDto>();

        MatchDataFeedItemDto feedItem = new MatchDataFeedItemDto();
        feedItem.getMatch().setMatchId(66531610L); // from 'json/singleMatchWithTimestamp.json'
        feedItem.getMatch().setMatchedUserId(4425406L);
        feedItem.getMatch().setLastModifiedDate(lastModifiedDate);

        hbaseStoreFeedItems.add(feedItem);
        resp.setHbaseStoreFeedItems(hbaseStoreFeedItems);
        resp.setDataAvailable(true);

        return Observable.just(resp);
    }

    @Test
    public void testGetTeaserMatches_RedisIsNewest() throws Exception {
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
            .thenReturn(getObservableContextWithMatch(userId,
                lastModifiedRedisDate));

        HBaseStoreFeedService hbaseStore = mock(HBaseStoreFeedService.class);
        when(hbaseStore.getUserMatchesByStatusGroupSafe(any()))
            .thenReturn(getObservableHBaseStoreResponseWithMatch(userId,
                lastModifiedHBaseDate));

        ProfileServiceClient profileSvcClient = mock(ProfileServiceClient.class);

        BasicPublicProfileDto publicProfile = new BasicPublicProfileDto();
        publicProfile.setUserId(userId);
        publicProfile.setGender(1);
        when(profileSvcClient.findBasicPublicProfileForUser(any()))
            .thenReturn(publicProfile);

		MatchDataFeedVoldyStore voldemortStore = mock(MatchDataFeedVoldyStore.class);

		ExecutorServiceProvider executorSP = new ExecutorServiceProvider(1);

		Whitebox.setInternalState(handler, "redisMergeMode", true);
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
	
}
