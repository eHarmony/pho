package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.codahale.metrics.Timer;
import com.eharmony.datastore.store.impl.JsonDataStore;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

/**
 * Repository class to fetch the data from voldemort store
 * 
 * @author vvangapandu
 *
 */
public class MatchDataFeedStore extends JsonDataStore<LegacyMatchDataFeedDto> {

    private static final Logger logger = LoggerFactory.getLogger(MatchDataFeedStore.class);

    /**
     * Reads matches from Voldemort.
     *
     * @param userId long,  feed userId
     *
     * @return LegacyMatchDataFeedDtoWrapper
     */
    public LegacyMatchDataFeedDtoWrapper getMatchesSafe(long userId) {

        Timer.Context timerContext = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFromVoldySafe").time();
        
        long startTime = System.currentTimeMillis();
        logger.debug("Getting feed from Voldy for user {}, start time {}", userId, startTime);
        LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(userId);

        try {
            LegacyMatchDataFeedDto feed = fetchValue(String.valueOf(userId));
            if (feed != null) {
                logger.debug("found {} matches in Voldemort for user {}", feed.getMatches().size(), userId);
                feedWrapper.setFeedAvailable(true);
                feedWrapper.setLegacyMatchDataFeedDto(feed);
            } else {
                logger.debug("no matches in Voldemort for user {}", userId);
                feedWrapper.setFeedAvailable(false);
            }
        } catch (Throwable t) {
            logger.warn("Exception while fetching the feed from voldemort for user {}", userId, t);
            //TODO meter?
            feedWrapper.setError(t);
            feedWrapper.setFeedAvailable(false);
        }
        long elapsedTime = timerContext.stop();
        logger.info("Total time to get the feed from voldy for user {} is {} MS", userId, elapsedTime);
        return feedWrapper;

    }

    /**
     * Observable to read the data from voldemort store
     * 
     * @param request MatchFeedRequestContext
     * @return LegacyMatchDataFeedDtoWrapper observer
     */
    public Observable<LegacyMatchDataFeedDtoWrapper> getMatchesObservableSafe(MatchFeedRequestContext request) {
        Observable<LegacyMatchDataFeedDtoWrapper> voldyFeed = Observable.defer(() -> Observable
                .just(getMatchesSafe(request.getUserId())));
        voldyFeed
                .onErrorReturn(ex -> {
                    logger.warn(
                            "Exception while fetching data from voldemort for user {} and returning null object for safe method",
                            request.getUserId(), ex);
                    return null;
                });
        return voldyFeed;
    }
}
