package com.eharmony.services.mymatchesservice.event;

import java.util.HashMap;
import static com.eharmony.services.mymatchesservice.event.EventConstant.*;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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

    @Value("${instance}")
    private String instance;

    @Resource(name = "eventSender")
    private EventSender eventSender;

    /**
     * Send event when the user fetched teaser matches.
     * 
     * @param matchesFeedContext
     */
    public void sendTeaserMatchShownEvent(final MatchFeedRequestContext matchesFeedContext, final Map<String,String> eventContextInfo) {
        long userId = matchesFeedContext.getUserId();
        String userIdStr = String.valueOf(userId);

        Map<String, String> context = buildTeaserEventContext(matchesFeedContext);
        if (context == null) {
            return;
        }
        
        if(MapUtils.isNotEmpty(eventContextInfo)){
        
	        eventContextInfo.entrySet().forEach(item ->{
	        	context.put(item.getKey(), item.getValue());
	        });
        
        }
        
        Event teaserMatchShownEven = new CommandEvent.Builder().setCategory(TEASER_MATCH_EVENT_CATEGORY)
                .setProducer(PRODUCER).setInstance(instance).setContext(context).setFrom(userIdStr).setUid(userIdStr)
                .build();

        log.info("sending {} event for user {}", TEASER_MATCH_EVENT_CATEGORY, userId);
        eventSender.send(teaserMatchShownEven);
    }

    private Map<String, String> buildTeaserEventContext(final MatchFeedRequestContext matchesFeedContext) {
        Map<String, String> context = new HashMap<String, String>();
        String locale = matchesFeedContext.getMatchFeedQueryContext().getLocale();
        long userId = matchesFeedContext.getUserId();
        String userIdStr = String.valueOf(userId);

        context.put(LOCALE, locale);
        context.put(USER_ID, userIdStr);
        try {
            Integer matchCount = matchesFeedContext.getLegacyMatchDataFeedDto().getTotalMatches();
            if (matchCount == 0) {
                log.info("user[{}] doesn't have teaser matches.", userIdStr);
                return null;
            }

            context.put(MATCH_COUNT, matchCount.toString());
            List<String> matchIdList = ImmutableList
                .copyOf(matchesFeedContext.getLegacyMatchDataFeedDto().getMatches().keySet());
            context.put(MATCH_ID_LIST, matchIdList.toString());
        } catch (Exception exp) {
            log.warn("Error while building teaser match event context: userId[{}]", userIdStr, exp);
            return null;
        }
        return context;
    }
}
