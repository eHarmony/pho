package com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
/**
 * Sort the matches based on the last communication date. 
 * 
 * @author esrinivasan
 */
public class CommDateComparator implements Comparator<Map.Entry<String, Map<String, Map<String, Object>>>> {

	@Override
	public int compare(Entry<String, Map<String, Map<String, Object>>> commInfoEntry1, Entry<String, Map<String, Map<String, Object>>> commInfoEntry2) {

		Map<String, Map<String, Object>> commInfo1 = commInfoEntry1.getValue();
		Map<String, Map<String, Object>> commInfo2 = commInfoEntry2.getValue();

		 // compare delivered date - can be Integer or Long
        Number date1 =
            (Number) commInfo1.get(MatchFeedModel.SECTIONS.COMMUNICATION)
                               .get(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE);
        Number date2 =
            (Number) commInfo2.get(MatchFeedModel.SECTIONS.COMMUNICATION)
                               .get(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE);
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
