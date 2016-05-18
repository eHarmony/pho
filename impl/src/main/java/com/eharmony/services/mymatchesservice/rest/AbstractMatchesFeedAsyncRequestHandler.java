package com.eharmony.services.mymatchesservice.rest;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.eharmony.singles.common.util.ResourceNotFoundException;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Handles the GetMatches feed async requests. specific implementation has to override the response handler method to
 * filter the content based on API requirements.
 * 
 * This abstract class manages the requests to HBase and Redis and feed merge from both stores.
 * 
 * Delegates the response handling for further filtering and enrichment to specific implemntation class
 * 
 * @author vvangapandu
 *
 */
public abstract class AbstractMatchesFeedAsyncRequestHandler implements MatchesFeedAsyncRequestHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedisStoreFeedService redisStoreFeedService;

    @Resource
    private ExecutorServiceProvider executorServiceProvider;

    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;

    @Resource
    private MatchStatusGroupResolver matchStatusGroupResolver;

    /**
     * Matches feed will be returned after applying the filters and enriching the data from feed stores. Feed will be
     * fetched from hbase store and redis in parallel and merges the data based on merge strategy.
     * 
     * This handler uses safe methods, will return valid results as long as at least one of the stores available and
     * respond with feed on time.
     * 
     * @param matchFeedQueryContext
     *            MatchFeedQueryContext
     * @param asyncResponse
     *            AsyncResponse
     */
    @Override
    public void getMatchesFeed(MatchFeedQueryContext matchFeedQueryContext, AsyncResponse asyncResponse) {
        @SuppressWarnings("resource")
        Timer.Context t = buildTimerContext();
        long userId = matchFeedQueryContext.getUserId();
        Observable<MatchFeedRequestContext> matchQueryRequestObservable = makeMqsRequestObservable(matchFeedQueryContext);

        matchQueryRequestObservable.subscribe(response -> {
            boolean feedNotFound = false;
            try {
                handleFeedResponse(response);
            } catch (ResourceNotFoundException e) {
                feedNotFound = true;
            }

            t.stop();
            //logger.debug("Match feed created for user {}, duration {}", userId, duration);
            ResponseBuilder builder = buildResponse(response, feedNotFound);
            performFinalTasksHook(response, feedNotFound);
            asyncResponse.resume(builder.build());
        }, (throwable) -> {
            long duration = t.stop();
            logger.error("Exception creating match feed for user {}, duration {}", userId, duration, throwable);
            asyncResponse.resume(throwable);
        }, () -> {
            asyncResponse.resume("");
        });

    }

    protected abstract Timer.Context buildTimerContext();

    protected ResponseBuilder buildResponse(MatchFeedRequestContext requestContext, boolean feedNotFound) {
        if (feedNotFound) {
            // just logging it here for any action to be taken if the need be.
            // an empty feed will be returned for such users.
            logger.info("Feed not available for userId: {}", requestContext.getUserId());
        }
        LegacyMatchDataFeedDtoWrapper wrapper = requestContext.getLegacyMatchDataFeedDtoWrapper();
        if (wrapper != null) {
            ResponseBuilder builder = Response.ok().entity(wrapper.getLegacyMatchDataFeedDto());
            builder.status(Status.OK);
            return builder;
        } else if (requestContext.getHbaseFeedItemsByStatusGroup() != null) {
            ResponseBuilder builder = Response.ok().entity(new LegacyMatchDataFeedDto());
            builder.status(Status.OK);
            return builder;
        } else {
            ResponseBuilder builder = Response.serverError().status(Status.INTERNAL_SERVER_ERROR);
            return builder;
        }
    }

    public abstract void handleFeedResponse(MatchFeedRequestContext context);

    protected Observable<MatchFeedRequestContext> makeMqsRequestObservable(
            final MatchFeedQueryContext matchFeedQueryContext) {
        MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        Observable<LegacyMatchDataFeedDtoWrapper> redisStoreFeedObservable = redisStoreFeedService
                .getUserMatchesSafe(matchFeedQueryContext.getUserId());

        matchQueryRequestObservable = matchQueryRequestObservable.zipWith(redisStoreFeedObservable,
                populateRediesStoreMatchesFeed).subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));

        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable,
                matchFeedQueryContext);
        
        return matchQueryRequestObservable;
    }

    private Observable<MatchFeedRequestContext> chainHBaseFeedRequestsByStatus(
            Observable<MatchFeedRequestContext> matchQueryRequestObservable,
            final MatchFeedQueryContext matchFeedQueryContext) {

        Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> requestedMatchStatusGroups = matchStatusGroupResolver
                .buildMatchesStatusGroups(matchFeedQueryContext.getUserId(), matchFeedQueryContext.getStatuses());

        if (MapUtils.isEmpty(requestedMatchStatusGroups)) {
            logger.warn(
                    "somethig is wrong, request doesn't contain valid match statuses to fetch feed from HBase for user {}",
                    matchFeedQueryContext.getUserId());
            return matchQueryRequestObservable;
        }

        for (Entry<MatchStatusGroupEnum, Set<MatchStatusEnum>> entry : requestedMatchStatusGroups.entrySet()) {
            logger.debug("create observable to fetch matches for group {} and user {}", entry.getKey(),
                    matchFeedQueryContext.getUserId());
            HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(matchFeedQueryContext);
            requestContext.setMatchStatuses(entry.getValue());
            requestContext.setMatchStatusGroup(entry.getKey());
            matchQueryRequestObservable = matchQueryRequestObservable.zipWith(
                    hbaseStoreFeedService.getUserMatchesByStatusGroupSafe(requestContext), populateHBaseMatchesFeed)
                    .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
        }
        
        // Add Spotlight Matches
        Set<MatchStatusEnum> matchStatuses = requestedMatchStatusGroups.values().stream().flatMap(set -> set.stream()).collect(Collectors.toSet());
        HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(matchFeedQueryContext);
        requestContext.setMatchStatuses(matchStatuses);
        
        matchQueryRequestObservable = matchQueryRequestObservable.zipWith(
                hbaseStoreFeedService.getSpotlitUserMatchesSafe(requestContext), populateHBaseMatchesFeedFromNonGroupedResponse)
                .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));

        return matchQueryRequestObservable;
    }

    // Handler for async feed request from Hbase
    private Func2<MatchFeedRequestContext, HBaseStoreFeedResponse, MatchFeedRequestContext> populateHBaseMatchesFeed = (
            request, matchesFeedResponse) -> {
        if (CollectionUtils.isNotEmpty(matchesFeedResponse.getHbaseStoreFeedItems())) {
            request.putFeedItemsInMapByStatusGroup(matchesFeedResponse.getMatchStatusGroup(),
                    matchesFeedResponse.getHbaseStoreFeedItems());
        }
        return request;
    };
    
    private Func2<MatchFeedRequestContext, HBaseStoreFeedResponse, MatchFeedRequestContext> populateHBaseMatchesFeedFromNonGroupedResponse = (
            request, matchesFeedResponse) -> {
        if (CollectionUtils.isNotEmpty(matchesFeedResponse.getHbaseStoreFeedItems())) {
            Map<MatchStatusGroupEnum, List<MatchDataFeedItemDto>> matchStatusGroupToMatchFeedItems = matchesFeedResponse.getHbaseStoreFeedItems().stream().collect(Collectors.<MatchDataFeedItemDto, MatchStatusGroupEnum>groupingBy(item -> {
                int status = item.getMatch().getStatus();
                MatchStatusEnum matchStatus = MatchStatusEnum.fromInt(status);
                switch (matchStatus) {
                case NEW:
                    return MatchStatusGroupEnum.NEW;
                case ARCHIVED:
                    return MatchStatusGroupEnum.ARCHIVE;
                case OPENCOMM:
                case MYTURN:
                case THEIRTURN:
                    return MatchStatusGroupEnum.COMMUNICATION;
                case CLOSED:
                    logger.warn("Closed matches are not supported in this system...");
                    break;
                default:
                    logger.error("Unable to assign match status {} to a match status group.", matchStatus);            
                }
                return MatchStatusGroupEnum.NEW;
            }));
            for(MatchStatusGroupEnum group: matchStatusGroupToMatchFeedItems.keySet()){
                request.putFeedItemsInMapByStatusGroup(group,
                        matchesFeedResponse.getHbaseStoreFeedItems());
            }
            
        }
        return request;
    };

    // Handler for async feed request from Redis
    private Func2<MatchFeedRequestContext, LegacyMatchDataFeedDtoWrapper, MatchFeedRequestContext> populateRediesStoreMatchesFeed = (
            request, legacyMatchDataFeedDtoWrapper) -> {

        request.setRedisFeed(legacyMatchDataFeedDtoWrapper.getLegacyMatchDataFeedDto());
        return request;
    };
    protected abstract void performFinalTasksHook(MatchFeedRequestContext requestContext, boolean feedNotFound);

}
