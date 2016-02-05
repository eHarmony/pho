package com.eharmony.services.mymatchesservice.service.merger;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
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
}
