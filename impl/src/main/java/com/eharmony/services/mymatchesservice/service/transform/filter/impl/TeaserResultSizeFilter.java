package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator.MatchScoreComparator;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class TeaserResultSizeFilter implements IMatchFeedTransformer {

    private static final Logger log = LoggerFactory.getLogger(TeaserResultSizeFilter.class);
      
    private MatchScoreComparator comparator = new MatchScoreComparator();
    
	@Override
	public MatchFeedRequestContext processMatchFeed(MatchFeedRequestContext context) {

        if (context == null) {

            log.debug("Match feed context is null or does not have data, returning without processing. Context={}",
                      context);
            return context;
        }

        LegacyMatchDataFeedDto feed = context.getLegacyMatchDataFeedDto();
        if( feed == null){
            log.debug("LegacyMatchDataFeedDto is null or does not have data, returning without processing. Context={}",
                    context);
            return context;      	
        }
        
        Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
        if (MapUtils.isEmpty(matches)) {

            log.debug("Match feed is null or empty, returning without processing. Context={}", context);
            feed.setTotalMatches(0);
            return context;

        }

        int pageNum = context.getMatchFeedQueryContext().getStartPage();
        if (pageNum < 1) {

            log.debug("Match feed context doesn't request pagination (pageNum={}), returning without processing. Context={}",
                      pageNum, context);
            return context;

        }

        int resultSize = context.getMatchFeedQueryContext().getTeaserResultSize();

        List<Map.Entry<String, Map<String, Map<String, Object>>>> entries = new LinkedList<Map.Entry<String, Map<String, Map<String, Object>>>>(matches.entrySet());
        Collections.sort(entries, comparator);
        
        Map<String, Map<String, Map<String, Object>>> result = new LinkedHashMap<String, Map<String, Map<String, Object>>>();
        Iterator<Entry<String, Map<String, Map<String, Object>>>> it = entries.iterator();

        int recordsPicked = 0;
        while ((recordsPicked < resultSize) && it.hasNext()) {

            Entry<String, Map<String, Map<String, Object>>> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
            recordsPicked++;
        }

        context.getLegacyMatchDataFeedDto().setMatches(result);
        
        feed.setTotalMatches(result.size());

        return context;

    }
	
}
