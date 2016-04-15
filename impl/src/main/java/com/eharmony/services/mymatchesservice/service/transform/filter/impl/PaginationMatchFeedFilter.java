package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class PaginationMatchFeedFilter implements IMatchFeedTransformer {

    private static final Logger log = LoggerFactory.getLogger(PaginationMatchFeedFilter.class);
      
    private Comparator<Map.Entry<String, Map<String, Map<String, Object>>>> comparator = new StatusDateIdMatchInfoComparator();

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

        feed.setTotalMatches(matches.size());
        
        int pageNum = context.getMatchFeedQueryContext().getStartPage();
        int pageSize = context.getMatchFeedQueryContext().getPageSize();
        if (pageNum < 1) {

            pageNum = 1;
            pageSize = matches.size();

        }

        pageSize =  (pageSize < 1) ? matches.size() : pageSize;

        // 1. order the matches based on buckets, deliveredDate and matchId
        List<Map.Entry<String, Map<String, Map<String, Object>>>> entries =
            new ArrayList<Map.Entry<String, Map<String, Map<String, Object>>>>(matches.entrySet());
        Collections.sort(entries, comparator);
        entries = entries.subList((pageNum - 1) * pageSize, pageNum * pageSize);
        Map<String, Map<String, Map<String, Object>>> entryMap = entries.stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),(k,v) ->{ throw new RuntimeException(String.format("Duplicate key %s", k));},
                LinkedHashMap::new));
        context.getLegacyMatchDataFeedDto().setMatches(entryMap);
        return context;

    }

}
