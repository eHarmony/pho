	package com.eharmony.services.mymatchesservice.rest;
	
	import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.HBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.HBaseStoreSingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.HBaseStoreSingleMatchResponse;
import com.eharmony.services.mymatchesservice.service.MRSAdapter;
import com.eharmony.services.mymatchesservice.service.MRSDto;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.transform.SingleMatchTransformerChain;
import com.eharmony.services.mymatchesservice.service.transform.filter.SingleMatchFilterChain;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedSORAStore;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;
import com.eharmony.services.profile.client.ProfileServiceClient;
import com.eharmony.singles.common.profile.BasicPublicProfileDto;
	
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
	    
	    @Resource 
	    private ProfileServiceClient profileService;
	    
	    @Resource(name="soraStore")
	    private MatchDataFeedSORAStore soraStore;
	    
	    @Resource(name="singleMatchResponseHandler")
	    private SingleMatchResponseHandler singleMatchResponseHandler;
	    
	    @Resource
	    private ExecutorServiceProvider executorServiceProvider;
	    	    
	    @Resource(name="singleMatchTransformerChain")
	    private SingleMatchTransformerChain singleMatchTransformerChain;	    

	    @Resource(name="singleMatchFilterChain")
	    private SingleMatchFilterChain singleMatchFilterChain;
	    
	    private static final String METRICS_HIERARCHY_PREFIX = "com.eharmony.services.mymatchesservice.rest.MatchFeedAsyncRequestHandler";
	    private static final String METRICS_GETSINGLEMATCH_ASYNC = "getSingleMatch";
	    
		protected Context buildTimerContext() {
			
	        Timer.Context t = matchQueryMetricsFactroy
	                .getTimerContext(METRICS_HIERARCHY_PREFIX, METRICS_GETSINGLEMATCH_ASYNC);
	        return t;
		}
		
	    public void getSingleMatch(final SingleMatchQueryContext singleMatchQueryContext, final AsyncResponse asyncResponse) {
	   	 	    	
	    	Timer.Context t = GraphiteReportingConfiguration.getRegistry().timer(getClass().getCanonicalName() + ".getSingleMatchAsync").time();
	        long userId = singleMatchQueryContext.getUserId();
	    	long matchId = singleMatchQueryContext.getMatchId();
	        Observable<SingleMatchRequestContext> singleMatchQueryRequestObservable = makeSingleMatchRequestObservable(singleMatchQueryContext);
	
	        singleMatchQueryRequestObservable.subscribe(response -> {
	        	            
	        	singleMatchResponseHandler.processMatchFromHBaseAndRedis(response);
	        	
	        	if(response.matchIsAvailable() && singleMatchQueryContext.isHBaseRedisEnabled()){
	        		
	        		singleMatchTransformerChain.execute(response);
	        		singleMatchFilterChain.execute(response);
	        		
	                long duration = t.stop();
	                logger.debug("Single match created for user {}, duration {}", userId, duration);
	                ResponseBuilder builder = buildResponse(response);
	                asyncResponse.resume(builder.build());        		
	       		
	        	}else{
	            	
		        	logger.debug("Match {} not found for user {}, searching SORA + MRS.", matchId, userId);
		        	
		        	Observable<SingleMatchRequestContext> fallbackObservable = 
		        							makeSingleMatchFallbackRequestObservable(singleMatchQueryContext);
		        	fallbackObservable.subscribe(response2 -> {
		        		
		        		singleMatchResponseHandler.processMatchFromMRSAndSORA(response2);
		        		if(response2.matchIsAvailable()){
		
		        			logger.debug("Single match found for user {}, matchId {}", userId, matchId);
			        		singleMatchTransformerChain.execute(response2);
			        		singleMatchFilterChain.execute(response2);
		        		}
		        		
		                long duration = t.stop();
		                logger.debug("Single match for user {}, duration {}", userId, duration);
		                ResponseBuilder builder = buildResponse(response2);
		                asyncResponse.resume(builder.build());
		
		            }, (throwable2) -> {
		                logger.error("Exception fetching fallback single match for user {}, matchId {} : {}", userId, matchId, throwable2);
		                asyncResponse.resume(throwable2);
		            }, () -> {
		                asyncResponse.resume("");
		            });   	
	        	}
	
	        }, (throwable) -> {
	        	
	            long duration = t.stop();
	            logger.error("Exception fetching single match for user {}, duration {}", userId, duration, throwable);
	            asyncResponse.resume(throwable);
	            
	        }, () -> {
	        	asyncResponse.resume("");
	        });
	    }
	
	    protected Observable<SingleMatchRequestContext> makeSingleMatchRequestObservable(
				final SingleMatchQueryContext singleMatchQueryContext) {
	
			logger.debug("Getting match for userId {} matchId {}", singleMatchQueryContext.getUserId(), 
							singleMatchQueryContext.getMatchId());
			
			SingleMatchRequestContext request = new SingleMatchRequestContext(singleMatchQueryContext);        
			
			Observable<SingleMatchRequestContext> matchQueryRequestObservable = Observable.just(request);
			
			// prep Redis...
			Observable<LegacyMatchDataFeedDtoWrapper> redisStoreFeedObservable = 
					redisStoreFeedService.getUserMatchesSafe(singleMatchQueryContext.getUserId());
			
			// prep Hbase...
			HBaseStoreSingleMatchRequestContext hbaseRequestContext = new HBaseStoreSingleMatchRequestContext(
																		singleMatchQueryContext.getUserId(), 
																		singleMatchQueryContext.getMatchId());
			
			matchQueryRequestObservable = matchQueryRequestObservable
			.zipWith(redisStoreFeedObservable, populateRedisSingleMatch)
			.zipWith(hbaseStoreFeedService.getUserMatchSafe(hbaseRequestContext), populateHBaseSingleMatch)
			.subscribeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()));
			
			return matchQueryRequestObservable;
	    }
	    
	    protected Observable<SingleMatchRequestContext> makeSingleMatchFallbackRequestObservable(
				final SingleMatchQueryContext singleMatchQueryContext) {
	
	    	long userId = singleMatchQueryContext.getUserId();
	    	long matchId = singleMatchQueryContext.getMatchId();
	    	
			logger.debug("Getting fallback match for userId {} matchId {}", singleMatchQueryContext.getUserId(), 
							matchId);
				
			SingleMatchRequestContext request = new SingleMatchRequestContext(singleMatchQueryContext);        
	    	
			Observable<MatchDo> match = Observable.just(soraStore.getMatch(userId, matchId));
			Observable<MatchSummaryDo> matchSummary = Observable.just(soraStore.getMatchSummary(userId, matchId));
			Observable<MRSDto> mrsDto = Observable.just(mrsAdapter.getMatch(userId, matchId));
			Observable<BasicPublicProfileDto> profileDto = Observable.just(profileService.findBasicPublicProfileForUser((int)userId));
			
			return Observable.zip(match, matchSummary, mrsDto, profileDto, (m, s, mrs, p) -> {
				
				request.setMatchDo(m);
				request.setMatchSummaryDo(s);
				request.setMrsDto(mrs);
				request.setPublicProfileDto(p);
				
				return request;
			});
	    }
	    

	    private Func2<SingleMatchRequestContext, HBaseStoreSingleMatchResponse, SingleMatchRequestContext> populateHBaseSingleMatch = (
	            request, matchResponse) -> {
	        
	        if(matchResponse.isDataAvailable()){
	        	request.setHbaseMatch(matchResponse.getHbaseStoreFeedItem());
	        }
	        return request;
	    };
	    
	    // Handler for async feed request from Redis
	    private Func2<SingleMatchRequestContext, LegacyMatchDataFeedDtoWrapper, SingleMatchRequestContext> populateRedisSingleMatch = (
	            request, legacyMatchDataFeedDtoWrapper) -> {
	            	
	        String matchId = Long.toString(request.getQueryContext().getMatchId());
	        
	        LegacyMatchDataFeedDto redisFeed =  legacyMatchDataFeedDtoWrapper.getLegacyMatchDataFeedDto();
	        if(redisFeed != null && redisFeed.getMatches() != null){
	        	request.setRedisMatch(redisFeed.getMatches().get(matchId)); 
	        }
	        return request;
	    };


	    protected ResponseBuilder buildResponse(SingleMatchRequestContext requestContext) {

	        Map<String, Map<String, Object>> oneMatch = requestContext.getSingleMatch();
	        
	        if (oneMatch != null) {
	            ResponseBuilder builder = Response.ok().entity(oneMatch);
	            builder.status(Status.OK);
	            return builder;
	        } else {
	            ResponseBuilder builder = Response.serverError().status(Status.NOT_FOUND);
	            return builder;
	        }
	    }	
	}
