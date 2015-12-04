package com.eharmony.services.mymatchesservice.service;

import java.util.List;
import java.util.Set;

import rx.Observable;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public interface UserMatchesHBaseStoreFeedService {
    
    public List<MatchDataFeedItemDto> getUserMatchesInternal(long userId);
    
    public MatchDataFeedItemDto getUserMatch(long userId, long matchId);
    
    public Observable<Set<MatchDataFeedItemDto>> getUserMatchesFromHBaseStoreSafe(MatchFeedRequestContext request);
    
}
