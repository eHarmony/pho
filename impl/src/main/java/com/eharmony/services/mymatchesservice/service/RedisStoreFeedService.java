package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

import rx.Observable;

public interface RedisStoreFeedService {
    
    public Observable<LegacyMatchDataFeedDtoWrapper> getUserMatchesSafe(final long userId);

}
