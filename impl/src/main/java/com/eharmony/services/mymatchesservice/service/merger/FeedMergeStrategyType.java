package com.eharmony.services.mymatchesservice.service.merger;

public enum FeedMergeStrategyType {

    VOLDY_FEED_ONLY(1, "VOLDY_FEED_ONLY", "VOLDY"), VOLDY_FEED_WITH_PROFILE_MERGE(2, "VOLDY_FEED_WITH_PROFILE_MERGE",
            "VOLDY"), VOLDY_FEED_WITH_FULL_MERGE(3, "VOLDY_FEED_WITH_FULL_MERGE", "VOLDY"), HBASE_FEED_ONLY(4,
            "HBASE_FEED_ONLY", "HBASE"), HBASE_FEED_WITH_MATCH_MERGE(5, "HBASE_FEED_WITH_MATCH_MERGE", "HBASE");

    final int code;
    final String name;
    final String direction;

    private FeedMergeStrategyType(final int code, final String name, final String direction) {
        this.code = code;
        this.name = name;
        this.direction = direction;
    }
}
