package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

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
        Date spotlit1Date = getDateNullSafe(matchInfo1.get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE));
        Date spotlit2Date = getDateNullSafe(matchInfo2.get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE));
        
        if(spotlit1Date == null && spotlit2Date == null){
            return 0;
        }
        
        // Match 1 does not have spotlight and Match 2 does, so Match 2 is first
        if(spotlit1Date == null && spotlit2Date != null){
            return 1;
        }
        
        // Match 1 does have spotlight and Match 2 does not, so Match 1 is first
        if(spotlit1Date != null && spotlit2Date == null){
            return -1;
        }
        
        return spotlit1Date.compareTo(spotlit2Date);
        
    }

    private Date getDateNullSafe(Object object) {

        if(object == null){
            return null;
        }
        
        Long epochTime = (Long) object;
        
        Date date = new Date();
        date.setTime(epochTime);
        
        return date;
    }

    
}
