package com.eharmony.services.mymatchesservice.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.event.CommandEvent;
import com.eharmony.event.Event;
import com.eharmony.event.EventSender;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.google.common.collect.ImmutableList;

@Component
public class MatchQueryEventService {
    private Logger log = LoggerFactory.getLogger(MatchQueryEventService.class);

    public static final String PRODUCER = "MQS";

    private static final String LOCALE = "locale";
    private static final String USER_ID = "userId";
    private static final String MATCH_COUNT = "matchCount";
    private static final String MATCH_ID_LIST = "matchIdList";
    private static final String TEASER_MATCH_EVENT_CATEGORY = "user.teaser.match.shown";

    @Value("${instance}")
    private String instance;

    @Resource(name = "eventSender")
    private EventSender eventSender;

    /**
     * Send event when the user fetched teaser matches.
     * 
     * @param matchesFeedContext
     */
    public void sendTeaserMatchShownEvent(final MatchFeedRequestContext matchesFeedContext) {
        long userId = matchesFeedContext.getUserId();
        String userIdStr = String.valueOf(userId);

        Map<String, String> context = extractTeaserEventContext(matchesFeedContext, userIdStr);
        Event teaserMatchShownEven = new CommandEvent.Builder().setCategory(TEASER_MATCH_EVENT_CATEGORY)
                .setProducer(PRODUCER).setInstance(instance).setContext(context).setFrom(userIdStr).setUid(userIdStr)
                .build();

        log.info("sending {} event for user {}", TEASER_MATCH_EVENT_CATEGORY, userId);
        eventSender.send(teaserMatchShownEven);
    }

    private Map<String, String> extractTeaserEventContext(final MatchFeedRequestContext matchesFeedContext,
            final String userIdStr) {
        Map<String, String> context = new HashMap<String, String>();
        String locale = matchesFeedContext.getMatchFeedQueryContext().getLocale();
        context.put(LOCALE, locale);
        context.put(USER_ID, userIdStr);
        try {
            String matchCount = matchesFeedContext.getLegacyMatchDataFeedDto().getTotalMatches().toString();

            context.put(MATCH_COUNT, matchCount);
            List<String> matchIdList = ImmutableList
                .copyOf(matchesFeedContext.getLegacyMatchDataFeedDto().getMatches().keySet());
            context.put(MATCH_ID_LIST, matchIdList.toString());
        } catch (Exception exp) {
            log.warn("Error while extract teaser event context: userId[{}]", userIdStr, exp);
        }
        return context;
    }
}
