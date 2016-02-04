package com.eharmony.services.mymatchesservice.store;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.eharmony.services.mymatchesservice.service.BasicStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.store.serializer.LegacyMatchDataFeedDtoSerializer;
import com.google.common.base.Preconditions;

import rx.Observable;
/**
 * Redis repository to get the 
 * @author gwang
 *
 */
public class MatchFeedRedisStore implements RedisStoreFeedService{
    private static final Logger log = LoggerFactory.getLogger(MatchFeedRedisStore.class);
    private RedisTemplate<String, String> redisMatchDataTemplate;
    private LegacyMatchDataFeedDtoSerializer matchDataFeedSerializer;
    
    MatchFeedRedisStore () {
    }

    public MatchFeedRedisStore(RedisTemplate<String, String> redisMatchDataTemplate,
            LegacyMatchDataFeedDtoSerializer matchDataFeedSerializer) {

        Preconditions.checkNotNull(redisMatchDataTemplate);

        Preconditions.checkNotNull(matchDataFeedSerializer);

        this.redisMatchDataTemplate = redisMatchDataTemplate;
        this.matchDataFeedSerializer = matchDataFeedSerializer;
    }

    protected LegacyMatchDataFeedDtoWrapper getUserMatchesSafeFromRedis(BasicStoreFeedRequestContext request) {
        long userIdLong = request.getMatchFeedQueryContext().getUserId();
        String userid = String.valueOf(userIdLong);
        LegacyMatchDataFeedDtoWrapper response = new LegacyMatchDataFeedDtoWrapper(userIdLong);
        
        try {
            HashOperations<String, String, String> hashOps = redisMatchDataTemplate.opsForHash();
            Map<String, String> hashEntries = hashOps.entries(userid);
            LegacyMatchDataFeedDto feedDto = response.getLegacyMatchDataFeedDto();
            for (Map.Entry<String, String> entry:hashEntries.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                LegacyMatchDataFeedDto matchDto = matchDataFeedSerializer.fromJson(value);
                if (feedDto == null) {
                    response.setLegacyMatchDataFeedDto(matchDto);
                    feedDto = matchDto;
                } else {
                    Map<String, Map<String, Object>> singleUpdatedMatch = matchDto.getMatches().get(key);
                    feedDto.getMatches().put(key, singleUpdatedMatch);
                }
            };
            
            if (feedDto != null) {
                feedDto.setTotalMatches(hashEntries.size());
                response.setFeedAvailable(true);
            }
        } catch (Exception exp) {
            log.warn("Error while getting feed for user{} from Redis", userid, exp);
            //re throw exception so the down stream observer can deal with it.
            throw exp;
        }
        return response;

    }
    
    public Observable<LegacyMatchDataFeedDtoWrapper> getUserMatchesSafe(BasicStoreFeedRequestContext request) {
        return Observable.defer(() -> Observable.just(getUserMatchesSafeFromRedis(request)));
    }
}
