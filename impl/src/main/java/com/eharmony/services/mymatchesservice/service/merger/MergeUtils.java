package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Date;
import java.util.Map;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class MergeUtils {

    public static final String TIMESTAMP_NAME = "lastModifiedDate";
    
    public static Map<String, Map<String, Object>> mergeMatchByTimestamp(Map<String, Map<String, Object>> targetMatch, 
            Map<String, Map<String, Object>> deltaMatch) {

        Map<String, Object> targetMatchSection = targetMatch.get(MatchFeedModel.SECTIONS.MATCH);
        Map<String, Object> deltaMatchSection = deltaMatch.get(MatchFeedModel.SECTIONS.MATCH);

        Date targetTs = new Date((Long) targetMatchSection.get(TIMESTAMP_NAME));
        Date deltaTs = new Date((Long) deltaMatchSection.get(TIMESTAMP_NAME));

        if (deltaTs.after(targetTs)) {
            targetMatch.put(MatchFeedModel.SECTIONS.MATCH, deltaMatch.get(MatchFeedModel.SECTIONS.MATCH));
            targetMatch.put(MatchFeedModel.SECTIONS.COMMUNICATION,
                    deltaMatch.get(MatchFeedModel.SECTIONS.COMMUNICATION));
        }
        
        return targetMatch;
    }
}
