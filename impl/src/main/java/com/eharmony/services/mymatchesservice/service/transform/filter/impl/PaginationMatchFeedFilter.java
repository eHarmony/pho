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
        
        // If pagination is disabled, consider us to be on page 1 where the page size equals the results size
        if (pageNum < 1) {

            pageNum = 1;
            pageSize = matches.size();

        }

        pageSize =  (pageSize < 1) ? matches.size() : pageSize;

        // 1. Place all the matches into a list, so that they can be sorted
        List<Map.Entry<String, Map<String, Map<String, Object>>>> entries =
            new ArrayList<Map.Entry<String, Map<String, Map<String, Object>>>>(matches.entrySet());
        
        // 2. Sort the list
        Collections.sort(entries, comparator);
        
        // 3. Select the sub-list to be returned on this page
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = pageNum * pageSize;
        int size = entries.size();
        if(toIndex > size){
            toIndex = size;
        }
        if(fromIndex > toIndex){
            entries =  Collections.emptyList();
        } else {
            entries = entries.subList(fromIndex, toIndex);
        }
        
        // 4. Put the entries back into map form, but use a LinkedHashMap to maintain the ordering
        Map<String, Map<String, Map<String, Object>>> entryMap = entries.stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(),(k,v) ->{ throw new RuntimeException(String.format("Duplicate key %s", k));},
                LinkedHashMap::new));
        
        // 5. Set the new ordered map on the response
        context.getLegacyMatchDataFeedDto().setMatches(entryMap);
        
        return context;

    }

}
