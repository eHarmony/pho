/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2016 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;


public class StatusDateIdMatchInfoComparator
          implements Comparator<Map.Entry<String, Map<String, Map<String, Object>>>> {

    private Comparator<String> matchStatusComparator = new MatchStatusComparator();

    @Override public int compare(Entry<String, Map<String, Map<String, Object>>> matchInfoEntry1,
                                 Entry<String, Map<String, Map<String, Object>>> matchInfoEntry2) {

        Map<String, Map<String, Object>> matchInfo1 = matchInfoEntry1.getValue();
        Map<String, Map<String, Object>> matchInfo2 = matchInfoEntry2.getValue();

        // compare match status
        String matchStatus1 = (String) matchInfo1.get(MatchFeedModel.SECTIONS.MATCH)
                                                 .get(MatchFeedModel.MATCH.STATUS);
        String matchStatus2 = (String) matchInfo2.get(MatchFeedModel.SECTIONS.MATCH)
                                                 .get(MatchFeedModel.MATCH.STATUS);
        int result = matchStatusComparator.compare(matchStatus1, matchStatus2);

        if (result == 0) { // same status
            
            // Compare spotlight status
            String spotlit1ISODate = (String) matchInfo1.get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE);
            String spotlit2ISODate = (String) matchInfo2.get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE);
            
            // Match 1 does not have spotlight and Match 2 does, so Match 2 is first
            if(StringUtils.isBlank(spotlit1ISODate) && !StringUtils.isBlank(spotlit2ISODate)){
                return 1;
            }
            
            // Match 1 does have spotlight and Match 2 does not, so Match 1 is first
            if(!StringUtils.isBlank(spotlit1ISODate) && StringUtils.isBlank(spotlit2ISODate)){
                return -1;
            }
            // They both have spotlight, so whichever spotlight ends first (and was therefore purchased first) comes first
            if(!StringUtils.isBlank(spotlit1ISODate) && !StringUtils.isBlank(spotlit2ISODate)){
                LocalDateTime spotlight1EndDate = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(spotlit1ISODate, LocalDateTime::from);
                LocalDateTime spotlight2EndDate = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(spotlit2ISODate, LocalDateTime::from);
                return spotlight1EndDate.compareTo(spotlight2EndDate);
            }
            
            // Both do not have spotlight, so compare delivered date - can be Integer or Long
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
            result =
                (t1 == t2) ? 0
                           : ((t1 < t2) ? 1
                                        : -1); // descending, most recent (larger) date goes first (determined to be less than)

        }
        if (result == 0) { // same status, and same spotlight, and same delivery date

            // compare matchId, take the earliest ones to go first

            Number mid1 = (Number) matchInfo1.get(MatchFeedModel.SECTIONS.MATCH)
                                             .get(MatchFeedModel.MATCH.ID);
            Number mid2 = (Number) matchInfo2.get(MatchFeedModel.SECTIONS.MATCH)
                                             .get(MatchFeedModel.MATCH.ID);

            long id1 =
                ((mid1 == null) ? 0
                                : mid1.longValue());
            long id2 =
                ((mid2 == null) ? 0
                                : mid2.longValue());
            result =
                (id1 == id2) ? 0
                             : ((id1 < id2) ? -1
                                            : 1);

        }

        return result;

    }

}
