package com.eharmony.services.mymatchesservice.service.merger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.rest.MatchCountContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.singles.common.status.MatchStatus;

@Component
public class MatchCountRedisDataMerger {

    public MatchCountContext mergeRedisData(MatchCountContext matchCountContext) {

        Map<MatchStatus, Set<Long>> hbaseMatchesByStatus = matchCountContext.getMatchesByStatus();
        LegacyMatchDataFeedDto redisDeltaFeedDto = matchCountContext.getRedisMatchDataFeedDto();

        // redis or hbase feed is empty, no need to merge the results.
        if (redisDeltaFeedDto == null || MapUtils.isEmpty(hbaseMatchesByStatus)) {
            return matchCountContext;
        }

        Map<MatchStatus, Set<Long>> matchesByStatus = matchCountContext.getMatchesByStatus();
        Map<String, Map<String, Map<String, Object>>> redisStoreMatches = redisDeltaFeedDto.getMatches();

        if (MapUtils.isEmpty(redisStoreMatches)) {
            return matchCountContext;
        }

        Map<MatchStatus, Set<Long>> changedMatchesMap = new HashMap<MatchStatus, Set<Long>>();

        if (MapUtils.isNotEmpty(matchesByStatus)) {
            for (MatchStatus matchStatus : matchesByStatus.keySet()) {
                Set<Long> matchIdsByStatus = matchesByStatus.get(matchStatus);
                Iterator<Long> matchIdsIte = matchIdsByStatus.iterator();
                matchIdsIte.forEachRemaining(mid -> {
                    if (redisStoreMatches != null && redisStoreMatches.get(mid) != null) {
                        Map<String, Map<String, Object>> redisMatch = redisStoreMatches.get(mid);
                        Map<String, Object> deltaMatchSection = redisMatch.get(MatchFeedModel.SECTIONS.MATCH);
                        Object matchStatusObj = deltaMatchSection != null ? deltaMatchSection
                                .get(MatchFeedModel.MATCH.STATUS) : null;
                        if (matchStatusObj != null
                                && Integer.valueOf(matchStatusObj.toString()) != matchStatus.ordinal()) {
                            Set<Long> changedMatchesByStatusSet = changedMatchesMap.get(matchStatus);
                            if (changedMatchesByStatusSet == null) {
                                changedMatchesByStatusSet = new HashSet<Long>();
                                changedMatchesMap.put(matchStatus, changedMatchesByStatusSet);
                            }
                            changedMatchesByStatusSet.add(mid);
                            matchIdsIte.remove();
                            if (matchStatus == MatchStatus.NEW) {
                                Set<Long> recentNewMatches = matchCountContext.getRecentNewMatches();
                                if (CollectionUtils.isNotEmpty(recentNewMatches)) {
                                    recentNewMatches.remove(mid);
                                }
                            }
                        }
                    }
                });
            }

        }

        if (MapUtils.isNotEmpty(changedMatchesMap)) {
            changedMatchesMap.forEach((a, b) -> {
                matchesByStatus.get(a).addAll(b);
            });
        }
        return matchCountContext;
    }
}
