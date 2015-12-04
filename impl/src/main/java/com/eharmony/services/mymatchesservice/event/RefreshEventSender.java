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

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.event.CommandEvent;
import com.eharmony.event.Event;
import com.eharmony.event.EventSender;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.singles.common.enumeration.Gender;

@Component
public class RefreshEventSender {

    private static final String LOCALE = "locale";
    private static final String GENDER = "gender";
    private static final String USER_ID = "userId";

    public static final String USER_FEED_REFRESH_EVENT = "user.match.feed.refresh";
    public static final String OLD_USER_FEED_REFRESH_EVENT = "user.match.refresh.command.send";
    public static final String PRODUCER = "MQS";
    public static final String CP_LOCALE = "en_US_10";

    @Value("${instance}")
    private String instance;

    @Resource(name = "eventSender")
    private EventSender eventSender;

    @Value(value = "${refresh.event.enabled}")
    private boolean sendRefreshEvent;

    private Logger log = LoggerFactory.getLogger(RefreshEventSender.class);

    public void sendRefreshEvent(MatchFeedRequestContext matchesFeedContext) {

        try {
            // send events only if configured to do so
            if (!sendRefreshEvent) {
                return;
            }

            // check voldy feed, if not present send a old refresh event
            if (!isVoldyFeedPresent(matchesFeedContext)) {
                sendRefreshEvent(matchesFeedContext, OLD_USER_FEED_REFRESH_EVENT);
            }

            // check hbase records. If none present raise a new refresh event
            if (!isHBASEFeedPresent(matchesFeedContext)) {
                sendRefreshEvent(matchesFeedContext, USER_FEED_REFRESH_EVENT);
            }

            // if there is an empty voldy feed but some HBASE records; raise an old refresh event
            if (voldyOutOfSync(matchesFeedContext)) {
                log.warn("For userId {}, voldy has an empty feed but HBASE has some records: ",
                        matchesFeedContext.getUserId());
                sendRefreshEvent(matchesFeedContext, OLD_USER_FEED_REFRESH_EVENT);
            }
        } catch (Exception ex) {
            log.warn("Exception while sending the refresh event for user {}", matchesFeedContext.getUserId(), ex);
        }

    }

    private boolean voldyOutOfSync(MatchFeedRequestContext matchesFeedContext) {
        if (matchesFeedContext.getLegacyMatchDataFeedDtoWrapper() == null) {
            return false;
        }
        // if there is a voldy feed
        if (matchesFeedContext.getLegacyMatchDataFeedDtoWrapper().isFeedAvailable()) {
            // is it empty?
            boolean doesNotHaveVoldyMatches = MapUtils.isEmpty(matchesFeedContext.getLegacyMatchDataFeedDtoWrapper()
                    .getLegacyMatchDataFeedDto().getMatches());
            if (doesNotHaveVoldyMatches) {
                if (CollectionUtils.isNotEmpty(matchesFeedContext.getNewStoreFeed())) {
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
        if (matchesFeedContext.getLegacyMatchDataFeedDtoWrapper() == null) {
            return false;
        }
        return matchesFeedContext.getLegacyMatchDataFeedDtoWrapper().isFeedAvailable();
    }

    private void sendRefreshEvent(MatchFeedRequestContext matchesFeedContext, String categoryName) {

        try {

            long userId = matchesFeedContext.getUserId();
            String locale = matchesFeedContext.getMatchFeedQueryContext().getLocale();

            String userIdStr = String.valueOf(userId);

            // NOTE: This is a super hack.
            // Ideally the api for feed refresh in MDS and Match events should lookup profile and get this info for the
            // given user
            String gender = deriveUserGender(matchesFeedContext);

            Map<String, String> context = new HashMap<String, String>();
            context.put(LOCALE, locale);
            context.put(USER_ID, userIdStr);
            context.put(GENDER, gender);

            Event userFeedRefreshEvent = new CommandEvent.Builder().setCategory(categoryName).setProducer(PRODUCER)
                    .setInstance(instance).setContext(context).setFrom(userIdStr).setUid(userIdStr).build();

            log.info("sending {} event for user {}", categoryName, userId);
            eventSender.send(userFeedRefreshEvent);

        } catch (Exception e) {

            log.warn("failed to send event. ", e);

        }

    }

    private String deriveUserGender(MatchFeedRequestContext matchesFeedContext) {

        String userLocale = matchesFeedContext.getMatchFeedQueryContext().getLocale();
        if (CollectionUtils.isNotEmpty(matchesFeedContext.getNewStoreFeed())) {
            MatchDataFeedItemDto oneFeedItem = matchesFeedContext.getNewStoreFeed().iterator().next();
            Gender matchedUserGender = Gender.fromInt(oneFeedItem.getMatchedUser().getGender());
            if (userLocale.equalsIgnoreCase(CP_LOCALE)) {
                return matchedUserGender.name();
            } else {
                if (Gender.MALE.compareTo(matchedUserGender) == 0) {
                    return Gender.FEMALE.name();
                } else {
                    return Gender.MALE.name();
                }
            }
        }

        log.warn("Could not derive gender for user {} since there are no HBASE records either", matchesFeedContext
                .getMatchFeedQueryContext().getUserId());
        return Gender.UNKNOWN.name();
    }
}
