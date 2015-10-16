package com.eharmony.services.mymatchesservice.service;

import java.util.List;

import com.eharmony.datastore.model.MatchDataFeedItemDto;

public interface UserMatchesFeedService {

	//public List<MatchDataFeedItemDto> getUserMatches(Integer userId);
	//public MatchDataFeedItemDto getUserMatch(Integer userId, Long matchId);
    public List<MatchDataFeedItemDto> getUserMatches(Integer userId);
	
}
