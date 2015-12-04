package com.eharmony.services.mymatchesservice.rest;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.event.RefreshEventSender;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.UserMatchesHBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyManager;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.service.transform.HBASEToLegacyFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedVoldyStore;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

/**
 * Handles the GetMatches feed async requests.
 * 
 * Feed will be fetched from voldemort store and hbase store in parallel and merges the data based on merge strategy.
 * 
 * This handler uses safe methods, will return valid results as long as at least one of the stores available and respond
 * with feed on time.
 * 
 * @author vvangapandu
 *
 */
@Component
public class MatchFeedAsyncRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(MatchFeedAsyncRequestHandler.class);

    @Resource
    private ExecutorServiceProvider executorServiceProvider;

    @Resource
    private UserMatchesHBaseStoreFeedService userMatchesFeedService;

    @Resource
    private MatchDataFeedVoldyStore voldemortStore;

    @Resource(name = "getMatchesFeedEnricherChain")
    private MatchFeedTransformerChain getMatchesFeedEnricherChain;

    @Resource(name = "getMatchesFeedFilterChain")
    private MatchFeedTransformerChain getMatchesFeedFilterChain;

    @Resource
    private RefreshEventSender refreshEventSender;

    @Resource
    private MatchStatusGroupResolver matchStatusGroupResolver;

    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;
    
    @Resource
    private HBASEToLegacyFeedTransformer hbaseToLegacyFeedTransformer;

    /**
     * Matches feed will be returned after applying the filters and enriching the data from feed stores. Feed will be
     * fetched from voldemort store and hbase store in parallel and merges the data based on merge strategy.
     * 
     * This handler uses safe methods, will return valid results as long as at least one of the stores available and
     * respond with feed on time.
     * 
     * @param matchFeedQueryContext
     *            MatchFeedQueryContext
     * @param asyncResponse
     *            AsyncResponse
     */

    public void getMatchesFeed(final MatchFeedQueryContext matchFeedQueryContext, final AsyncResponse asyncResponse) {

        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFeedAsync").time();
        long userId = matchFeedQueryContext.getUserId();
        MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
        request.setFeedMergeType(FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE);

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        matchQueryRequestObservable = matchQueryRequestObservable.zipWith(voldemortStore.getMatchesObservableSafe(matchFeedQueryContext),
                populateLegacyMatchesFeed).subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
        
        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable, matchFeedQueryContext,
                FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE, false);

        matchQueryRequestObservable.subscribe(response -> {
            handleFeedResponse(response);
            long duration = t.stop();
            logger.debug("Match feed created for user {}, duration {}", userId, duration);
            ResponseBuilder builder = buildResponse(response);
            asyncResponse.resume(builder.build());
        }, (throwable) -> {
            long duration = t.stop();
            logger.error("Exception creating match feed for user {}, duration {}", userId, duration, throwable);
            asyncResponse.resume(throwable);
        }, () -> {
            logger.info("Why are we here? when try to get feed for user {}", userId);
            asyncResponse.resume("");
        });
    }

    private void populateContextWithHBaseMatchesOnVoldeError(MatchFeedRequestContext request) {

        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFromHBaseOnVoldeError").time();
        MatchFeedQueryContext queryContext = request.getMatchFeedQueryContext();
        long userId = queryContext.getUserId();
        request.setFallbackRequest(request.isFallbackRequest());
        request.setFeedMergeType(FeedMergeStrategyType.HBASE_FEED_ONLY);

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);

        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable, queryContext, null, false);

        CountDownLatch latch = new CountDownLatch(1);

        matchQueryRequestObservable.subscribe(response -> {
            long duration = t.stop();
            logger.debug("Fetched feed from HBase for fallback. user {}, duration {}", userId, duration);
            latch.countDown();
        }, (throwable) -> {
            long duration = t.stop();
            logger.error("Exception while fetching feed from HBase fallback. user {}, duration {}", userId, duration,
                    throwable);
            latch.countDown();
        }, () -> {
            logger.info("Why are we here? when try to get feed for user {}", userId);
            latch.countDown();
        });

        try {
            latch.wait(30000);
        } catch (Exception ex) {
            logger.warn("Exception while waiting for results from HBase fallback request for user {}", userId, ex);
        }

    }

    private Observable<MatchFeedRequestContext> chainHBaseFeedRequestsByStatus(
            Observable<MatchFeedRequestContext> matchQueryRequestObservable,
            final MatchFeedQueryContext matchFeedQueryContext, final FeedMergeStrategyType feedMergeType,
            final boolean isFallbackRequest) {

        Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> requestedMatchStatusGroups = matchStatusGroupResolver
                .buildMatchesStatusGroups(matchFeedQueryContext.getUserId(), matchFeedQueryContext.getStatuses());

        if (MapUtils.isEmpty(requestedMatchStatusGroups)) {
            logger.warn(
                    "somethig is wrong, request doesn't contain valid match statuses to fetch feed from HBase for user {}",
                    matchFeedQueryContext.getUserId());
            return matchQueryRequestObservable;
        }

        for(Entry<MatchStatusGroupEnum, Set<MatchStatusEnum>> entry : requestedMatchStatusGroups.entrySet()) {
            HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(matchFeedQueryContext);
            requestContext.setFallbackRequest(isFallbackRequest);
            requestContext.setFeedMergeType(feedMergeType);
            requestContext.setMatchStatuses(entry.getValue());
            requestContext.setMatchStatusGroup(entry.getKey());
            matchQueryRequestObservable = matchQueryRequestObservable.zipWith(hbaseStoreFeedService.getUserMatchesByStatusGroupSafe(requestContext),
                    populateHBaseMatchesFeed).subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
        }
        return matchQueryRequestObservable;
    }

    private void handleFeedResponse(MatchFeedRequestContext context) {
        refreshEventSender.sendRefreshEvent(context);
        executeFallbackIfRequired(context);
        aggregateHBaseFeedItems(context);
        // convert the hbase feed to voldy feed by using legacy feed transformer and make the hbase feed empty, we
        // need to do this here to honor the pagination
        hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeedIfRequired(context);
        getMatchesFeedFilterChain.execute(context);
        FeedMergeStrategyManager.getMergeStrategy(context).merge(context, userMatchesFeedService);
        getMatchesFeedEnricherChain.execute(context);
    }
    
	private void aggregateHBaseFeedItems(MatchFeedRequestContext context) {
        Map<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> feedItemsByGroups = context
                .getHbaseFeedItemsByStatusGroup();
        if (MapUtils.isNotEmpty(feedItemsByGroups)) {
            Set<MatchDataFeedItemDto> storeFeedItems = new HashSet<MatchDataFeedItemDto>();
            feedItemsByGroups.forEach((k, v) -> {
                storeFeedItems.addAll(v);
            });
            context.setNewStoreFeed(storeFeedItems);
        }
    }

    private void executeFallbackIfRequired(MatchFeedRequestContext response) {
        if (shouldFallbackToHBase(response)) {
            // TODO make sure there is no concurent modificaiton - VIJAY
            response.setFallbackRequest(true);
            populateContextWithHBaseMatchesOnVoldeError(response);
        }
    }

    // Unit test please
    private boolean shouldFallbackToHBase(MatchFeedRequestContext response) {
        LegacyMatchDataFeedDtoWrapper legacyFeedWrapper = response.getLegacyMatchDataFeedDtoWrapper();
        if (legacyFeedWrapper != null && legacyFeedWrapper.isFeedAvailable()
                && legacyFeedWrapper.getLegacyMatchDataFeedDto() != null
                && MapUtils.isNotEmpty(legacyFeedWrapper.getLegacyMatchDataFeedDto().getMatches())) {
            return false;
        }

        // Voldemort feed is empty but there are matches in Hbase
        if (MapUtils.isNotEmpty(response.getHbaseFeedItemsByStatusGroup())) {
            for (Entry<MatchStatusGroupEnum, Set<MatchDataFeedItemDto>> result : response
                    .getHbaseFeedItemsByStatusGroup().entrySet())
                if (CollectionUtils.isNotEmpty(result.getValue())) {
                    return true;
                }
        }

        return false;
    }

    private ResponseBuilder buildResponse(MatchFeedRequestContext requestContext) {
        LegacyMatchDataFeedDtoWrapper wrapper = requestContext.getLegacyMatchDataFeedDtoWrapper();
        if (wrapper != null) {
            ResponseBuilder builder = Response.ok().entity(wrapper.getLegacyMatchDataFeedDto());
            builder.status(Status.OK);
            return builder;
        } else {
            ResponseBuilder builder = Response.serverError().status(Status.INTERNAL_SERVER_ERROR);
            return builder;
        }
    }

    private Func2<MatchFeedRequestContext, HBaseStoreFeedResponse, MatchFeedRequestContext> populateHBaseMatchesFeed = (
            request, matchesFeedResponse) -> {
        if (CollectionUtils.isNotEmpty(matchesFeedResponse.getHbaseStoreFeedItems())) {
            request.putFeedItemsInMapByStatusGroup(matchesFeedResponse.getMatchStatusGroup(),
                    matchesFeedResponse.getHbaseStoreFeedItems());
        }
        return request;
    };

    private Func2<MatchFeedRequestContext, LegacyMatchDataFeedDtoWrapper, MatchFeedRequestContext> populateLegacyMatchesFeed = (
            request, legacyMatchDataFeedDtoWrapper) -> {

        logger.debug("Voldemort State flag = {}", request.getMatchFeedQueryContext().getVoldyState());
        request.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
        return request;
    };

}
