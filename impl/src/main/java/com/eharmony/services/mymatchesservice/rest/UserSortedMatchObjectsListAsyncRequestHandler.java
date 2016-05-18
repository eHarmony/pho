package com.eharmony.services.mymatchesservice.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Timer;
import com.eharmony.services.mymatchesservice.monitoring.MatchQueryMetricsFactroy;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserComparatorSelector;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserDto;
import com.eharmony.services.mymatchesservice.service.transform.MapToMatchedUserDtoTransformer;

@Component("userSortedMatchObjectsListAsyncRequestHandler")
public class UserSortedMatchObjectsListAsyncRequestHandler extends AbstractMatchesFeedAsyncRequestHandler {

    @Resource(name = "userMyMatchesFeedResponseHandler")
    private UserMyMatchesFeedResponseHandler userMyMatchesFeedResponseHandler;

    @Resource
    private MatchQueryMetricsFactroy matchQueryMetricsFactroy;

    @Resource
    private SimpleMatchedUserComparatorSelector simpleMatchedUserComparatorSelector;

    @Resource
    private MapToMatchedUserDtoTransformer mapToMatchedUserDtoTransformer;

    private static final String METRICS_HIERARCHY_PREFIX = "com.eharmony.services.mymatchesservice.rest.MatchFeedAsyncRequestHandler";
    private static final String METRICS_GETMATCHUSER_ASYNC = "getSimpleMatchedUserList";

    @Override
    public void handleFeedResponse(MatchFeedRequestContext context) {
        userMyMatchesFeedResponseHandler.processMatchFeedResponse(context);

    }

    @Override
    public Timer.Context buildTimerContext() {
        Timer.Context t = matchQueryMetricsFactroy
                .getTimerContext(METRICS_HIERARCHY_PREFIX, METRICS_GETMATCHUSER_ASYNC);
        return t;
    }

    @Override
    protected void performFinalTasksHook(MatchFeedRequestContext requestContext, boolean feedNotFound) {
        logger.debug("Match feed created for sorted matches list request for user {}", requestContext.getUserId());

    }

    @Override
    protected ResponseBuilder buildResponse(MatchFeedRequestContext requestContext, boolean feedNotFound) {
        if (feedNotFound) {
            // just logging it here for any action to be taken if the need be.
            // an empty feed will be returned for such users.
            logger.info("Feed not available for userId: {}", requestContext.getUserId());
        }
        Comparator<SimpleMatchedUserDto> sortComparator = simpleMatchedUserComparatorSelector
                .selectComparator(requestContext.getMatchFeedQueryContext().getOrderings());
        List<SimpleMatchedUserDto> localResult = new ArrayList<SimpleMatchedUserDto>();

        if (requestContext.getLegacyMatchDataFeedDto() != null) {
            localResult = requestContext.getLegacyMatchDataFeedDto().getMatches().entrySet().stream()
                    .map(Map.Entry<String, Map<String, Map<String, Object>>>::getValue)
                    .map(mapToMatchedUserDtoTransformer).collect(Collectors.toList());

        }

        if (CollectionUtils.isNotEmpty(localResult) &&  sortComparator != null) {
            localResult.sort(sortComparator);
        }
        
        ResponseBuilder builder = Response.ok().entity(localResult);
        builder.status(Status.OK);
        return builder;

    }

}
