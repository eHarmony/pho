package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.rest.MatchCountRequestContext;

import rx.Observable;

public interface HBaseStoreFeedService {

    public Observable<HBaseStoreFeedResponse> getUserMatchesByStatusGroupSafe(HBaseStoreFeedRequestContext request);
    
    public Integer getUserMatchesCount(MatchCountRequestContext request);
    
    public Integer getUserNewMatchesCount(MatchCountRequestContext request);
}
