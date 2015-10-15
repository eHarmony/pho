package com.eharmony.services.mymatchesservice.service;

import java.util.List;

public interface UserMatchesFeedService {

	//public List<MatchDataFeedItemDto> getUserMatches(Integer userId);
	//public MatchDataFeedItemDto getUserMatch(Integer userId, Long matchId);
    public List<Object> getUserMatches(Integer userId);
	
}
