package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

/**
 * Compare matches based on spotlight date, so that earliest spotlight purchases come first
 */
@Component
public class SpotlightComparator implements
        Comparator<Map.Entry<String, Map<String, Map<String, Object>>>> {

    @Override
    public int compare(Entry<String, Map<String, Map<String, Object>>> matchInfoEntry1,
            Entry<String, Map<String, Map<String, Object>>> matchInfoEntry2) {

        Map<String, Map<String, Object>> matchInfo1 = matchInfoEntry1.getValue();
        Map<String, Map<String, Object>> matchInfo2 = matchInfoEntry2.getValue();
        
        // Compare spotlight status
        String spotlit1ISODate = (String) matchInfo1.get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE);
        String spotlit2ISODate = (String) matchInfo2.get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE);
        
        if(StringUtils.isBlank(spotlit1ISODate) && StringUtils.isBlank(spotlit2ISODate)){
            return 0;
        }
        
        // Match 1 does not have spotlight and Match 2 does, so Match 2 is first
        if(StringUtils.isBlank(spotlit1ISODate) && !StringUtils.isBlank(spotlit2ISODate)){
            return 1;
        }
        
        // Match 1 does have spotlight and Match 2 does not, so Match 1 is first
        if(!StringUtils.isBlank(spotlit1ISODate) && StringUtils.isBlank(spotlit2ISODate)){
            return -1;
        }
        // They both have spotlight, so whichever spotlight ends first (and was therefore purchased first) comes first
        LocalDateTime spotlight1EndDate = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(spotlit1ISODate, LocalDateTime::from);
        LocalDateTime spotlight2EndDate = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(spotlit2ISODate, LocalDateTime::from);
        return spotlight1EndDate.compareTo(spotlight2EndDate);
        
    }

    
}
