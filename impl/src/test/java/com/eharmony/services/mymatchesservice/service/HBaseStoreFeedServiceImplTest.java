package com.eharmony.services.mymatchesservice.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

/**
 * This test case verifies that the Hbase record fetch limit is dictated by the matchFeedLimitsByStatusConfiguration and no other parameter controls the fetch behavior.
 * 
 * @author esrinivasan
 *
 */
public class HBaseStoreFeedServiceImplTest {

	private static final int FALLBACK_FEED_LIMIT = 10;

	private static final int FEED_LIMIT = 5;

	@Mock
	private MatchStoreQueryRepository queryRepository;

	@Mock
	private List<String> selectedProfileFields;

	@Mock
	private MatchFeedLimitsByStatusConfiguration matchFeedLimitsByStatusConfiguration;

	@Mock
	private MatchQueryMetricsFactroy matchQueryMetricsFactroy;

	@InjectMocks
	private HBaseStoreFeedServiceImpl hbaseStoreFeedService = new HBaseStoreFeedServiceImpl();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This test verifies that the Hbase query limit for fall back scenario is
	 * defined through matchFeedLimitsByStatusConfiguration.
	 * 
	 * @throws Exception
	 */
	@Test
	public void getUserMatchesByStatusGroupSafe_FallBackTest() throws Exception {

		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(100L)
		        .build();

		HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(queryCtx);
		Set<MatchStatusEnum> matchStatusEnum = new HashSet<MatchStatusEnum>();
		matchStatusEnum.add(MatchStatusEnum.NEW);
		requestContext.setMatchStatuses(matchStatusEnum);
		MatchStatusGroupEnum newGroupEnum = MatchStatusGroupEnum.NEW;
		requestContext.setMatchStatusGroup(newGroupEnum);

		when(matchQueryMetricsFactroy.getTimerContext(anyString(), anyString(), any(MatchStatusGroupEnum.class)))
		        .thenReturn(new Timer().time());

		Histogram histoGram = Mockito.mock(Histogram.class);

		when(matchQueryMetricsFactroy.getHistogram(anyString(), anyString(), any(MatchStatusGroupEnum.class)))
		        .thenReturn(histoGram);

		when(matchFeedLimitsByStatusConfiguration.getFallbackFeedLimitForGroup(newGroupEnum))
		        .thenReturn(FALLBACK_FEED_LIMIT);

		Set<MatchDataFeedItemDto> matchDataFeedItemSet = new HashSet<MatchDataFeedItemDto>();

		for (int i = 0; i < FALLBACK_FEED_LIMIT; i++) {
			matchDataFeedItemSet.add(new MatchDataFeedItemDto());
		}

		when(queryRepository.getMatchDataFeed(any())).thenReturn(matchDataFeedItemSet);

		requestContext.setFallbackRequest(true);

		hbaseStoreFeedService.getUserMatchesByStatusGroupSafe(requestContext).subscribe(response -> {
			Assert.assertEquals(FALLBACK_FEED_LIMIT, response.getHbaseStoreFeedItems().size());
			Assert.assertNull(response.getError());
		});

		ArgumentCaptor<MatchDataFeedQueryRequest> argument = ArgumentCaptor.forClass(MatchDataFeedQueryRequest.class);
		verify(queryRepository).getMatchDataFeed(argument.capture());

		Assert.assertEquals(FALLBACK_FEED_LIMIT, argument.getValue().getPageSize());

	}

	/**
	 * This test verifies that the Hbase query limit for non-fall back scenario
	 * is defined through matchFeedLimitsByStatusConfiguration.
	 * 
	 * @throws Exception
	 */
	@Test
	public void getUserMatchesByStatusGroupSafe_NonFallBackTest() throws Exception {

		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(100L)
		        .build();

		HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(queryCtx);
		Set<MatchStatusEnum> matchStatusEnum = new HashSet<MatchStatusEnum>();
		matchStatusEnum.add(MatchStatusEnum.NEW);
		requestContext.setMatchStatuses(matchStatusEnum);
		MatchStatusGroupEnum newGroupEnum = MatchStatusGroupEnum.NEW;
		requestContext.setMatchStatusGroup(newGroupEnum);

		when(matchQueryMetricsFactroy.getTimerContext(anyString(), anyString(), any(MatchStatusGroupEnum.class)))
		        .thenReturn(new Timer().time());

		Histogram histoGram = Mockito.mock(Histogram.class);

		when(matchQueryMetricsFactroy.getHistogram(anyString(), anyString(), any(MatchStatusGroupEnum.class)))
		        .thenReturn(histoGram);

		when(matchFeedLimitsByStatusConfiguration.getDefaultFeedLimitForGroup(newGroupEnum)).thenReturn(FEED_LIMIT);

		Set<MatchDataFeedItemDto> matchDataFeedItemSet = new HashSet<MatchDataFeedItemDto>();

		for (int i = 0; i < FEED_LIMIT; i++) {
			matchDataFeedItemSet.add(new MatchDataFeedItemDto());
		}

		when(queryRepository.getMatchDataFeed(any())).thenReturn(matchDataFeedItemSet);

		requestContext.setFallbackRequest(false);

		hbaseStoreFeedService.getUserMatchesByStatusGroupSafe(requestContext).subscribe(response -> {
			Assert.assertEquals(FEED_LIMIT, response.getHbaseStoreFeedItems().size());
			Assert.assertNull(response.getError());
		});

		ArgumentCaptor<MatchDataFeedQueryRequest> argument = ArgumentCaptor.forClass(MatchDataFeedQueryRequest.class);
		verify(queryRepository).getMatchDataFeed(argument.capture());

		Assert.assertEquals(FEED_LIMIT, argument.getValue().getPageSize());

	}

	/**
	 * This test verifies Exception handling
	 * 
	 * @throws Exception
	 */
	@Test
	public void getUserMatchesByStatusGroupSafe_Exception() throws Exception {

		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(100L)
		        .build();

		HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(queryCtx);
		Set<MatchStatusEnum> matchStatusEnum = new HashSet<MatchStatusEnum>();
		matchStatusEnum.add(MatchStatusEnum.NEW);
		requestContext.setMatchStatuses(matchStatusEnum);
		MatchStatusGroupEnum newGroupEnum = MatchStatusGroupEnum.NEW;
		requestContext.setMatchStatusGroup(newGroupEnum);

		when(matchQueryMetricsFactroy.getTimerContext(anyString(), anyString(), any(MatchStatusGroupEnum.class)))
		        .thenReturn(new Timer().time());

		Histogram histoGram = Mockito.mock(Histogram.class);

		when(matchQueryMetricsFactroy.getHistogram(anyString(), anyString(), any(MatchStatusGroupEnum.class)))
		        .thenReturn(histoGram);

		when(matchFeedLimitsByStatusConfiguration.getDefaultFeedLimitForGroup(newGroupEnum))
		        .thenThrow(new RuntimeException());

		Set<MatchDataFeedItemDto> matchDataFeedItemSet = new HashSet<MatchDataFeedItemDto>();

		for (int i = 0; i < FEED_LIMIT; i++) {
			matchDataFeedItemSet.add(new MatchDataFeedItemDto());
		}

		when(queryRepository.getMatchDataFeed(any())).thenReturn(matchDataFeedItemSet);

		requestContext.setFallbackRequest(false);

		hbaseStoreFeedService.getUserMatchesByStatusGroupSafe(requestContext).subscribe(response -> {
			Assert.assertNull(response.getHbaseStoreFeedItems());
			Assert.assertNotNull(response.getError());
		});

	}

}
