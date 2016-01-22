package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class HBaseRedisFeedMergeStrategyImpl implements FeedMergeStrategy {

    private static final Logger log = LoggerFactory.getLogger(HBaseRedisFeedMergeStrategyImpl.class);

	private final String HBASE_TIMESTAMP_NAME = "lastModifiedDate";
	private final String REDIS_TIMESTAMP_NAME = "updatedAt";

	@Override
	public void merge(MatchFeedRequestContext request) {
        
        LegacyMatchDataFeedDto hbaseFeed = request.getLegacyMatchDataFeedDto();
        LegacyMatchDataFeedDto redisFeed = request.getRedisFeed();
        long userId = request.getUserId();
		log.info("Merging HBase, Redis feeds for userId {}", userId);
        
        if(hbaseFeed == null || MapUtils.isEmpty(hbaseFeed.getMatches())){
        	if(redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())){
        		log.warn("HBase and Redis have no data for userId {}.", userId);
        		return;
        	}
        	
        	log.warn("HBase has no data for userId {}. Using Redis feed.", userId);
        	
        	LegacyMatchDataFeedDtoWrapper wrapper = request.getLegacyMatchDataFeedDtoWrapper();
        	wrapper.setLegacyMatchDataFeedDto(redisFeed);
        	wrapper.setFeedAvailable(true);    	
        	
        }else{
        	if(redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())){
        		log.info("Redis has no data for userId {}, nothing to merge.", userId);
        		return;
	        }
        	
    		Map<String, Map<String, Map<String, Object>>> hbaseMatches = hbaseFeed.getMatches();
    		final Map<String, Map<String, Map<String, Object>>> redisMatches = redisFeed.getMatches();
    		
    		hbaseMatches.keySet().stream()
    				.forEach((matchId) -> {
    					
    					Map<String, Map<String, Object>> redisMatch = redisMatches.get(matchId);
    						
    					if(redisMatch != null){
        					Map<String, Map<String, Object>> hbaseMatch = hbaseMatches.get(matchId);
        					if(mergeMatchByTimestamp(matchId, hbaseMatch, HBASE_TIMESTAMP_NAME, redisMatch, REDIS_TIMESTAMP_NAME)){
        						log.info("match {} updated by delta.", matchId);
        					}
    					}
        		});
        	
        }
	}

 
	protected boolean mergeMatchByTimestamp(String matchId, Map<String, Map<String, Object>> targetMatch, String tmTimestampName, 
											Map<String, Map<String, Object>> deltaMatch, String dmTimestampName){
            	
		Map<String, Object> targetMatchSection = targetMatch.get(MatchFeedModel.SECTIONS.MATCH);
		Map<String, Object> deltaMatchSection = deltaMatch.get(MatchFeedModel.SECTIONS.MATCH);
		
		Date targetTs = new Date((Long) targetMatchSection.get(tmTimestampName));
		Date deltaTs = dateFromLongAsString((String) deltaMatchSection.get(dmTimestampName));

		if(targetTs == null || deltaTs == null){
			log.warn("match {} missing one or more timestamps: target {}, delta {}.", matchId, targetTs, deltaTs );
			return false;
		}
		
		if(deltaTs.after(targetTs)){
			targetMatch.put(MatchFeedModel.SECTIONS.MATCH, deltaMatch.get(MatchFeedModel.SECTIONS.MATCH));
			targetMatch.put(MatchFeedModel.SECTIONS.COMMUNICATION, deltaMatch.get(MatchFeedModel.SECTIONS.COMMUNICATION));					
			return true;
		}
		return false;
	}
	
	private Date dateFromLongAsString(String longAsString){
		if(StringUtils.isEmpty(longAsString)){
			return null;
		}
		
		if(!NumberUtils.isNumber(longAsString)){
			return null;
		}
		
		return new Date(Long.valueOf(longAsString));
	}

}
