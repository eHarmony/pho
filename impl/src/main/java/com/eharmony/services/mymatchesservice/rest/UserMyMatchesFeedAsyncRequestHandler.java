package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;

@Component("userMyMatchesFeedAsyncRequestHandler")
public class UserMyMatchesFeedAsyncRequestHandler extends AbstractMatchesFeedAsyncRequestHandler {

    @Resource(name="userMyMatchesFeedResponseHandler")
    private UserMyMatchesFeedResponseHandler userMyMatchesFeedResponseHandler;
    
    @Override
    public void handleFeedResponse(MatchFeedRequestContext context) {
        
        userMyMatchesFeedResponseHandler.processMatchFeedResponse(context);
        
    }

    @Override
    public Timer.Context buildTimerContext() {
        Timer.Context t = GraphiteReportingConfiguration.getRegistry()
                .timer(getClass().getCanonicalName() + ".getMatchesFeedAsync").time();
        return t;
    }

    @Override
    protected void performFinalTasksHook(MatchFeedRequestContext requestContext, boolean feedNotFound) {
        logger.debug("Match feed created for user {}",requestContext.getUserId());
        
    }

}
