package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

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
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.service.ExecutorServiceProvider;
import com.eharmony.services.mymatchesservice.service.UserMatchesFeedService;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategy;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

@Component
public class MatchFeedAsyncRequestHanlder {

    private static final Logger logger = LoggerFactory.getLogger(MatchFeedAsyncRequestHanlder.class);

    @Resource
    private ExecutorServiceProvider executorServiceProvider;

    @Resource
    private UserMatchesFeedService userMatchesFeedService;

    @Resource
    private MatchDataFeedStore voldemortStore;

    @Resource
    private FeedMergeStrategy<LegacyMatchDataFeedDto> feedMergeStrategy;

    public void getMatchesFeed(final long userId, final AsyncResponse asyncResponse) {

        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFeedAsync").time();
        MatchFeedQueryContext matchFeedQueryContext = MatchFeedQueryContextBuilder.newInstance().setUserId(userId).build();
                
        MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
        request.setFeedMergeType(FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE);

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        matchQueryRequestObservable
                .zipWith(userMatchesFeedService.getUserMatchesFromStoreObservable(request), populateMathesFeed)
                .observeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()))
                .zipWith(voldemortStore.getMatchesObservable(userId), populateLegacyMathesFeed)
                .observeOn(Schedulers.from(executorServiceProvider.getTaskExecutor())).subscribe(response -> {
                    feedMergeStrategy.merge(response);
                    long duration = t.stop();
                    logger.debug("Match feed created, duration {}", duration);
                    ResponseBuilder builder = buildResponse(response);
                    asyncResponse.resume(builder.build());
                }, (throwable) -> {
                    long duration = t.stop();
                    logger.error("Exception creating match feed, duration {}", duration, throwable);
                    asyncResponse.resume(throwable);
                }, () -> {
                    asyncResponse.resume("");
                });
    }
    
    public void getMatchesFeed(final MatchFeedQueryContext matchFeedQueryContext, final AsyncResponse asyncResponse) {

        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFeedAsync").time();

        MatchFeedRequestContext request = new MatchFeedRequestContext(matchFeedQueryContext);
        request.setFeedMergeType(FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE);

        Observable<MatchFeedRequestContext> matchQueryRequestObservable = Observable.just(request);
        matchQueryRequestObservable
                .zipWith(userMatchesFeedService.getUserMatchesFromStoreObservable(request), populateMathesFeed)
                .observeOn(Schedulers.from(executorServiceProvider.getTaskExecutor()))
                .zipWith(voldemortStore.getMatchesObservable(request.getUserId()), populateLegacyMathesFeed)
                .observeOn(Schedulers.from(executorServiceProvider.getTaskExecutor())).subscribe(response -> {
                    feedMergeStrategy.merge(response);
                    //TODO call filter service
                    //TODO call enricher service
                    long duration = t.stop();
                    logger.debug("Match feed created, duration {}", duration);
                    ResponseBuilder builder = buildResponse(response);
                    asyncResponse.resume(builder.build());
                }, (throwable) -> {
                    long duration = t.stop();
                    logger.error("Exception creating match feed, duration {}", duration, throwable);
                    asyncResponse.resume(throwable);
                }, () -> {
                    asyncResponse.resume("");
                });
    }

    private ResponseBuilder buildResponse(MatchFeedRequestContext requestContext) {
        ResponseBuilder builder = Response.ok().entity(requestContext.getLegacyMatchDataFeedDto());
        builder.status(Status.OK);
        return builder;
    }

    private Func2<MatchFeedRequestContext, Set<MatchDataFeedItemDto>, MatchFeedRequestContext> populateMathesFeed = (
            request, matchesFed) -> {
        request.setNewStoreFeed(matchesFed);
        return request;
    };

    private Func2<MatchFeedRequestContext, LegacyMatchDataFeedDto, MatchFeedRequestContext> populateLegacyMathesFeed = (
            request, legacyMatchDataFeed) -> {
        request.setLegacyMatchDataFeedDto(legacyMatchDataFeed);
        return request;
    };

}
