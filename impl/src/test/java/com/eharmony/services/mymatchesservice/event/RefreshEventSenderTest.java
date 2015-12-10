package com.eharmony.services.mymatchesservice.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.eharmony.event.Event;
import com.eharmony.event.EventResponse;
import com.eharmony.event.EventSender;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

@ContextConfiguration
public class RefreshEventSenderTest {

//TODO: fix PropertySource issue
	@Test
	public void testDefaultHBaseRefreshEvent(){
//		
//		// set up Voldy feed, no HBase.
//		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().build();
//		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);
//		
//		long matchId = 11790420914L;
//		LegacyMatchDataFeedDto legacy = new LegacyMatchDataFeedDto();
//		Map<String, Map<String, Map<String, Object>>> matches = new HashMap<>();
//		matches.put(String.valueOf(matchId), new HashMap<String, Map<String, Object>>());
//		legacy.setMatches(matches);
//		
//		long userId = 62837673;
//		
//		ctx.setLegacyMatchDataFeedDtoWrapper(new LegacyMatchDataFeedDtoWrapper(userId));
//		LegacyMatchDataFeedDtoWrapper wrapper = ctx.getLegacyMatchDataFeedDtoWrapper();
//
//		wrapper.setLegacyMatchDataFeedDto(legacy);
//		wrapper.setFeedAvailable(true);
//		wrapper.setVoldyMatchesCount(1);
//			
//		MockEventSender eventSender = new MockEventSender();
//		
//		RefreshEventSender target = new RefreshEventSender();
//		target.setEventSender(eventSender);
//		ReflectionTestUtils.setField(target, "sendRefreshEvent", true);
//		ReflectionTestUtils.setField(target, "instance", "junit-test");
//		
//		target.sendRefreshEvent(ctx);
//		
//		Event sentEvent = eventSender.getLastEvent();
//		
//		assertNotNull(sentEvent);
//		assertEquals("user.match.feed.request.new", sentEvent.getCategory());
//	
	}
	
	static class MockEventSender implements EventSender{

		private Event lastEvent;
		
		public Event getLastEvent(){
			return lastEvent;
		}
		
		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Future<EventResponse> send(Event event) {
			this.lastEvent = event;
			return null;
		}
	}
	
	@Configuration
    public static class SpringConfiguration {
		
		@org.springframework.context.annotation.Bean
		public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		    PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		    ppc.setIgnoreResourceNotFound(true);
		    return ppc;
		}
    }
}
