package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.LegacyMatchTransformer;

public class HBaseRedisMatchMerger {

    private static final Logger log = LoggerFactory.getLogger(HBaseRedisMatchMerger.class);
 
    private LegacyMatchTransformer legacyMatchTransformer = new LegacyMatchTransformer();

	public void merge(SingleMatchRequestContext singleMatchRequest){
	    
		Map<String, Map<String, Object>> hbaseMatch = legacyMatchTransformer.transform(singleMatchRequest.getHbaseMatch());
		Map<String, Map<String, Object>> redisMatch = singleMatchRequest.getRedisMatch();

		if(MapUtils.isEmpty(redisMatch)){
			log.debug("Redis has no match for userId {}, nothing to merge.", singleMatchRequest.getQueryContext().getUserId());
			singleMatchRequest.setSingleMatch(hbaseMatch);			
		}else if(MapUtils.isEmpty(hbaseMatch)){
			log.debug("HBase has no match for userId {}. Using Redis feed.", singleMatchRequest.getQueryContext().getUserId());
			singleMatchRequest.setSingleMatch(redisMatch); 			
		}else{
			Map<String, Map<String, Object>> mergedMatch = 
      								MergeUtils.mergeMatchByTimestamp(hbaseMatch, redisMatch);
			log.debug("Merging match for userId {} matchId {}", singleMatchRequest.getQueryContext().getUserId(), 
																singleMatchRequest.getQueryContext().getMatchId());
      		singleMatchRequest.setSingleMatch(mergedMatch);			
		}
    }
}
