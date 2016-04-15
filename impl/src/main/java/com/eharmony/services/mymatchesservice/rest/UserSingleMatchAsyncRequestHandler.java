	package com.eharmony.services.mymatchesservice.rest;
	
	import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.MRSAdapter;
import com.eharmony.services.mymatchesservice.service.MRSDto;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.merger.MRSAndSORAMatchMerger;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedSORAStore;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;
import com.eharmony.singles.common.util.ResourceNotFoundException;
	
	@Component("userSingleMatchAsyncRequestHandler")
	public class UserSingleMatchAsyncRequestHandler{
	
	    protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	    @Resource
	    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;
	    
	    @Resource
	    private RedisStoreFeedService redisStoreFeedService;
	    
	    @Resource
	    private HBaseStoreFeedService hbaseStoreFeedService;
	    
	    @Resource(name="mrsAdapter")
	    private MRSAdapter mrsAdapter;
	    
	    @Resource(name="soraStore")
	    private MatchDataFeedSORAStore soraStore;
	    
	    @Resource(name="userMyMatchesFeedResponseHandler")
	    private UserMyMatchesFeedResponseHandler userMyMatchesFeedResponseHandler;
	    
	    @Resource
	    private ExecutorServiceProvider executorServiceProvider;
	    
	    @Resource(name="mrsAndSoraMatchMerger")
	    private MRSAndSORAMatchMerger mrsAndSORAMatchMerger;
	    
	    @Resource(name = "getSingleMatchEnricherChain")
	    private MatchFeedTransformerChain getSingleMatchEnricherChain;

	    @Resource(name = "getMatchesFeedEnricherChain")
	    private MatchFeedTransformerChain getMatchesFeedEnricherChain;

	    @Resource(name = "getMatchesFeedFilterChain")
	    private MatchFeedTransformerChain getMatchesFeedFilterChain;
	    
	    
	    private static final String METRICS_HIERARCHY_PREFIX = "com.eharmony.services.mymatchesservice.rest.MatchFeedAsyncRequestHandler";
	    private static final String METRICS_GETSINGLEMATCH_ASYNC = "getSingleMatch";
	    
    	private boolean __INTERNAL_NOTFORPRODUCTION_HBASE_ENABLED__ = true;
	    
		protected Context buildTimerContext() {
			
	        Timer.Context t = matchQueryMetricsFactroy
	                .getTimerContext(METRICS_HIERARCHY_PREFIX, METRICS_GETSINGLEMATCH_ASYNC);
	        return t;
		}
	
		public void handleFeedResponse(MatchFeedRequestContext context) {

			userMyMatchesFeedResponseHandler.processMatchFeedResponse(context);
		}
		
	    public void getSingleMatch(final MatchFeedQueryContext singleMatchQueryContext, final AsyncResponse asyncResponse) {
	   	 	    	
	    	Timer.Context t = GraphiteReportingConfiguration.getRegistry().timer(getClass().getCanonicalName() + ".getSingleMatchAsync").time();
	        long userId = singleMatchQueryContext.getUserId();
	    	long matchId = singleMatchQueryContext.getMatchId();
	        Observable<MatchFeedRequestContext> singleMatchQueryRequestObservable = makeSingleMatchRequestObservable(singleMatchQueryContext);
	
	        singleMatchQueryRequestObservable.subscribe(response -> {
	        	
	            boolean feedNotFound = false;
	            try {            	
	            	// proceed with merge/filter/enrich processing.
	            	handleFeedResponse(response);        	
	            	
	            } catch (ResourceNotFoundException e) {
	                feedNotFound = true;
	            }
	            
	        	if(isDataAvailable(response, userId, matchId) && __INTERNAL_NOTFORPRODUCTION_HBASE_ENABLED__){
	        		
	                long duration = t.stop();
	                logger.debug("Single match feed created for user {}, duration {}", userId, duration);
	                ResponseBuilder builder = buildResponse(response, feedNotFound);
	                asyncResponse.resume(builder.build());        		
	       		
	        	}else{
	            	
		        	logger.info("Match {} not found for user {}, searching SORA + MRS.", matchId, userId);
		        	
		        	Observable<MatchFeedRequestContext> fallbackObservable = 
		        							makeSingleMatchFallbackRequestObservable(singleMatchQueryContext);
		        	fallbackObservable.subscribe(response2 -> {
		                boolean backupFeedNotFound = false;
		        		try{
		                	// Do profile enrichment...
		        			getSingleMatchEnricherChain.execute(response2);
		        			
		        			// Then proceed with usual filtering/enrichment...
		        			getMatchesFeedEnricherChain.execute(response2);

		        			getMatchesFeedFilterChain.execute(response2);
		        			
		    	        } catch (ResourceNotFoundException e) {
		    	        	backupFeedNotFound = true;
		    	        }
		
		                logger.debug("Single match feed created for user {}, matchId {}", userId, matchId);
		                ResponseBuilder builder = buildBackupResponse(response2, backupFeedNotFound);
		                asyncResponse.resume(builder.build());
		
		            }, (throwable2) -> {
		                logger.error("Exception creating single match feed for user {}, matchId {} : {}", userId, matchId, throwable2);
		                asyncResponse.resume(throwable2);
		            }, () -> {
		            	logger.warn("match {} not found in SORA or MRS, this match is invalid!", matchId);
		                asyncResponse.resume("");
		            });   	
	        	}
	
	        }, (throwable) -> {
	        	
	            long duration = t.stop();
	            logger.error("Exception creating single match feed for user {}, duration {}", userId, duration, throwable);
	            asyncResponse.resume(throwable);
	            
	        }, () -> {
	        	asyncResponse.resume("");
	        });
	    }
	
	    protected Observable<MatchFeedRequestContext> makeSingleMatchRequestObservable(
				final MatchFeedQueryContext matchFeedQueryContext) {
	
			logger.info("Getting feed for userId {} matchId {}", matchFeedQueryContext.getUserId(), 
							matchFeedQueryContext.getMatchId());
			
			MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);        
			
			Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
			
			// prep Redis...
			Observable<LegacyMatchDataFeedDtoWrapper> redisStoreFeedObservable = 
			redisStoreFeedService.getUserMatchesSafe(matchFeedQueryContext.getUserId());
			
			// prep Hbase...
			HBaseStoreFeedRequestContext requestContext = new HBaseStoreFeedRequestContext(matchFeedQueryContext);
			
			matchQueryRequestObservable = matchQueryRequestObservable
			.zipWith(redisStoreFeedObservable, populateLegacyMatchesFeed)
			.zipWith(hbaseStoreFeedService.getUserMatchSafe(requestContext), populateHBaseMatchesFeed)
			.subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
			
			return matchQueryRequestObservable;
	    }
	    
	    protected Observable<MatchFeedRequestContext> makeSingleMatchFallbackRequestObservable(
				final MatchFeedQueryContext singleMatchQueryContext) {
	
	    	long userId = singleMatchQueryContext.getUserId();
	    	long matchId = singleMatchQueryContext.getMatchId();
	    	
			logger.info("Getting fallback feed for userId {} matchId {}", singleMatchQueryContext.getUserId(), 
							matchId);
				
			MatchFeedRequestContext request = new MatchFeedRequestContext(singleMatchQueryContext);        
	    	
			Observable<MatchDo> match = Observable.just(soraStore.getMatch(matchId));
			Observable<MatchSummaryDo> matchSummary = Observable.just(soraStore.getMatchSummary(userId, matchId));
			Observable<MRSDto> mrsDto = Observable.just(mrsAdapter.getMatch(matchId));
			
			return Observable.zip(match, matchSummary, mrsDto, (m, s, mrs) -> {
				
				mrsAndSORAMatchMerger.mergeMatch(request, mrs, m, s);
				return request;
			});
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
	
	    protected boolean isDataAvailable(MatchFeedRequestContext context, long userId, long matchId){
	    	
	    	return (context.getAggregateHBaseFeedItems().size() > 0 ||
	    		(context.getRedisFeed() != null && context.getRedisFeed().getMatches().size() > 0) ||
	    		(context.getLegacyMatchDataFeedDto() != null && context.getLegacyMatchDataFeedDto().getTotalMatches() > 0));    
	    }
		
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
	    
	    protected ResponseBuilder buildBackupResponse(MatchFeedRequestContext requestContext, boolean feedNotFound) {
	        if (feedNotFound) {
	            // just logging it here for any action to be taken if the need be.
	            // an empty feed will be returned for such users.
	            logger.info("Match not available for userId: {}", requestContext.getMatchFeedQueryContext().getUserId());
	        }
	        
	        LegacyMatchDataFeedDto singleMatch = requestContext.getLegacyMatchDataFeedDto();
	        if (singleMatch != null) {
	            ResponseBuilder builder = Response.ok().entity(singleMatch);
	            builder.status(Status.OK);
	            return builder;
	        } else {
	            ResponseBuilder builder = Response.serverError().status(Status.INTERNAL_SERVER_ERROR);
	            return builder;
	        }
	    }
	
	}
