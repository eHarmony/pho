package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator.CommDateComparator;
import com.eharmony.services.mymatchesservice.service.transform.filter.impl.comparator.MatchDeliveryDateComparator;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

/**
 * This filter returns the top matches which fall within a certain time range. The pool of matches is decided based 
 * on delivery date.
 * 
 * @author esrinivasan
 */
public class MatchTeaserCandidatesFilter implements IMatchFeedTransformer {

    private static final Logger log = LoggerFactory.getLogger(MatchTeaserCandidatesFilter.class);
      
    private MatchDeliveryDateComparator deliveryDateComparator = new MatchDeliveryDateComparator();
    
    private CommDateComparator commDateCompartor = new CommDateComparator();
    
    // Filter matches with dates between the (most recent date) and (most recent date - match history days)
    private String matchHistoryDays;  
    
	public MatchTeaserCandidatesFilter(String historyDays) {
		
		super();
		Assert.notNull(historyDays);
		this.matchHistoryDays = historyDays;

	}

	@Override
	public MatchFeedRequestContext processMatchFeed(MatchFeedRequestContext context) {

        if (context == null) {

            log.debug("Match feed context is null or does not have data, returning without processing. Context={}", context);
            return context;
        }

        LegacyMatchDataFeedDto feed = context.getLegacyMatchDataFeedDto();
        if( feed == null){
            log.debug("LegacyMatchDataFeedDto is null or does not have data, returning without processing. Context={}", context);
            return context;      	
        }
        
        Map<String, Map<String, Map<String, Object>>> matches = feed.getMatches();
        if (MapUtils.isEmpty(matches)) {

            log.debug("Match feed is null or empty, returning without processing. Context={}", context);
            feed.setTotalMatches(0);
            return context;

        }

        List<Map.Entry<String, Map<String, Map<String, Object>>>> entries = new LinkedList<Map.Entry<String, Map<String, Map<String, Object>>>>(matches.entrySet());
        Collections.sort(entries, deliveryDateComparator);
        
        Map<String, Map<String, Map<String, Object>>> result = new LinkedHashMap<String, Map<String, Map<String, Object>>>();
        
        result = filterRecentMatchesByDays(entries, MatchFeedModel.SECTIONS.MATCH, MatchFeedModel.MATCH.DELIVERED_DATE);
        
       /* 
        TODO: Need to discuss with Product to see if we need to bring in the candidates with whom the user commed into the scoring pool. 
        Collections.sort(entries,  commDateCompartor);
        result.putAll(filterRecentMatchesByDays(entries, MatchFeedModel.SECTIONS.COMMUNICATION, MatchFeedModel.COMMUNICATION.LAST_COMM_DATE));
       */
        
        context.getLegacyMatchDataFeedDto().setMatches(result);

        return context;

    }
	
	/**
	 * Takes a list of matches and then fetches the matches which fall within a certain time range.
	 * The time range is the difference between the most recent date in the list and the date which is 'matchHistoryDays' old.
	 * 
	 * @param entries list of matches entries from the feed
	 * @param sectionName  name of the section to pull information from the matches feed
	 * @param fieldName  name of the field to pull information from the section.
	 * @return Collection of matches
	 */
	private Map<String, Map<String, Map<String, Object>>> filterRecentMatchesByDays(List<Entry<String, Map<String, Map<String, Object>>>> entries,String sectionName,String fieldName) {
		 Iterator<Entry<String, Map<String, Map<String, Object>>>> it = entries.iterator();
		 
		 boolean processedFirstRecord = false;
		 
		 DateTime oldestDate = null;
		 
		 Map<String, Map<String, Map<String, Object>>> result = new LinkedHashMap<String, Map<String, Map<String, Object>>>();
		 Map<String, Map<String, Map<String, Object>>> filteredMap = new LinkedHashMap<String, Map<String, Map<String, Object>>>();
		 
		 while(it.hasNext())
		 {
			 Entry<String, Map<String, Map<String, Object>>> entry = it.next();
			 Map<String, Object> sectionMap = entry.getValue().get(sectionName);
			 Long dateAsLong = (Long)sectionMap.get(fieldName);
			 
			//This will be null only if we take into account people who were delivered earlier than the given time range but were communicated recently.
			//For phase 1 , this scenario might never happen as we are only looking as delivered_date field.
			if (dateAsLong == null) {
				return result;
			}
			 
			 DateTime date = new DateTime(dateAsLong,DateTimeZone.UTC);
			 if(!processedFirstRecord){
			 
				oldestDate = date.minus(Integer.parseInt(matchHistoryDays));
				processedFirstRecord = true; 
				
			 }
			 
			 // If the date of the current records is within the date range then add it to the result list.
			 if(date.isAfter(oldestDate) || date.isEqual(oldestDate)){
				 
				 result.put(entry.getKey(), entry.getValue());
				 
			 }else{
				 
				 filteredMap.put(entry.getKey(), entry.getValue());
			 }
			 
		 }
		 
		 entries = new LinkedList<Map.Entry<String, Map<String, Map<String, Object>>>>(filteredMap.entrySet());
		 return result;
	}
	
}
