package com.eharmony.services.mymatchesservice.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

import com.codahale.metrics.Timer;
import com.eharmony.datastore.store.impl.JsonDataStore;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.internal.DataServiceStateEnum;

/**
 * Repository class to fetch the data from voldemort store
 * 
 * @author vvangapandu
 *
 */
public class MatchDataFeedVoldyStore extends JsonDataStore<LegacyMatchDataFeedDto> {

    private static final Logger logger = LoggerFactory.getLogger(MatchDataFeedVoldyStore.class);

    /**
     * Reads matches from Voldemort.
     *
     * @param queryContext      MatchFeedQueryContext
     *
     * @return LegacyMatchDataFeedDtoWrapper
     */
    public LegacyMatchDataFeedDtoWrapper getMatchesSafe(MatchFeedQueryContext queryContext) {

        long userId = queryContext.getUserId();
        logger.debug("Getting feed from Voldy for user {}", userId);
        long startTime = System.currentTimeMillis();

        LegacyMatchDataFeedDtoWrapper feedWrapper = buildWrapperForMockRequest(queryContext);
        // Return the feed if the request is mock request
        if (feedWrapper != null) {
            logger.warn("Recieved the mock volde feed request for user {}, please verify this is intended?", userId);
            return feedWrapper;
        }
        Timer.Context timerContext = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFromVoldySafe").time();
        feedWrapper = new LegacyMatchDataFeedDtoWrapper(userId);

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
            // TODO meter?
            feedWrapper.setError(t);
            feedWrapper.setFeedAvailable(false);
        } finally {
            timerContext.stop();
            long endTime = System.currentTimeMillis();
            logger.info("Total time to get the feed from voldy for user {} is {} MS", userId, (endTime - startTime));
        }

        return feedWrapper;

    }

    private LegacyMatchDataFeedDtoWrapper buildWrapperForMockRequest(MatchFeedQueryContext queryContext) {
        DataServiceStateEnum mockVoldeRequest = queryContext.getVoldyState();
        if (mockVoldeRequest == null) {
            return null;
        }
        switch (mockVoldeRequest) {
        case ENABLED:
            return null;
        case EMPTY:
            LegacyMatchDataFeedDtoWrapper empty = new LegacyMatchDataFeedDtoWrapper(queryContext.getUserId());
            empty.setFeedAvailable(true);
            empty.setLegacyMatchDataFeedDto(new LegacyMatchDataFeedDto());
            return empty;
        case DISABLED:
            LegacyMatchDataFeedDtoWrapper disabled = new LegacyMatchDataFeedDtoWrapper(queryContext.getUserId());
            disabled.setFeedAvailable(false);
            disabled.setLegacyMatchDataFeedDto(null);
            disabled.setError(new Exception("Voldy flag set to DISABLED in request."));
            return disabled;
        default:
            return null;
        }
    }

    /**
     * Observable to read the data from voldemort store
     * 
     * @param queryContext
     *            MatchFeedQueryContext
     * @return LegacyMatchDataFeedDtoWrapper observer
     */
    public Observable<LegacyMatchDataFeedDtoWrapper> getMatchesObservableSafe(MatchFeedQueryContext queryContext) {
        Observable<LegacyMatchDataFeedDtoWrapper> voldyFeed = Observable.defer(() -> Observable
                .just(getMatchesSafe(queryContext)));
        voldyFeed
                .onErrorReturn(ex -> {
                    logger.warn(
                            "Exception while fetching data from voldemort for user {} and returning null object for safe method",
                            queryContext.getUserId(), ex);
                    return null;
                });
        return voldyFeed;
    }
}
