package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.store.serializer.LegacyMatchDataFeedDtoSerializer;
import com.google.common.base.Preconditions;

public class MatchFeedRedisStore {
	private static final Logger log = LoggerFactory.getLogger(MatchFeedRedisStore.class);
	private RedisTemplate<String, String> redisMatchDataTemplate;
	private LegacyMatchDataFeedDtoSerializer matchDataFeedSerializer;

	public MatchFeedRedisStore(RedisTemplate<String, String> redisMatchDataTemplate,
			LegacyMatchDataFeedDtoSerializer matchDataFeedSerializer) {

		Preconditions.checkNotNull(redisMatchDataTemplate);

		Preconditions.checkNotNull(matchDataFeedSerializer);

		this.redisMatchDataTemplate = redisMatchDataTemplate;
		this.matchDataFeedSerializer = matchDataFeedSerializer;
	}

	public LegacyMatchDataFeedDtoWrapper getMatchesSafe(MatchFeedQueryContext queryContext) {
		String userid = String.valueOf(queryContext.getUserId());
		LegacyMatchDataFeedDtoWrapper resultWrapper = new LegacyMatchDataFeedDtoWrapper(queryContext.getUserId());
		try {
			HashOperations<String, String, String> hashOps = redisMatchDataTemplate.opsForHash();
			hashOps.entries(userid).forEach((key, value) -> {
				resultWrapper.setLegacyMatchDataFeedDto(matchDataFeedSerializer.fromJson(value));
			});
		} catch (Exception exp) {
			log.warn("Error while getting feed for user{} from Redis", userid, exp);
		}

		return resultWrapper;
	}
}
