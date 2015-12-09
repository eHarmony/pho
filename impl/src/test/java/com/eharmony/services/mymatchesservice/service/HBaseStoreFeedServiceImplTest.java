package com.eharmony.services.mymatchesservice.service;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;

public class HBaseStoreFeedServiceImplTest {

	@Test
	public void populateQueryWithLimitParams(){
		
		// TODO: check limits set correctly.
		long userId = 1234L;
		
		Set<String> statuses = new HashSet<String>();
		statuses.add("NEW");
		
		final MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US")
										.setPageSize(5).setStatuses(statuses).setStartPage(2).build();
		
		final HBaseStoreFeedRequestContext request = new HBaseStoreFeedRequestContext(queryCtx);
		final MatchDataFeedQueryRequest requestQuery = new MatchDataFeedQueryRequest(userId);
		
		HBaseStoreFeedServiceImpl hbaseStore = new HBaseStoreFeedServiceImpl();
		hbaseStore.populateRequestWithQueryParams(request, requestQuery);
		
		assertEquals(2, requestQuery.getStartPage());
		assertEquals(5, requestQuery.getPageSize());
	}
}
