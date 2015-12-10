package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public interface FeedMergeStrategy{

	void merge(MatchFeedRequestContext request);
}
