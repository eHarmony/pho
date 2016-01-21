package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

public class RedisStoreFeedResponse extends AbstractStoreFeedResponse{

    private LegacyMatchDataFeedDto redisStoreFeedDto;

	public LegacyMatchDataFeedDto getRedisStoreFeedDto() {
		return redisStoreFeedDto;
	}

	public void setRedisStoreFeedDto(LegacyMatchDataFeedDto redisStoreFeedDto) {
		this.redisStoreFeedDto = redisStoreFeedDto;
	}

	public RedisStoreFeedResponse(final MatchStatusGroupEnum matchStatusGroup) {
    	super(matchStatusGroup);
    }

}
