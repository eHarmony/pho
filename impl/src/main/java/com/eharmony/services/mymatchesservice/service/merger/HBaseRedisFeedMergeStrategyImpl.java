package com.eharmony.services.mymatchesservice.service.merger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public class HBaseRedisFeedMergeStrategyImpl implements FeedMergeStrategy {

    private static final Logger log = LoggerFactory.getLogger(HBaseRedisFeedMergeStrategyImpl.class);

	@Override
	public void merge(MatchFeedRequestContext request) {
		
        log.warn("HBASE-REDIS MERGE NOT YET IMPLEMENTED", request.getMatchFeedQueryContext().getUserId());

	}

}
