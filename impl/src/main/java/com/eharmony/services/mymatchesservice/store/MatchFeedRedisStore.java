package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.base.Preconditions;

public class MatchFeedRedisStore {
    private static final Logger log = LoggerFactory.getLogger(MatchFeedRedisStore.class);
    private RedisTemplate<String, String> redisMatchDataTemplate;

    public MatchFeedRedisStore(RedisTemplate<String, String> redisMatchDataTemplate, Long matchDataTTLSecs) {

        Preconditions.checkNotNull(redisMatchDataTemplate);
        Preconditions.checkNotNull(matchDataTTLSecs);

        this.redisMatchDataTemplate = redisMatchDataTemplate;
    }

}
