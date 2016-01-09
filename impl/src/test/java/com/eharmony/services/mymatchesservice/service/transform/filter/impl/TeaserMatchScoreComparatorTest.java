package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator.MatchScoreComparator;

@RunWith(MockitoJUnitRunner.class)
public class TeaserMatchScoreComparatorTest {

	@Test
	public void testSortOrder()
	{
		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<String, Map<String, Map<String, Object>>>();
		
		Map<String, Map<String, Object>> matchSection1 = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> matchSection2 = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> matchSection3 = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> matchSection4 = new HashMap<String, Map<String, Object>>();
		
		Map<String,Object> match1 = new HashMap<String, Object>();
		Map<String,Object> match2 = new HashMap<String, Object>();
		Map<String,Object> match3 = new HashMap<String, Object>();
		Map<String,Object> match4 = new HashMap<String, Object>();
		
		
		match1.put(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE, 123L);
		match2.put(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE, 13L);
		match3.put(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE, 223L);
		match4.put(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE, 923L);
		
		
		matchSection1.put(MatchFeedModel.SECTIONS.MATCH,match1);
		matchSection2.put(MatchFeedModel.SECTIONS.MATCH,match2);
		matchSection3.put(MatchFeedModel.SECTIONS.MATCH,match3);
		matchSection4.put(MatchFeedModel.SECTIONS.MATCH,match4);
		
		matches.put("1", matchSection1); // Third Highest
		matches.put("2", matchSection2); // Lowest Score
		matches.put("3", matchSection3); // Second Highest
		matches.put("4", matchSection4); // Highest Score
		
		
		List<Map.Entry<String, Map<String, Map<String, Object>>>> entries =
	            new LinkedList<Map.Entry<String, Map<String, Map<String, Object>>>>(matches.entrySet());
		
		Collections.sort(entries, new MatchScoreComparator());
		assertTrue(entries.get(0).getKey() == "4");
		assertTrue(entries.get(1).getKey() == "3");
		assertTrue(entries.get(2).getKey() == "1");
		assertTrue(entries.get(3).getKey() == "2");
		
	}
	
}
