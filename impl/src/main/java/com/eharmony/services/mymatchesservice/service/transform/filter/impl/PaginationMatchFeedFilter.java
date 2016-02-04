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
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class PaginationMatchFeedFilter implements IMatchFeedTransformer {

    private static final Logger log = LoggerFactory.getLogger(PaginationMatchFeedFilter.class);
      
    private StatusDateIdMatchInfoComparator comparator = new StatusDateIdMatchInfoComparator();

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
        if (pageNum < 1) {

            log.debug("Match feed context doesn't request pagination (pageNum={}), returning without processing. Context={}",
                      pageNum, context);
            feed.setTotalMatches(feed.getMatches().size());
            return context;

        }

        int pageSize = context.getMatchFeedQueryContext().getPageSize();
        pageSize =
            (pageSize < 1) ? matches.size()
                           : pageSize; // fall back to default

        // 1. order the matches based on buckets, deliveredDate and matchId
        List<Map.Entry<String, Map<String, Map<String, Object>>>> entries =
            new LinkedList<Map.Entry<String, Map<String, Map<String, Object>>>>(matches.entrySet());
        Collections.sort(entries, comparator);

        // 2. advance to required window
        Iterator<Entry<String, Map<String, Map<String, Object>>>> it = entries.iterator();
        int currentRecord = 1;
        int starting = ((pageNum - 1) * pageSize) + 1;
        while ((currentRecord < starting) && it.hasNext()) {

            Entry<String, Map<String, Map<String, Object>>> entry = it.next();

            log.debug("skipped record#={} for matchId={} matchInfo={}",
            				currentRecord, entry.getKey(), entry.getValue());

            currentRecord++;

        }

        // 3. pick N elements, store them in the ordered map
        Map<String, Map<String, Map<String, Object>>> result =
            new LinkedHashMap<String, Map<String, Map<String, Object>>>(); // must be linked map to preserve the order !!
        int picked = 0;
        while ((picked < pageSize) && it.hasNext()) {

            Entry<String, Map<String, Map<String, Object>>> entry = it.next();
            result.put(entry.getKey(), entry.getValue());
            picked++;

            log.debug("added record#={} as #={} for matchId={} matchInfo={}",
                          new Object[] { starting + picked - 1, picked, entry.getKey(), entry.getValue() });

        }

        // 4. re-wire ordered map into the context
        context.getLegacyMatchDataFeedDto().setMatches(result);

        return context;

    }

}
