package com.eharmony.services.mymatchesservice.event;

import java.util.HashMap;
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

@Component
public class RefreshEventSender {

    private static final String LOCALE = "locale";
    private static final String USER_ID = "userId";
    
    public static final String USER_FEED_REFRESH_EVENT = "user.match.feed.refresh";
    public static final String OLD_USER_FEED_REFRESH_EVENT = "user.match.refresh.command.send";
    public static final String PRODUCER = "MQS";
    
    @Value("${instance}")
    private String instance;
    
    @Resource(name="eventSender")
    private EventSender eventSender;
    
    @Value(value="${refresh.event.enabled}")
    private boolean sendRefreshEvent;
    
    private Logger log = LoggerFactory.getLogger(RefreshEventSender.class);
    
    public void sendRefreshEvent (MatchFeedRequestContext matchesFeedContext) {
   
    	// send events only if configured to do so
    	if(!sendRefreshEvent) {
    		return;
    	}
    	
    	long userId = matchesFeedContext.getUserId();
    	String locale = matchesFeedContext.getMatchFeedQueryContext().getLocale();
    	
    	// check voldy feed, if not present send a old refresh event
    	if(!isVoldyFeedPresent(matchesFeedContext)) {
    		sendRefreshEvent(userId, locale, OLD_USER_FEED_REFRESH_EVENT);
    	}
    	
    	// check hbase records. If none present raise a new refresh event
    	if(!isHBASEFeedPresent(matchesFeedContext)) {
    		sendRefreshEvent(userId, locale, USER_FEED_REFRESH_EVENT);
    	}
    	
    	// if there is an empty voldy feed but some HBASE records; raise an old refresh event
    	if(voldyOutOfSync(matchesFeedContext)) {
    		log.warn("For userId {}, voldy has an empty feed but HBASE has some records: ", userId);
    		sendRefreshEvent(userId, locale, OLD_USER_FEED_REFRESH_EVENT);
    	}
    	
    }
    
	private boolean voldyOutOfSync(MatchFeedRequestContext matchesFeedContext) {
		// if there is a voldy feed
		if(matchesFeedContext.getLegacyMatchDataFeedDtoWrapper().isFeedAvailable()) {
			// is it empty?
			boolean doesNotHaveVoldyMatches = MapUtils.isEmpty(matchesFeedContext.getLegacyMatchDataFeedDtoWrapper().getLegacyMatchDataFeedDto().getMatches());
			if(doesNotHaveVoldyMatches) {
				if(CollectionUtils.isNotEmpty(matchesFeedContext.getNewStoreFeed())) {
					return true;
				}
				
			}
		}
		return false;
	}

	private boolean isHBASEFeedPresent(MatchFeedRequestContext matchesFeedContext) {
		return CollectionUtils.isNotEmpty(matchesFeedContext.getNewStoreFeed());
	}

	private boolean isVoldyFeedPresent(MatchFeedRequestContext matchesFeedContext) {
		return matchesFeedContext.getLegacyMatchDataFeedDtoWrapper().isFeedAvailable();
	}

	public void sendRefreshEvent(long userId, String lcoale, String categoryName) {
    	
        try {
        	
        	String userIdStr = String.valueOf(userId);

            Map<String, String> context = new HashMap<String, String>();
            context.put(LOCALE, lcoale);
            context.put(USER_ID, userIdStr);
            // NOTE: we dont have gender here so not populating it. On the receiving side will have to look it up.

            Event userFeedRefreshEvent =
                new CommandEvent.Builder().setCategory(categoryName)
                                          .setProducer(PRODUCER)
                                          .setInstance(instance)
                                          .setContext(context)
                                          .setFrom(userIdStr)
                                          .setUid(userIdStr)
                                          .build();

            log.info("sending {} event for user {}", categoryName, userId);
            eventSender.send(userFeedRefreshEvent);

        } catch (Exception e) {

            log.warn("failed to send event. ", e);

        }

    }
}
