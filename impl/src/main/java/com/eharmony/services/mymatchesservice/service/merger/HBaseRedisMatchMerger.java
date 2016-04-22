package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.LegacyMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class HBaseRedisMatchMerger {

    private static final Logger log = LoggerFactory.getLogger(HBaseRedisMatchMerger.class);
 
    public static final String TIMESTAMP_NAME = "lastModifiedDate";

    private LegacyMatchTransformer legacyMatchTransformer = new LegacyMatchTransformer();

	public void merge(SingleMatchRequestContext singleMatchRequest){

	    long userId = singleMatchRequest.getQueryContext().getUserId();
	    
        log.debug("Merging HBase, Redis feeds for userId {}", userId);

		Map<String, Map<String, Object>> hbaseMatch = legacyMatchTransformer.transform(singleMatchRequest.getHbaseMatch());
		LegacyMatchDataFeedDto redisFeed = singleMatchRequest.getRedisFeed();

        if (MapUtils.isEmpty(hbaseMatch)) {
            //No Hbase feed
            if (redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())) {
                //Both are empty.
                handleHBaseNoMatchRedisNoMatch(hbaseMatch, redisFeed, singleMatchRequest);
            } else {
                //Redis is the only feed we have. use it.
                handleHBaseNoMatchRedisHasMatch(hbaseMatch, redisFeed, singleMatchRequest);
            }

        } else {
            if (redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())) {
                //Hbase has the updated data
                handleHBaseHasMatchRedisNoMatch(hbaseMatch, redisFeed, singleMatchRequest);
            } else {
                //apply delta in Redis to Hbase
                handleHBaseHasMatchRedisHasMatch(hbaseMatch, redisFeed, singleMatchRequest);
            }
        }
    }

    //following two cases are delta are empty, does nothing.
    private void handleHBaseHasMatchRedisNoMatch(Map<String, Map<String, Object>> hbaseMatch,
            LegacyMatchDataFeedDto redisFeed, SingleMatchRequestContext context) {
        log.debug("Redis has no match for userId {}, nothing to merge.", context.getQueryContext().getUserId());
        context.setSingleMatch(hbaseMatch);
    }

    private void handleHBaseNoMatchRedisNoMatch(Map<String, Map<String, Object>> hbaseMatch,
            LegacyMatchDataFeedDto redisFeed, SingleMatchRequestContext context) {
        log.warn("HBase and Redis have no match for userId {}.", context.getQueryContext().getUserId());
        context.setSingleMatch(null);
    }

    //take all Redis feed and put them to Hbase feeds
    private void handleHBaseNoMatchRedisHasMatch(Map<String, Map<String, Object>> hbaseMatch,
            LegacyMatchDataFeedDto redisFeed, SingleMatchRequestContext context) {

        log.warn("HBase has no data for userId {}. Using Redis feed.", context.getQueryContext().getUserId());

    	// remove all but the single match from Redis feed.
    	long matchId = context.getQueryContext().getMatchId();
    	
    	Map<String, Map<String, Object>> redisMatch = redisFeed.getMatches().get(Long.toString(matchId));
    	context.setSingleMatch(redisMatch);
     }

    //actual merge, check time stamp on both sides and take the latest.
    private void handleHBaseHasMatchRedisHasMatch(Map<String, Map<String, Object>> hbaseMatch,
            LegacyMatchDataFeedDto redisFeed, SingleMatchRequestContext context) {

    	String matchId = Long.toString(context.getQueryContext().getMatchId());
    	
        Map<String, Map<String, Map<String, Object>>> redisMatches = redisFeed.getMatches();
        Map<String, Map<String, Object>> redisMatch = redisMatches.get(matchId);
      
        if(redisMatch != null){
        	mergeMatchByTimestamp(matchId, hbaseMatch, redisMatch);
        }
        
        context.setSingleMatch(hbaseMatch);
    }
    
    protected void mergeMatchByTimestamp(String matchId, Map<String, Map<String, Object>> targetMatch, 
            Map<String, Map<String, Object>> deltaMatch) {

        Map<String, Object> targetMatchSection = targetMatch.get(MatchFeedModel.SECTIONS.MATCH);
        Map<String, Object> deltaMatchSection = deltaMatch.get(MatchFeedModel.SECTIONS.MATCH);

        Date targetTs = new Date((Long) targetMatchSection.get(TIMESTAMP_NAME));
        Date deltaTs = new Date((Long) deltaMatchSection.get(TIMESTAMP_NAME));

        if (deltaTs.after(targetTs)) {
            targetMatch.put(MatchFeedModel.SECTIONS.MATCH, deltaMatch.get(MatchFeedModel.SECTIONS.MATCH));
            targetMatch.put(MatchFeedModel.SECTIONS.COMMUNICATION,
                    deltaMatch.get(MatchFeedModel.SECTIONS.COMMUNICATION));
            log.debug("match {} updated by delta.", matchId);
        }
    }
}
