package com.eharmony.services.mymatchesservice.rest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.event.MatchQueryEventService;
import com.eharmony.services.mymatchesservice.event.RefreshEventSender;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.BasicStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MatchStatusGroupResolver;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserComparatorSelector;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserDto;
import com.eharmony.services.mymatchesservice.service.UserMatchesHBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyManager;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.service.transform.HBASEToLegacyFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MapToMatchedUserDtoTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedVoldyStore;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.eharmony.services.profile.client.ProfileServiceClient;
import com.eharmony.singles.common.enumeration.Gender;
import com.eharmony.singles.common.profile.BasicPublicProfileDto;
import com.eharmony.singles.common.util.ResourceNotFoundException;
import com.google.common.collect.Lists;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

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
    private static final String METRICS_HIERARCHY_PREFIX = MatchFeedAsyncRequestHandler.class.getCanonicalName(); 
    private static final String METRICS_GETMATCHUSER_ASYNC = "getSimpleMatchedUserList";
    
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

    @Resource(name= "getTeaserMatchesFeedFilterChain")
    private MatchFeedTransformerChain  getTeaserMatchesFeedFilterChain;
    
    @Resource
    private RefreshEventSender refreshEventSender;

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
    private FeedMergeStrategyManager feedMergeStrategyManager;
    
    @Value("${redis.merge.enabled:false}")
    private boolean redisMergeMode;
    
    @Resource
    private ProfileServiceClient profileService;

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

    public void getTeaserMatchesFeed(final MatchFeedQueryContext matchFeedQueryContext, final AsyncResponse asyncResponse, Map<String,String> eventContextInfo) {

    	Timer.Context t = GraphiteReportingConfiguration.getRegistry().timer(getClass().getCanonicalName() + ".getMatchesFeedAsyncTeaser").time();
        long userId = matchFeedQueryContext.getUserId();
        MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
        request.setFeedMergeType(FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE);

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        matchQueryRequestObservable = matchQueryRequestObservable.zipWith(
                voldemortStore.getMatchesObservableSafe(matchFeedQueryContext), populateLegacyMatchesFeedFromVoldy).subscribeOn(
                Schedulers.from(executorServiceProvider.getTaskExecutor()));

        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable,
                matchFeedQueryContext, FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE, false);

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
        FeedMergeStrategyType mergeType;
        if (redisMergeMode) {
            mergeType = FeedMergeStrategyType.HBASE_FEED_WITH_MATCH_MERGE;
        } else {
            mergeType = FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE;
        }
        request.setFeedMergeType(mergeType);
        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        Observable<LegacyMatchDataFeedDtoWrapper> storeFeedObservable = null;
        
        if (redisMergeMode) {
            BasicStoreFeedRequestContext basicRequest = new BasicStoreFeedRequestContext(matchFeedQueryContext);
            Observable<BasicPublicProfileDto> profileObservable = Observable.defer(
                ()->
                    Observable.just(profileService.findBasicPublicProfileForUser((int) matchFeedQueryContext.getUserId()))
            );
            storeFeedObservable = redisStoreFeedService.getUserMatchesSafe(basicRequest)
                                                       .zipWith(profileObservable, appendUserLocaleGender);
        } else {
            storeFeedObservable = voldemortStore.getMatchesObservableSafe(matchFeedQueryContext);
        }
        matchQueryRequestObservable = matchQueryRequestObservable
                                          .zipWith(storeFeedObservable, populateLegacyMatchesFeed)
                                          .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));

        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable,
                matchFeedQueryContext, mergeType, false);
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

    private void populateContextWithHBaseMatchesOnVoldeError(MatchFeedRequestContext request) {

        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFromHBaseOnVoldeError").time();
        long startTime = System.currentTimeMillis();
        // Should we use meter
        MatchFeedQueryContext queryContext = request.getMatchFeedQueryContext();
        long userId = queryContext.getUserId();
        request.setFallbackRequest(true);
        request.setFeedMergeType(FeedMergeStrategyType.HBASE_FEED_ONLY);

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);

        matchQueryRequestObservable = chainHBaseFeedRequestsByStatus(matchQueryRequestObservable, queryContext, null,
                request.isFallbackRequest());

        try {
            matchQueryRequestObservable.timeout(hbaseCallbackTimeout, TimeUnit.MILLISECONDS).toBlocking().first();
        } catch (Throwable ex) {
            long endTime = System.currentTimeMillis();
            logger.error("Exception while fetching feed from HBase fallback. user {}, duration {}", userId,
                    (endTime - startTime), ex);
        } finally {
            t.stop();
            long endTime = System.currentTimeMillis();
            logger.debug("Fetched feed from HBase for fallback. user {}, duration {}", userId, (endTime - startTime));
        }
        logger.debug("Returning the context after hbase fallback call...");

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

        for (Entry<MatchStatusGroupEnum, Set<MatchStatusEnum>> entry : requestedMatchStatusGroups.entrySet()) {
            logger.debug("create observable to fetch matches for group {} and user {}", entry.getKey(),
                    matchFeedQueryContext.getUserId());
            HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(matchFeedQueryContext);
            requestContext.setFallbackRequest(isFallbackRequest);
            requestContext.setFeedMergeType(feedMergeType);
            requestContext.setMatchStatuses(entry.getValue());
            requestContext.setMatchStatusGroup(entry.getKey());
            matchQueryRequestObservable = matchQueryRequestObservable.zipWith(
                    hbaseStoreFeedService.getUserMatchesByStatusGroupSafe(requestContext), populateHBaseMatchesFeed)
                    .subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
        }
        return matchQueryRequestObservable;
    }

    
    private void handleFeedResponse(MatchFeedRequestContext context) {
        if (redisMergeMode) {
        
            hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeed(context);
        
        } else {
            refreshEventSender.sendRefreshEvent(context);
            executeFallbackIfRequired(context);
            // convert the hbase feed to voldy feed by using legacy feed transformer and make the hbase feed empty, we
            // need to do this here to honor the pagination
            hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeedIfRequired(context);
        }
        
        throwExceptionIfFeedIsNotAvailable(context);

        getMatchesFeedFilterChain.execute(context);
        
        feedMergeStrategyManager.getMergeStrategy(context).merge(context);
        
        getMatchesFeedEnricherChain.execute(context);
    }

    private void handleTeaserFeedResponse(MatchFeedRequestContext context) {
        executeFallbackIfRequired(context);
        // convert the hbase feed to voldy feed by using legacy feed transformer and make the hbase feed empty, we
        // need to do this here to honor the pagination
        hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeedIfRequired(context);
        
        throwExceptionIfFeedIsNotAvailable(context);
        
        feedMergeStrategyManager.getMergeStrategy(context).merge(context);
		
        getTeaserMatchesFeedFilterChain.execute(context);
        
        getMatchesFeedEnricherChain.execute(context);
    }


    private void throwExceptionIfFeedIsNotAvailable(MatchFeedRequestContext context) {
        if (context.getLegacyMatchDataFeedDtoWrapper() != null
                && context.getLegacyMatchDataFeedDtoWrapper().getVoldyMatchesCount() > 0) {
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
        
        throw new ResourceNotFoundException("Feed not available in voldy and HBase for user " + context.getUserId());

    }

    private void executeFallbackIfRequired(MatchFeedRequestContext response) {
        if (shouldFallbackToHBase(response)) {
            response.setFallbackRequest(true);
            populateContextWithHBaseMatchesOnVoldeError(response);
            // aggregateHBaseFeedItems(response);
        } else {
            response.setFallbackRequest(false);
        }
    }

    // Unit test please
    protected boolean shouldFallbackToHBase(MatchFeedRequestContext response) {
        LegacyMatchDataFeedDtoWrapper legacyFeedWrapper = response.getLegacyMatchDataFeedDtoWrapper();
        if (legacyFeedWrapper == null) {
            logger.warn("legacyFeedWrapper must not be null for user {}", response.getUserId());
            if (response.hasHbaseMatches()) {
                logger.warn("legacyFeedWrapper is null for user {} and falling back to hbase", response.getUserId());
                return true;
            }

            return false;
        }
        if (legacyFeedWrapper.getLegacyMatchDataFeedDto() != null
                && MapUtils.isNotEmpty(legacyFeedWrapper.getLegacyMatchDataFeedDto().getMatches())) {
            return false;
        }

        // Voldemort feed is empty but there are matches in Hbase
        if (response.hasHbaseMatches()) {
            logger.info(
                    "There are no matches in voldemrt, but matches exist in HBase for user {} falling back on hbase",
                    response.getUserId());
            return true;
        }

        logger.debug("There are no records in HBase and Voldemort for user {}", response.getUserId());
        return false;
    }

    private ResponseBuilder buildResponse(MatchFeedRequestContext requestContext, boolean feedNotFound) {
        if (feedNotFound) {

            ResponseBuilder builder = Response.status(Status.NOT_FOUND);
            
            return builder;
        }
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

    // Handler for async feed request from Redis or Voldy
    private Func2<MatchFeedRequestContext, LegacyMatchDataFeedDtoWrapper, MatchFeedRequestContext> populateLegacyMatchesFeed = (
            request, legacyMatchDataFeedDtoWrapper) -> {

        logger.debug("Voldemort State flag = {}", request.getMatchFeedQueryContext().getVoldyState());
        if (redisMergeMode) {
            request.setRedisFeed(legacyMatchDataFeedDtoWrapper.getLegacyMatchDataFeedDto());
        } else {
            request.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
        }
        return request;
    };
	
    // Handler for async feed request from Voldy only. 
    private Func2<MatchFeedRequestContext, LegacyMatchDataFeedDtoWrapper, MatchFeedRequestContext> populateLegacyMatchesFeedFromVoldy = (
            request, legacyMatchDataFeedDtoWrapper) -> {

        logger.debug("Voldemort State flag = {}", request.getMatchFeedQueryContext().getVoldyState());
        
        request.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);

        return request;
    };
    
    private Func2<LegacyMatchDataFeedDtoWrapper,BasicPublicProfileDto, LegacyMatchDataFeedDtoWrapper>  appendUserLocaleGender = (request, profileDto) ->{
        LegacyMatchDataFeedDto feedDto = request.getLegacyMatchDataFeedDto();
        if (profileDto == null) {
            return request;
        }
        //Redis doesn't have the feed, create a dummy feed to carry locale and gender.
        if (feedDto == null) {
             feedDto = new LegacyMatchDataFeedDto();
             request.setLegacyMatchDataFeedDto(feedDto);
        }
        feedDto.setLocale(profileDto.getLocale());
        String genderStr = Gender.fromInt(profileDto.getGender()).toString();
        feedDto.setGender(genderStr);
        return request;
    };
}
