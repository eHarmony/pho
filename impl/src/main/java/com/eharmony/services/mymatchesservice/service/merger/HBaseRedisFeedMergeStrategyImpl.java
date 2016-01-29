package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Sets;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class HBaseRedisFeedMergeStrategyImpl implements FeedMergeStrategy {

    private static final Logger log = LoggerFactory.getLogger(HBaseRedisFeedMergeStrategyImpl.class);

    public static final String HBASE_TIMESTAMP_NAME = "lastModifiedDate";
    public static final String REDIS_TIMESTAMP_NAME = "updatedAt";

    @Override
    public void merge(MatchFeedRequestContext request) {

        LegacyMatchDataFeedDto hbaseFeed = request.getLegacyMatchDataFeedDto();
        LegacyMatchDataFeedDto redisFeed = request.getRedisFeed();
        long userId = request.getUserId();
        log.info("Merging HBase, Redis feeds for userId {}", userId);

        if (hbaseFeed == null || MapUtils.isEmpty(hbaseFeed.getMatches())) {
            if (redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())) {
                handleHBaseIsEmptyRedisIsEmpty(userId);
            } else {
                handleHBaseIsEmptyRedisHasMatches(userId, request, redisFeed);
            }

        } else {
            if (redisFeed == null || MapUtils.isEmpty(redisFeed.getMatches())) {
                handleHBaseHasMatchesRedisIsEmpty(userId);
            } else {
                handleHBaseHasMatchesRedisHasMatches(hbaseFeed, redisFeed);
            }
        }
    }

    private void handleHBaseHasMatchesRedisIsEmpty(long userId) {
        log.info("Redis has no data for userId {}, nothing to merge.", userId);
        return;

    }

    private void handleHBaseIsEmptyRedisIsEmpty(long userId) {
        log.warn("HBase and Redis have no data for userId {}.", userId);
        return;

    }

    private void handleHBaseIsEmptyRedisHasMatches(long userId, MatchFeedRequestContext request,
            LegacyMatchDataFeedDto redisFeed) {

        log.warn("HBase has no data for userId {}. Using Redis feed.", userId);

        LegacyMatchDataFeedDtoWrapper wrapper = request.getLegacyMatchDataFeedDtoWrapper();
        wrapper.setLegacyMatchDataFeedDto(redisFeed);
        wrapper.setFeedAvailable(true);

    }

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
                mergeRedisToHbase(matchId, hbaseMatch, redisMatch);
            }
        });
        
        suplementryIdSet.stream().forEach((matchId) -> {
            Map<String, Map<String, Object>> redisMatch = redisMatches.get(matchId);
            hbaseFeed.getMatches().put(matchId, redisMatch);
            int totalMatches = hbaseFeed.getTotalMatches();
			hbaseFeed.setTotalMatches(totalMatches + 1);
        });

    }

    protected void mergeRedisToHbase(String matchId, Map<String, Map<String, Object>> targetMatch,
            Map<String, Map<String, Object>> deltaMatch) {
        targetMatch.put(MatchFeedModel.SECTIONS.MATCH, deltaMatch.get(MatchFeedModel.SECTIONS.MATCH));
        targetMatch.put(MatchFeedModel.SECTIONS.COMMUNICATION,
                deltaMatch.get(MatchFeedModel.SECTIONS.COMMUNICATION));
        log.info("match {} updated by delta.", matchId);

    }

    protected void mergeMatchByTimestamp(String matchId, Map<String, Map<String, Object>> targetMatch,
            String tmTimestampName, Map<String, Map<String, Object>> deltaMatch, String dmTimestampName) {

        Map<String, Object> targetMatchSection = targetMatch.get(MatchFeedModel.SECTIONS.MATCH);
        Map<String, Object> deltaMatchSection = deltaMatch.get(MatchFeedModel.SECTIONS.MATCH);

        Date targetTs = new Date((Long) targetMatchSection.get(tmTimestampName));
        Date deltaTs = new Date((Long) deltaMatchSection.get(dmTimestampName));

        if (targetTs == null || deltaTs == null) {
            log.warn("match {} missing one or more timestamps: target {}, delta {}.", matchId, targetTs, deltaTs);
            return;
        }

        if (deltaTs.after(targetTs)) {
            targetMatch.put(MatchFeedModel.SECTIONS.MATCH, deltaMatch.get(MatchFeedModel.SECTIONS.MATCH));
            targetMatch.put(MatchFeedModel.SECTIONS.COMMUNICATION,
                    deltaMatch.get(MatchFeedModel.SECTIONS.COMMUNICATION));
            log.info("match {} updated by delta.", matchId);
        }
    }
}
