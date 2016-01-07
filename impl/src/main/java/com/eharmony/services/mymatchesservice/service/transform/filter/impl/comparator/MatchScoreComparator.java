package com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

/**
 * Sort the matches based on the match score received from the scorer service.
 * 
 * @author esrinivasan
 */
public class MatchScoreComparator implements Comparator<Map.Entry<String, Map<String, Map<String, Object>>>> {

	@Override
	public int compare(Entry<String, Map<String, Map<String, Object>>> matchInfoEntry1, Entry<String, Map<String, Map<String, Object>>> matchInfoEntry2) {

		Map<String, Map<String, Object>> matchInfo1 = matchInfoEntry1.getValue();
		Map<String, Map<String, Object>> matchInfo2 = matchInfoEntry2.getValue();

		// compare match score
		Long matchTeaserScore1 = (Long) matchInfo1.get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE);
		Long matchTeaserScore2 = (Long) matchInfo2.get(MatchFeedModel.SECTIONS.MATCH).get(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE);

		if ((matchTeaserScore1 == null) && (matchTeaserScore2 == null)) {

			return 0;

		}
		if (matchTeaserScore1 == null) {

			return 1; // unknown goes last

		}
		if (matchTeaserScore2 == null) {

			return -1; // unknown goes last

		}

		return matchTeaserScore2.compareTo(matchTeaserScore1);

	}

}
