package com.eharmony.services.mymatchesservice.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.eharmony.event.EventSender;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

import com.eharmony.event.Event;
public class MatchQueryEventServiceTest {
	MatchQueryEventService mqsEventSvc = new MatchQueryEventService();

	@Test
	public void testSendTeaserMatchShownEvent() {
		MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US").setUserId(100L).build();
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(queryCtx);

		LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(100L);
		LegacyMatchDataFeedDto legacyMatchDataFeedDto = new LegacyMatchDataFeedDto();
		legacyMatchDataFeedDto.setTotalMatches(3);
		Map<String, Map<String, Map<String, Object>>> matchesMap = new HashMap<>(3);
		matchesMap.put("990", null);
		matchesMap.put("991", null);
		matchesMap.put("992", null);
		
		legacyMatchDataFeedDto.setMatches(matchesMap);
		legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(legacyMatchDataFeedDto );
		ctx.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
		
		EventSender eventSender = mock(EventSender.class);
		ReflectionTestUtils.setField(mqsEventSvc, "eventSender", eventSender);
		ReflectionTestUtils.setField(mqsEventSvc, "instance", "instance1");
		
		mqsEventSvc.sendTeaserMatchShownEvent(ctx);
		verify(eventSender).send(any(Event.class));
	}

}
