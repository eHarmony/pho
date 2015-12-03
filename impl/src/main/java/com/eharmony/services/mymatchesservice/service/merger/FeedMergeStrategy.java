package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.UserMatchesFeedService;

public interface FeedMergeStrategy{

	void merge(MatchFeedRequestContext request, UserMatchesFeedService userMatches);
}
