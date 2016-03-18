package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.event.MatchQueryEventService;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;

@Component("userTeaserMatchesFeedAsyncRequestHandler")
public class UserTeaserMatchesFeedAsyncRequestHandler extends AbstractMatchesFeedAsyncRequestHandler {

    @Resource(name = "userTeaserMatchesFeedResponseHandler")
    private UserTeaserMatchesFeedResponseHandler userTeaserMatchesFeedResponseHandler;

    @Resource
    private MatchQueryEventService matchQueryEventService;

    @Override
    public void handleFeedResponse(MatchFeedRequestContext context) {
        userTeaserMatchesFeedResponseHandler.processMatchFeedResponse(context);

    }

    @Override
    public Timer.Context buildTimerContext() {
        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFeedAsyncTeaser").time();
        return t;
    }

    protected void performFinalTasksHook(MatchFeedRequestContext requestContext, boolean feedNotFound) {
        logger.debug("Match feed created for teaser user {}", requestContext.getUserId());
        if (!feedNotFound) {
            matchQueryEventService.sendTeaserMatchShownEvent(requestContext, requestContext.getMatchFeedQueryContext()
                    .getRequestMetadata());
        }
    }

}
