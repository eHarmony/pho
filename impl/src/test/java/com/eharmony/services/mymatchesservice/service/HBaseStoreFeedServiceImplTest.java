package com.eharmony.services.mymatchesservice.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.rest.MatchCountContext;
import com.eharmony.services.mymatchesservice.rest.MatchCountRequestContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
public class HBaseStoreFeedServiceImplTest {
	HBaseStoreFeedServiceImpl svc = new HBaseStoreFeedServiceImpl();
	@Test
	public void testGetMatchCount() throws Exception {
		MatchStoreQueryRepository queryRepository = mock(MatchStoreQueryRepository.class);
		ReflectionTestUtils.setField(svc, "queryRepository", queryRepository);
		ReflectionTestUtils.setField(svc, "newMatchThresholdDays", 3);
		MatchCountRequestContext matchCountRequest = new MatchCountRequestContext();
		matchCountRequest.setUserId(1000l);
		MatchCountContext result = svc.getUserMatchesCount(matchCountRequest);
		verify(queryRepository,times(1)).getMatchCountDto(any());
	}

}