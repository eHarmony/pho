package com.eharmony.services.mymatchesservice.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.eharmony.datastore.model.MatchCountDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.singles.common.status.MatchStatus;

public class MatchCountContext {

    private MatchCountDto matchCountDto;
    private MatchCountRequestContext matchCountRequestContext;
    private LegacyMatchDataFeedDto redisMatchDataFeedDto;
    private Map<MatchStatus, Set<Long>> matchesByStatus = new HashMap<MatchStatus, Set<Long>>();
    private Set<Long> recentNewMatches = new HashSet<Long>();

    public MatchCountDto getMatchCountDto() {
        return matchCountDto;
    }

    public void setMatchCountDto(MatchCountDto matchCountDto) {
        this.matchCountDto = matchCountDto;
    }

    public MatchCountRequestContext getMatchCountRequestContext() {
        return matchCountRequestContext;
    }

    public void setMatchCountRequestContext(MatchCountRequestContext matchCountRequestContext) {
        this.matchCountRequestContext = matchCountRequestContext;
    }

    public LegacyMatchDataFeedDto getRedisMatchDataFeedDto() {
        return redisMatchDataFeedDto;
    }

    public void setRedisMatchDataFeedDto(LegacyMatchDataFeedDto redisMatchDataFeedDto) {
        this.redisMatchDataFeedDto = redisMatchDataFeedDto;
    }

    public Map<MatchStatus, Set<Long>> getMatchesByStatus() {
        return matchesByStatus;
    }

    public void setMatchesByStatus(Map<MatchStatus, Set<Long>> matchCountsByStatus) {
        this.matchesByStatus = matchCountsByStatus;
    }

    public void putMatchCountsByStatus(MatchStatus matchStatus, Set<Long> matchIds) {
        matchesByStatus.put(matchStatus, matchIds);
    }
    
    public Set<Long> getRecentNewMatches() {
        return recentNewMatches;
    }

    public void setRecentNewMatches(Set<Long> recentNewMatches) {
        this.recentNewMatches = recentNewMatches;
    }

}
