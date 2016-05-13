package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

@Component("paginationMatchFeedFilter")
public class PaginationMatchFeedFilter implements IMatchFeedTransformer {

    private static final Logger log = LoggerFactory.getLogger(PaginationMatchFeedFilter.class);
    
    @Autowired
    private StatusDateIdMatchInfoComparator statusDateIdMatchInfoComparator;
    
    @Autowired
    private SpotlightComparator spotlightComparator;

    @Value("${spotlight.users.to.elevate.maximum:4}")
    private int maximumSpotlitUsers;

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
        
        // 2. Sort spotlight users first (up to limit) followed by the rest sorted by delivery date
        List<Entry<String, Map<String, Map<String, Object>>>> spotlightPortion = entries.stream().filter(entry -> entry.getValue().get(MatchFeedModel.SECTIONS.PROFILE).get(MatchFeedModel.PROFILE.SPOTLIGHT_END_DATE) != null).sorted(spotlightComparator).limit(maximumSpotlitUsers).collect(Collectors.toList());
        entries.removeAll(spotlightPortion);
        Collections.sort(entries, statusDateIdMatchInfoComparator);
        entries.addAll(0, spotlightPortion);
        
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
