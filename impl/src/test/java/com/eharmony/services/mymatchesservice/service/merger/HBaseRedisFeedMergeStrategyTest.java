package com.eharmony.services.mymatchesservice.service.merger;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
public class HBaseRedisFeedMergeStrategyTest {

	@Test
	public void testMerge_SameTime() throws Exception {
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		String matchId = "66531610";
		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();

		Map<String, Map<String, Object>> oneMatch = match.getMatches().get(matchId);
		long oldModifiedDate = (Long) oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate");
		merger.mergeMatchByTimestamp(matchId, oneMatch, oneMatch);
		assertEquals(oldModifiedDate, (long) oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate"));
	}

	@Test
	public void testMerge_TargetIsNewer() throws Exception {

		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		String matchId = "66531610";

		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();

		Map<String, Map<String, Object>> target = match.getMatches().get(matchId);
		target.get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE,
				System.currentTimeMillis());
		Map<String, Map<String, Object>> delta = match2.getMatches().get(matchId);
		long oldModifiedDate = (Long) target.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate");

		merger.mergeMatchByTimestamp(matchId, target, delta);

		assertEquals(oldModifiedDate, (long) target.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate"));
	}

	@Test
	public void testMerge_DeltaIsNewer() throws Exception {

		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		String matchId = "66531610";

		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();

		Map<String, Map<String, Object>> target = match.getMatches().get(matchId);
		Map<String, Map<String, Object>> delta = match2.getMatches().get(matchId);
		delta.get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE,
				System.currentTimeMillis());

		long newDate = (Long) delta.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate");

		merger.mergeMatchByTimestamp(matchId, target, delta);

		assertEquals(newDate, (long) target.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate"));
	}
	
	@Test
	public void testMerge_DeltaIsNewerAndStatusIsClosed() throws Exception {

		String MATCH_ID = "66531610";
		LegacyMatchDataFeedDto older = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto newer = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(older);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(newer);
		newer.getMatches().get(MATCH_ID).get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE, 
				System.currentTimeMillis());
		newer.getMatches().get(MATCH_ID).get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.STATUS, "closed");

		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();


		merger.merge(request);

		assertEquals(0, request.getLegacyMatchDataFeedDto().getMatches().size());


	}
	
	@Test
	public void testMerge_DeltaIsEmpty() throws Exception {

		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto match2 = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testMerge_HbaseIsEmpty() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto match = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testMerge_HbaseRedisAllEmpty() throws Exception {

		LegacyMatchDataFeedDto match2 = null;
		LegacyMatchDataFeedDto match = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();


		merger.merge(request);

		assertEquals(0, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testMerge_HbaseAllHasSameValue() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		match2.getMatches().get("66531610").get("match").put("stage", 6);

		match2.getMatches().get("66531610").get("match").put("lastModifiedDate", 1106200000000L);
		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
		//assert Delta has been applied to base
		assertEquals(6, request.getLegacyMatchDataFeedDto().getMatches().get("66531610").get("match").get("stage"));
	}
	
	@Test
	public void testMerge_HbaseHasDifferentValue() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed("json/singleMatchWithTimestamp.json");
		
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		Map<String, Map<String, Object>> matchesMap = match2.getMatches().get("66531610");

		match2.getMatches().put("66531611", matchesMap);
		match2.getMatches().remove("66531610");
		HBaseRedisFeedMergeStrategyImpl merger = new HBaseRedisFeedMergeStrategyImpl();


		merger.merge(request);

		//should have both 66531610 and 66531611
		assertEquals(2, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
}
