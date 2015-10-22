package com.eharmony.services.mymatchesservice.service;

import java.util.List;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public interface UserMatchesFeedService {

    public LegacyMatchDataFeedDto getUserMatches(Integer userId);
    
    public List<MatchDataFeedItemDto> getUserMatchesInternal(Integer userId);
    
    public MatchDataFeedItemDto getUserMatch(Integer userId, Long matchId);
	
}
