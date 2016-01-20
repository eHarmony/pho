package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

import com.eharmony.services.mymatchesservice.MatchTestUtils;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class StateCodeEnricherTest {

	@SuppressWarnings("unchecked")	
	private void getMatchesAndVerifyLogging(String fileName, String expectedLogMessage) throws Exception{
		
		long USER_ID = 123L;
		
		StateCodeEnricher enricher = new StateCodeEnricher();
		
		// build the filter context...
		MatchFeedQueryContext qctx = MatchFeedQueryContextBuilder
											.newInstance()
											.setUserId(USER_ID).build(); 
		
		MatchFeedRequestContext ctx = new MatchFeedRequestContext(qctx);		
		LegacyMatchDataFeedDto legacyMatchDataFeedDto = 
						MatchTestUtils.getTestFeed(fileName);
		LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(qctx.getUserId());
		legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(legacyMatchDataFeedDto);
        ctx.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);

        // Setup a log appender to pick up logging messages...
        ch.qos.logback.classic.Logger root = 
        		(ch.qos.logback.classic.Logger) 
        			LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        final Appender mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");
        root.addAppender(mockAppender);		
        
		enricher.processMatchFeed(ctx);
		
		if(expectedLogMessage == null){
			
			verifyZeroInteractions(mockAppender);
			
		}else{
			verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {
			      @Override
			      public boolean matches(final Object argument) {
			        return ((LoggingEvent)argument).toString()
			        		.contains(expectedLogMessage);
			      }
			    }));	
		}
		
	}
	
	@Test
	public void testNullStateCodesLogged() throws Exception{
		
		getMatchesAndVerifyLogging("json/getMatches_brokenStateCodes.json",
				"[WARN] State code for userId 62599299 is unresolved: \"????743.code????\"" );
	}
	
	@Test
	public void testUnknownStateCodesLogged() throws Exception{

		getMatchesAndVerifyLogging("json/getMatches_nullStateCodes.json",
				"[WARN] State code for userId 62599299 is blank.");	
	}
	
	@Test
	public void testOKStateCodesNotLogged() throws Exception{
		
		getMatchesAndVerifyLogging("json/getMatches.json", null);
	}
}
