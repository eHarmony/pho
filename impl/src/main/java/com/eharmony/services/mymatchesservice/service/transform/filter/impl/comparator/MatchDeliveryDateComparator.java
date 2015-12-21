package com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

/**
 * Sort the matches based on delivery date.
 * 
 * @author esrinivasan
 */
public class MatchDeliveryDateComparator implements Comparator<Map.Entry<String, Map<String, Map<String, Object>>>> {

	@Override
	public int compare(Entry<String, Map<String, Map<String, Object>>> matchInfoEntry1, Entry<String, Map<String, Map<String, Object>>> matchInfoEntry2) {

		Map<String, Map<String, Object>> matchInfo1 = matchInfoEntry1.getValue();
		Map<String, Map<String, Object>> matchInfo2 = matchInfoEntry2.getValue();

		 // compare delivered date - can be Integer or Long
        Number date1 =
            (Number) matchInfo1.get(MatchFeedModel.SECTIONS.MATCH)
                               .get(MatchFeedModel.MATCH.DELIVERED_DATE);
        Number date2 =
            (Number) matchInfo2.get(MatchFeedModel.SECTIONS.MATCH)
                               .get(MatchFeedModel.MATCH.DELIVERED_DATE);
        long t1 =
            ((date1 == null) ? 0
                             : date1.longValue());
        long t2 =
            ((date2 == null) ? 0
                             : date2.longValue());
        int result =
            (t1 == t2) ? 0
                       : ((t1 < t2) ? 1
                                    : -1); // descending, most recent (larger) date goes first (determined to be less than)

        return result;

	}

}
