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

	public static final String SINGLE_MATCH_JSON_FILENAME = "json/singleMatchWithTimestamp.json";
	public static final String TWO_MATCHES_JSON_FILENAME = "json/twoMatchesWithTimestamp.json";
	public static final String FORTY_MATCHES_JSON_FILENAME = "json/getMatches_40_matches.json";

	// Match ID for single match in SINGLE_MATCH_JSON_FILENAME
	public static final String SINGLE_MATCH_ID = "66531610";
	public static final String SINGLE_MATCH_ID2 = "66531611";
	public static final String MATCH_ID_IN_40 = "11790355637";
	
	
	@Test
	public void testMerge_SameTime() throws Exception {
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();

		Map<String, Map<String, Object>> oneMatch = match.getMatches().get(SINGLE_MATCH_ID);
		long oldModifiedDate = (Long) oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate");
		merger.mergeMatchByTimestamp(SINGLE_MATCH_ID, oneMatch, oneMatch);
		assertEquals(oldModifiedDate, (long) oneMatch.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate"));
	}

	@Test
	public void testMerge_TargetIsNewer() throws Exception {

		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);

		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();

		Map<String, Map<String, Object>> target = match.getMatches().get(SINGLE_MATCH_ID);
		target.get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE,
				System.currentTimeMillis());
		Map<String, Map<String, Object>> delta = match2.getMatches().get(SINGLE_MATCH_ID);
		long oldModifiedDate = (Long) target.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate");

		merger.mergeMatchByTimestamp(SINGLE_MATCH_ID, target, delta);

		assertEquals(oldModifiedDate, (long) target.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate"));
	}

	@Test
	public void testMerge_DeltaIsNewer() throws Exception {

		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);

		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();

		Map<String, Map<String, Object>> target = match.getMatches().get(SINGLE_MATCH_ID);
		Map<String, Map<String, Object>> delta = match2.getMatches().get(SINGLE_MATCH_ID);
		delta.get(MatchFeedModel.SECTIONS.MATCH).put(MatchFeedModel.MATCH.LAST_MODIFIED_DATE,
				System.currentTimeMillis());

		long newDate = (Long) delta.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate");

		merger.mergeMatchByTimestamp(SINGLE_MATCH_ID, target, delta);

		assertEquals(newDate, (long) target.get(MatchFeedModel.SECTIONS.MATCH).get("lastModifiedDate"));
	}

	
	@Test
	public void testMerge_DeltaIsEmpty() throws Exception {

		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match2 = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testMerge_HbaseIsEmpty() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


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
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		assertEquals(0, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testMerge_HbaseAllHasSameValue() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		match2.getMatches().get(SINGLE_MATCH_ID).get("match").put("stage", 6);

		match2.getMatches().get(SINGLE_MATCH_ID).get("match").put("lastModifiedDate", 1106200000000L);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
		//assert Delta has been applied to base
		assertEquals(6, request.getLegacyMatchDataFeedDto().getMatches().get(SINGLE_MATCH_ID).get("match").get("stage"));
	}
	
	@Test
	public void testMerge_HbaseHasDifferentValue() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		Map<String, Map<String, Object>> matchesMap = match2.getMatches().get(SINGLE_MATCH_ID);

		match2.getMatches().put("66531611", matchesMap);
		match2.getMatches().remove(SINGLE_MATCH_ID);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		//should have both 66531610 and 66531611
		assertEquals(2, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	
	@Test
	public void testSingleMatchMerge_HbaseIsEmptyAndRedisHasOneMatch() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		LegacyMatchDataFeedDto match = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().setMatchId(Long.parseLong(SINGLE_MATCH_ID)).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testSingleMatchMerge_HbaseIsEmptyAndRedisHasManyMatches() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(FORTY_MATCHES_JSON_FILENAME);
		LegacyMatchDataFeedDto match = new LegacyMatchDataFeedDto();
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().setMatchId(Long.parseLong(MATCH_ID_IN_40)).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
	}
	
	@Test
	public void testSingleMatchMerge_RedisHasManyMatches() throws Exception {

		LegacyMatchDataFeedDto match2 = MatchTestUtils.getTestFeed(TWO_MATCHES_JSON_FILENAME);
		LegacyMatchDataFeedDto match = MatchTestUtils.getTestFeed(SINGLE_MATCH_JSON_FILENAME);
		
		MatchFeedQueryContext matchFeedQueryContext =  MatchFeedQueryContextBuilder.newInstance().setMatchId(Long.parseLong(SINGLE_MATCH_ID)).build();
		MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
		LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(1111L);
		feedWrapper.setLegacyMatchDataFeedDto(match);
		request.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
		request.setRedisFeed(match2);
		match2.getMatches().get(SINGLE_MATCH_ID).get("match").put("stage", 6);

		match2.getMatches().get(SINGLE_MATCH_ID).get("match").put("lastModifiedDate", 1106200000000L);
		HBaseRedisFeedMerger merger = new HBaseRedisFeedMerger();


		merger.merge(request);

		assertEquals(1, request.getLegacyMatchDataFeedDto().getMatches().size());
		//assert Delta has been applied to base
		assertEquals(6, request.getLegacyMatchDataFeedDto().getMatches().get(SINGLE_MATCH_ID).get("match").get("stage"));
	}
}
