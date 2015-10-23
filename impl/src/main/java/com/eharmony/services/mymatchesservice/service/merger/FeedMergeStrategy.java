package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;

public interface FeedMergeStrategy<T> {

	T merge(MatchDataFeedQueryRequest request);
}
