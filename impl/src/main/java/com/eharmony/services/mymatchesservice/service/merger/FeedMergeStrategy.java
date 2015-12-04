package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.UserMatchesHBaseStoreFeedService;

public interface FeedMergeStrategy{

	void merge(MatchFeedRequestContext request, UserMatchesHBaseStoreFeedService userMatches);
}
