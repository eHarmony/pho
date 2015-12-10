package com.eharmony.services.mymatchesservice.event;

import java.util.HashMap;
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
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

@Component
public class RefreshEventSender {

    private static final String LOCALE = "locale";
    private static final String USER_ID = "userId";
    
    @Value("${hbase.feed.refresh.event.name:user.match.feed.refresh.new}" )
    public String USER_FEED_REFRESH_EVENT;
    
    public static final String OLD_USER_FEED_REFRESH_EVENT = "user.match.refresh.command.send";
    public static final String PRODUCER = "MQS";

    @Value("${instance}")
    private String instance;

    @Resource(name = "eventSender")
    private EventSender eventSender;

    @Value(value = "${refresh.event.enabled}")
    private boolean sendRefreshEvent;

    private Logger log = LoggerFactory.getLogger(RefreshEventSender.class);
    
    public void setEventSender(EventSender sender){
    	this.eventSender = sender;
    }

    public void sendRefreshEvent(MatchFeedRequestContext matchesFeedContext) {

        try {
            // send events only if configured to do so
            if (!sendRefreshEvent) {
                return;
            }

            // check voldy feed, if not present send a old refresh event
            if (shouldSendVoldyFeedRefreshEvent(matchesFeedContext)) {
                sendRefreshEvent(matchesFeedContext, OLD_USER_FEED_REFRESH_EVENT);
            }

            // check hbase records. If none present raise a new refresh event
            if (shouldSendHBaseFeedRefreshEvent(matchesFeedContext)) {
                sendRefreshEvent(matchesFeedContext, USER_FEED_REFRESH_EVENT);
            }

        } catch (Exception ex) {
            log.warn("Exception while sending the refresh event for user {}", matchesFeedContext.getUserId(), ex);
        }

    }

    private boolean shouldSendHBaseFeedRefreshEvent(MatchFeedRequestContext matchesFeedContext) {
        if (matchesFeedContext.hasHbaseMatches()) {
            return false;
        }

        LegacyMatchDataFeedDtoWrapper wrapper = matchesFeedContext.getLegacyMatchDataFeedDtoWrapper();
        if (wrapper != null && wrapper.getVoldyMatchesCount() > 0) {
            log.info("user {} has {} matches in voldy but no matches in hbase, sending refresh event.",
                    matchesFeedContext.getUserId(), wrapper.getVoldyMatchesCount());
            return true;
        }
        return false;
    }

    private boolean shouldSendVoldyFeedRefreshEvent(MatchFeedRequestContext matchesFeedContext) {
        LegacyMatchDataFeedDtoWrapper wrapper = matchesFeedContext.getLegacyMatchDataFeedDtoWrapper();
        if (wrapper == null) {
            log.warn("Voldy feed wrapper object must not be null for user {}.", matchesFeedContext.getUserId());
            return false;
        }

        if (wrapper.getError() != null) {
            log.info("Voldy feed is not available for user {} due to error.", matchesFeedContext.getUserId());
            return false;
        }

        if (wrapper.getVoldyMatchesCount() > 0) {
            // feed is available and there are matches in feed
            return false;
        }

        if(!wrapper.isFeedAvailable()) {
            return true;
        }
        
        if (wrapper.isFeedAvailable() && wrapper.getVoldyMatchesCount() == 0 && matchesFeedContext.hasHbaseMatches()) {
            log.info(
                    "Voldemort feed is not available for the user {} but hbase has records, emiting the feed refresh event..",
                    matchesFeedContext.getUserId());
            return true;
        }

        
        return false;
    }

    private void sendRefreshEvent(MatchFeedRequestContext matchesFeedContext, String categoryName) {

        try {

            long userId = matchesFeedContext.getUserId();
            String locale = matchesFeedContext.getMatchFeedQueryContext().getLocale();

            String userIdStr = String.valueOf(userId);

            Map<String, String> context = new HashMap<String, String>();
            context.put(LOCALE, locale);
            context.put(USER_ID, userIdStr);

            Event userFeedRefreshEvent = new CommandEvent.Builder().setCategory(categoryName).setProducer(PRODUCER)
                    .setInstance(instance).setContext(context).setFrom(userIdStr).setUid(userIdStr).build();

            log.info("sending {} event for user {}", categoryName, userId);
            eventSender.send(userFeedRefreshEvent);

        } catch (Exception e) {

            log.warn("failed to send event. ", e);

        }

    }

}
