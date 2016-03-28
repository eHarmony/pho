package com.eharmony.services.mymatchesservice.service.merger;

public enum FeedMergeStrategyType {

	// TODO: if HBASE_FEED_ONLY not used, there'll be only 1 strategy, and no need for 
	// this FeedMergeStrategy paradigm in general.
	
    HBASE_FEED_ONLY(4,"HBASE_FEED_ONLY", "HBASE"), 
    HBASE_FEED_WITH_MATCH_MERGE(5, "HBASE_FEED_WITH_MATCH_MERGE", "HBASE");

    final int code;
    final String name;
    final String direction;

    private FeedMergeStrategyType(final int code, final String name, final String direction) {
        this.code = code;
        this.name = name;
        this.direction = direction;
    }
}
