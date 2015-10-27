package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public interface FeedMergeStrategy<T> {

	T merge(MatchFeedRequestContext request);
}
