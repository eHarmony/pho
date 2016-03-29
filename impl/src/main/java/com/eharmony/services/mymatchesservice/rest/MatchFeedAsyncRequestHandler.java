package com.eharmony.services.mymatchesservice.rest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.event.MatchQueryEventService;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserComparatorSelector;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserDto;
import com.eharmony.services.mymatchesservice.service.UserMatchesHBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.merger.HBaseRedisFeedMerger;
import com.eharmony.services.mymatchesservice.service.transform.HBASEToLegacyFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MapToMatchedUserDtoTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.eharmony.services.profile.client.ProfileServiceClient;
import com.eharmony.singles.common.util.ResourceNotFoundException;
import com.google.common.collect.Lists;

/**
 * Handles the GetMatches feed async requests.
 * 
 * Feed will be fetched from redis and hbase store in parallel and merges the data based on merge strategy.
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
    private static final String METRICS_HIERARCHY_PREFIX = MatchFeedAsyncRequestHandler.class.getCanonicalName(); 
    private static final String METRICS_GETMATCHUSER_ASYNC = "getSimpleMatchedUserList";
    
    @Resource
    private ExecutorServiceProvider executorServiceProvider;

    @Resource
    private UserMatchesHBaseStoreFeedService userMatchesFeedService;

    @Resource(name = "getMatchesFeedEnricherChain")
    private MatchFeedTransformerChain getMatchesFeedEnricherChain;

    @Resource(name = "getMatchesFeedFilterChain")
    private MatchFeedTransformerChain getMatchesFeedFilterChain;

    @Resource(name= "getTeaserMatchesFeedFilterChain")
    private MatchFeedTransformerChain  getTeaserMatchesFeedFilterChain;

    @Resource
    private MatchStatusGroupResolver matchStatusGroupResolver;

    @Resource
    private HBaseStoreFeedService hbaseStoreFeedService;

    @Resource
    private HBASEToLegacyFeedTransformer hbaseToLegacyFeedTransformer;
    
    @Resource
    private SimpleMatchedUserComparatorSelector simpleMatchedUserComparatorSelector;
    
    @Resource
    private MapToMatchedUserDtoTransformer mapToMatchedUserDtoTransformer;
    
    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;
    
    @Resource
    private MatchQueryEventService matchQueryEventService;
    
    @Resource
    private RedisStoreFeedService redisStoreFeedService;

    @Value("${hbase.fallback.call.timeout:120000}")
    private int hbaseCallbackTimeout;
    
    @Resource
    private HBaseRedisFeedMerger hbaseRedisFeedMerger;
    
    @Resource
    private ProfileServiceClient profileService;

    /**
     * Matches feed will be returned after applying the filters and enriching the data from feed stores. Feed will be
     * fetched from hbase store in parallel and merges the data based on merge strategy.
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

    	Timer.Context t = GraphiteReportingConfiguration.getRegistry().timer(getClass().getCanonicalName() + ".getMatchesFeedAsync").time();
        long userId = matchFeedQueryContext.getUserId();
        Observable<MatchFeedRequestContext> matchQueryRequestObservable = makeMqsRequestObservable(matchFeedQueryContext);

        matchQueryRequestObservable.subscribe(response -> {
            boolean feedNotFound = false;
            try {
                handleFeedResponse(response);
            } catch (ResourceNotFoundException e) {
                feedNotFound = true;
            }

            long duration = t.stop();
            logger.debug("Match feed created for user {}, duration {}", userId, duration);
            ResponseBuilder builder = buildResponse(response, feedNotFound);
            asyncResponse.resume(builder.build());
        }, (throwable) -> {
            long duration = t.stop();
            logger.error("Exception creating match feed for user {}, duration {}", userId, duration, throwable);
            asyncResponse.resume(throwable);
        }, () -> {
            asyncResponse.resume("");
        });
    }
    

    /**
     * Teaser Matches will be returned after applying the filters and enriching the data from feed stores. Feed will be
     * fetched from redis and hbase store in parallel and merges the data based on merge strategy.
     * 
     * This handler uses safe methods, will return valid results as long as at least one of the stores available and
     * respond with feed on time.
     * 
     * @param matchFeedQueryContext
     *            MatchFeedQueryContext
     * @param asyncResponse
     *            AsyncResponse
     * @param eventContextInfo Map
     */

    public void getTeaserMatchesFeed(final MatchFeedQueryContext matchFeedQueryContext, final AsyncResponse asyncResponse, Map<String,String> eventContextInfo) {
   
    	Timer.Context t = GraphiteReportingConfiguration.getRegistry().timer(getClass().getCanonicalName() + ".getMatchesFeedAsyncTeaser").time();
        long userId = matchFeedQueryContext.getUserId();

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = makeMqsRequestObservable(matchFeedQueryContext);

        matchQueryRequestObservable.subscribe(response -> {
            boolean feedNotFound = false;
            try {
                handleTeaserFeedResponse(response);
            } catch (ResourceNotFoundException e) {
                feedNotFound = true;
            }

            long duration = t.stop();
            logger.debug("Match feed created for user {}, duration {}", userId, duration);
            ResponseBuilder builder = buildResponse(response, feedNotFound);
            if (!feedNotFound) {
              matchQueryEventService.sendTeaserMatchShownEvent(response, eventContextInfo);
            																																																																												}
            asyncResponse.resume(builder.build());
        }, (throwable) -> {
            long duration = t.stop();
            logger.error("Exception creating match feed for user {}, duration {}", userId, duration, throwable);
            asyncResponse.resume(throwable);
        }, () -> {
            asyncResponse.resume("");
        });
    }
    
    protected Observable<MatchFeedRequestContext> makeMqsRequestObservable(final MatchFeedQueryContext matchFeedQueryContext) {
        MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        Observable<LegacyMatchDataFeedDtoWrapper> storeFeedObservable = redisStoreFeedService.getUserMatchesSafe(matchFeedQueryContext.getUserId());
        
        matchQueryRequestObservable = matchQueryRequestObservable
                                          .zipWith(storeFeedObservable, populateLegacyMatchesFeed)
                                          .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));

        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable,
                matchFeedQueryContext);
        return matchQueryRequestObservable;
    }
    
    /**
     * Similar to {@link getMatchesFeed}, but take out all the matched users and return them as a list.
     * @param matchFeedQueryContext Query context
     * @param asyncResponse  async RS response.
     * @param sortBy sort by criteria
     */
    public void getSimpleMatchedUserList(final MatchFeedQueryContext matchFeedQueryContext,
            final AsyncResponse asyncResponse, final String sortBy) {

        Timer.Context t = matchQueryMetricsFactroy.getTimerContext(METRICS_HIERARCHY_PREFIX, METRICS_GETMATCHUSER_ASYNC);
        long userId = matchFeedQueryContext.getUserId();
        Observable<MatchFeedRequestContext> matchQueryRequestObservable = makeMqsRequestObservable(
                matchFeedQueryContext);
        Comparator<SimpleMatchedUserDto> sortComparator = simpleMatchedUserComparatorSelector.selectComparator(sortBy);
        /*
         * Function to extract user list from match feed context, make them Observable object 
         */
        Func1<MatchFeedRequestContext, Observable<SimpleMatchedUserDto>> extractUserFunc = new Func1<MatchFeedRequestContext, Observable<SimpleMatchedUserDto>>() {
            @Override
            public Observable<SimpleMatchedUserDto> call(MatchFeedRequestContext context) {
                
                try {
                    handleFeedResponse(context);
                    List<SimpleMatchedUserDto> localResult = context.getLegacyMatchDataFeedDto()
                        .getMatches()
                        .entrySet()
                        .stream()
                        .map(Map.Entry<String, Map<String, Map<String, Object>>>::getValue)
                        .map(mapToMatchedUserDtoTransformer)
                        .collect(Collectors.toList());
                    return Observable.from(localResult);
                } catch(ResourceNotFoundException notFound) {
                    return Observable.empty();
                } catch (Exception exp) {
                    logger.warn("Error while organizing the user list ", exp);
                    //re-throw so subscriber will catch it.
                    throw exp;
                }
            }
        };
        List<SimpleMatchedUserDto> result = Lists.newArrayList();
        matchQueryRequestObservable.flatMap(extractUserFunc).subscribe(response -> {
            result.add(response);
        } , (throwable) -> {
            asyncResponse.resume(throwable);
        } , () -> {
            if (CollectionUtils.isEmpty(result)) {
                ResponseBuilder builder = Response.status(Status.NOT_FOUND);
                asyncResponse.resume(builder.build());
            } else {
                if (sortComparator != null) {
                    result.sort(sortComparator);
                }
                long duration = t.stop();
                logger.debug("Fetching all matched user for user {}, duration {}", userId, duration);
                ResponseBuilder builder = Response.ok().entity(result);
                asyncResponse.resume(builder.build());
                
            }
        });
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
        return matchQueryRequestObservable;
    }

    
    private void handleFeedResponse(MatchFeedRequestContext context) {
        hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeed(context);
        throwExceptionIfFeedIsNotAvailable(context);
        // Merge to obtain final state of matches BEFORE filtering.
       hbaseRedisFeedMerger.merge(context);
        getMatchesFeedFilterChain.execute(context);        
        getMatchesFeedEnricherChain.execute(context);
    }

    //TODO 
    /*
     * 1. create feed response handler service ( interface)
     * 2. Create abstract class with legacy feed transformation, throw except and feed merge and feed enricher methods
     * 3. Override filter chain in specific implementation.
     */
    private void handleTeaserFeedResponse(MatchFeedRequestContext context) {

    	// convert the hbase feed to legacy feed format by using legacy feed transformer and make the hbase feed empty, we
        // need to do this here to honor the pagination
        hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeed(context);
        throwExceptionIfFeedIsNotAvailable(context);
        hbaseRedisFeedMerger.merge(context);
        getTeaserMatchesFeedFilterChain.execute(context);
        getMatchesFeedEnricherChain.execute(context);
    }


    private void throwExceptionIfFeedIsNotAvailable(MatchFeedRequestContext context) {
        if (context.getLegacyMatchDataFeedDtoWrapper() != null
                && context.getLegacyMatchDataFeedDtoWrapper().getMatchesCount() > 0) {
            // Feed is available, no action required
            return;
        }
        if(context.getLegacyMatchDataFeedDtoWrapper() != null && context.getLegacyMatchDataFeedDtoWrapper().getLegacyMatchDataFeedDto() != null) {
            //can be non null but have zero matches
            LegacyMatchDataFeedDto dto = context.getLegacyMatchDataFeedDtoWrapper().getLegacyMatchDataFeedDto();
            if (!MapUtils.isEmpty(dto.getMatches())) {
                return;
            };
        }
        
        throw new ResourceNotFoundException("Feed not available in HBase for user " + context.getUserId());

    }

    private ResponseBuilder buildResponse(MatchFeedRequestContext requestContext, boolean feedNotFound) {
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
        } else if(requestContext.getHbaseFeedItemsByStatusGroup() != null) {
        	ResponseBuilder builder = Response.ok().entity(new LegacyMatchDataFeedDto());
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

    // Handler for async feed request from Redis 
    private Func2<MatchFeedRequestContext, LegacyMatchDataFeedDtoWrapper, MatchFeedRequestContext> populateLegacyMatchesFeed = (
            request, legacyMatchDataFeedDtoWrapper) -> {

        request.setRedisFeed(legacyMatchDataFeedDtoWrapper.getLegacyMatchDataFeedDto());
        
        return request;
    };
	
}
