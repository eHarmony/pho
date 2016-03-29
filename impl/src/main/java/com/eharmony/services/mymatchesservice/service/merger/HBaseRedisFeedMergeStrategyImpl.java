package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.google.common.collect.Sets;
@Component("HBaseRedisFeedMergeStrategy")
public class HBaseRedisFeedMergeStrategyImpl implements FeedMergeStrategy {
    public static final String TIMESTAMP_NAME = "lastModifiedDate";


    private static final Logger log = LoggerFactory.getLogger(HBaseRedisFeedMergeStrategyImpl.class);

    @Override
    public void merge(MatchFeedRequestContext request) {

        LegacyMatchDataFeedDto hbaseFeed = request.getLegacyMatchDataFeedDto();
        LegacyMatchDataFeedDto redisFeed = request.getRedisFeed();
        
        long userId = request.getUserId();
        log.debug("Merging HBase, Redis feeds for userId {}", userId);

        if (hbaseFeed == null || MapUtils.isEmpty(hbaseFeed.getMatches())) {
            //No Hbase feed
            if (redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())) {
                //Both are empty.
                handleHBaseIsEmptyRedisIsEmpty(userId);
            } else {
                //Redis is the only feed we have. use it.
                handleHBaseIsEmptyRedisHasMatches(userId, request, redisFeed);
            }

        } else {
            if (redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())) {
                //Hbase has the updated data
                handleHBaseHasMatchesRedisIsEmpty(userId);
            } else {
                //apply delta in Redis to Hbase
                handleHBaseHasMatchesRedisHasMatches(hbaseFeed, redisFeed);
            }
        }
        
        //
        LegacyMatchDataFeedDto mergedFeed = request.getLegacyMatchDataFeedDto();

        //merge gender and locale, this is attached to Redis feed.
        if ((mergedFeed != null) && (redisFeed != null)){
            mergedFeed.setGender(redisFeed.getGender());
            mergedFeed.setLocale(redisFeed.getLocale());
        }
    }

    //following two cases are delta are empty, does nothing.
    private void handleHBaseHasMatchesRedisIsEmpty(long userId) {
        log.debug("Redis has no data for userId {}, nothing to merge.", userId);
        return;

    }

    private void handleHBaseIsEmptyRedisIsEmpty(long userId) {
        log.warn("HBase and Redis have no data for userId {}.", userId);
        return;

    }

    //take all Redis feed and put them to Hbase feeds
    private void handleHBaseIsEmptyRedisHasMatches(long userId, MatchFeedRequestContext request,
            LegacyMatchDataFeedDto redisFeed) {

        log.warn("HBase has no data for userId {}. Using Redis feed.", userId);

        LegacyMatchDataFeedDtoWrapper wrapper = request.getLegacyMatchDataFeedDtoWrapper();
        wrapper.setLegacyMatchDataFeedDto(redisFeed);
        wrapper.setFeedAvailable(true);

    }

    //actual merge, check time stamp on both sides and take the latest.
    private void handleHBaseHasMatchesRedisHasMatches(LegacyMatchDataFeedDto hbaseFeed,
            LegacyMatchDataFeedDto redisFeed) {

        Map<String, Map<String, Map<String, Object>>> hbaseMatches = hbaseFeed.getMatches();
        final Map<String, Map<String, Map<String, Object>>> redisMatches = redisFeed.getMatches();
        
        Set<String> hbaseMatchIdSet = hbaseMatches.keySet();
        Set<String> redisMatchIdSet = redisMatches.keySet();

        Set<String> commonIdSet = Sets.intersection(hbaseMatchIdSet, redisMatchIdSet);
        Set<String> suplementryIdSet = Sets.difference(redisMatchIdSet, commonIdSet);
               
        hbaseMatchIdSet.stream().forEach((matchId) -> {

            Map<String, Map<String, Object>> redisMatch = redisMatches.get(matchId);

            if (redisMatch != null) {
                Map<String, Map<String, Object>> hbaseMatch = hbaseMatches.get(matchId);
                mergeMatchByTimestamp(matchId, hbaseMatch, redisMatch);
                
            }
        });
        
        suplementryIdSet.stream().forEach((matchId) -> {
            Map<String, Map<String, Object>> redisMatch = redisMatches.get(matchId);
            hbaseFeed.getMatches().put(matchId, redisMatch);
            int totalMatches = hbaseFeed.getTotalMatches();
			hbaseFeed.setTotalMatches(totalMatches + 1);
        });

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
