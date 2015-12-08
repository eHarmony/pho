package com.eharmony.services.mymatchesservice.service;

import org.junit.Test;

import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;

public class HBaseStoreFeedServiceImplTest {

	@Test
	public void populateQueryWithLimitParams(){
		
		// TODO: check limits set correctly.
		long userId = 1234L;
		
		final MatchFeedQueryContext queryCtx = MatchFeedQueryContextBuilder.newInstance().setLocale("en_US")
										.setPageSize(5).setStartPage(2).build();
		
		final HBaseStoreFeedRequestContext request = new HBaseStoreFeedRequestContext(queryCtx);
		final MatchDataFeedQueryRequest requestQuery = new MatchDataFeedQueryRequest(userId);
		
		HBaseStoreFeedServiceImpl hbaseStore = new HBaseStoreFeedServiceImpl();
		hbaseStore.populateRequestWithQueryParams(request, requestQuery);
	}
}
