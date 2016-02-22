package com.eharmony.services.mymatchesservice.store;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.BasicStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.store.serializer.LegacyMatchDataFeedDtoSerializer;
import com.google.common.base.Preconditions;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.SECTIONS.MATCH;
import rx.Observable;
/**
 * Redis repository to get the delta matches
 * @author gwang
 *
 */
public class MatchFeedRedisStore implements RedisStoreFeedService{
	public static final String TIMESTAMP_NAME = "lastModifiedDate";
    private static final Logger log = LoggerFactory.getLogger(MatchFeedRedisStore.class);
    private RedisTemplate<String, String> redisMatchDataTemplate;
    private LegacyMatchDataFeedDtoSerializer matchDataFeedSerializer;
    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;
    
    private static final String METRICS_HIERARCHY_PREFIX = MatchDataFeedVoldyStore.class.getCanonicalName();
    
    private static final String METRICS_GET_REDIS_SAFE = "getMatchesFromRedisSafe";

    public MatchFeedRedisStore () {
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
        Timer.Context timerContext = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX, METRICS_GET_REDIS_SAFE);
        long startTime = System.currentTimeMillis();

        try {
            HashOperations<String, String, String> hashOps = redisMatchDataTemplate.opsForHash();
            Map<String, String> hashEntries = hashOps.entries(userid);
            LegacyMatchDataFeedDto feedDto = null;
            //if Redis has several feeds, take the match out for each feed and
            // combine them as one match feed DTO, copy the time stamp at the 
            // feed level to match level
            for (Map.Entry<String, String> entry:hashEntries.entrySet()) {
                //key is the match id
                String key = entry.getKey();
                String value = entry.getValue();
                
                LegacyMatchDataFeedDto matchDto = matchDataFeedSerializer.fromJson(value);
                Date updateAt = matchDto.getUpdatedAt();
                Map<String, Map<String, Object>> singleUpdatedMatch = matchDto.getMatches().get(key);
                if (feedDto == null) {
                    //first match under this user, set it to response
                    response.setLegacyMatchDataFeedDto(matchDto);
                    feedDto = matchDto;
                } else {
                    //add match to response.
                    feedDto.getMatches().put(key, singleUpdatedMatch);
                }
                
                //add time stamp to match level
                singleUpdatedMatch.get(MATCH).put(TIMESTAMP_NAME, updateAt.getTime());
            };
            
            if (feedDto != null) {
                feedDto.setTotalMatches(hashEntries.size());
                response.setFeedAvailable(true);
            }
        } catch (Exception exp) {
            log.warn("Error while getting feed for user{} from Redis", userid, exp);
            //re throw exception so the down stream observer can deal with it.
            throw exp;
        } finally {
            timerContext.stop();
            long endTime = System.currentTimeMillis();
            log.info("Total time to get the feed from Redis for user {} is {} MS", userid, (endTime - startTime));
        }
        return response;

    }
    
    public Observable<LegacyMatchDataFeedDtoWrapper> getUserMatchesSafe(BasicStoreFeedRequestContext request) {
        return Observable.defer(() -> Observable.just(getUserMatchesSafeFromRedis(request)));
    }
}
