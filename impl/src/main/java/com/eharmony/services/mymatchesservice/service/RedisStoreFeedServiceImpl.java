package com.eharmony.services.mymatchesservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.merger.HBaseRedisFeedMergeStrategyImpl;

import rx.Observable;

@Component
public class RedisStoreFeedServiceImpl implements RedisStoreFeedService {

    private static final Logger log = LoggerFactory.getLogger(RedisStoreFeedServiceImpl.class);

	@Override
	public Observable<RedisStoreFeedResponse> getUserMatchesByStatusGroupSafe(
			BasicStoreFeedRequestContext request) {

		// TODO: integrate with Redis DataStoreApi
		log.warn("REDIS STORE NOT YET IMPLEMENTED.");
		
		RedisStoreFeedResponse response = new RedisStoreFeedResponse(request.getMatchStatusGroup());
		response.setDataAvailable(false);
		response.setError(new UnsupportedOperationException("Redis Store not yet implemented."));
		
		return Observable.just(response);
	}

}
