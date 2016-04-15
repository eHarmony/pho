package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.rest.MatchCountRequestContext;

import rx.Observable;

public interface HBaseStoreFeedService {

    public Observable<HBaseStoreFeedResponse> getUserMatchesByStatusGroupSafe(HBaseStoreFeedRequestContext request);

    public Observable<HBaseStoreCountResponse> getUserMatchesCount(MatchCountRequestContext request);
    public Observable<HBaseStoreCountResponse> getUserNewMatchesCount(MatchCountRequestContext request);

    public Observable<HBaseStoreFeedResponse> getUserMatchSafe(HBaseStoreFeedRequestContext request);

}
