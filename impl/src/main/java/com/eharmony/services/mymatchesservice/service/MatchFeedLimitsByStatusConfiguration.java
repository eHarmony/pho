package com.eharmony.services.mymatchesservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import com.google.common.base.Preconditions;

@Component
public class MatchFeedLimitsByStatusConfiguration {

    private Map<MatchStatusGroupEnum, Integer> defaultFeedLimitThresholds = new HashMap<MatchStatusGroupEnum, Integer>();
    private Map<MatchStatusGroupEnum, Integer> fallbackFeedLimitThresholds = new HashMap<MatchStatusGroupEnum, Integer>();

    @Autowired
    public MatchFeedLimitsByStatusConfiguration(
            @Value("${matches.feed.limit.new.default:1000}") final int defaultNewMatchesLimit,
            @Value("${matches.feed.limit.archive.default:1000}") final int defaultArchiveMatchesLimit,
            @Value("${matches.feed.limit.new.fallback:200}") final int fallbackNewMatchesLimit,
            @Value("${matches.feed.limit.archive.fallback:100}") final int fallbackArchiveMatchesLimit) {

        Preconditions.checkArgument(defaultNewMatchesLimit > 0, "defaultNewMatchesLimit must be > 0 ");
        Preconditions.checkArgument(fallbackNewMatchesLimit > 0, "fallbackNewMatchesLimit must be > 0 ");

        defaultFeedLimitThresholds.put(MatchStatusGroupEnum.NEW, defaultNewMatchesLimit);
        defaultFeedLimitThresholds.put(MatchStatusGroupEnum.ARCHIVE, defaultArchiveMatchesLimit);
        fallbackFeedLimitThresholds.put(MatchStatusGroupEnum.NEW, fallbackNewMatchesLimit);
        fallbackFeedLimitThresholds.put(MatchStatusGroupEnum.ARCHIVE, fallbackArchiveMatchesLimit);

    }

    public Integer getDefaultFeedLimitForGroup(MatchStatusGroupEnum matchStatusGroup) {
        return defaultFeedLimitThresholds.get(matchStatusGroup);
    }

    public Integer getFallbackFeedLimitForGroup(MatchStatusGroupEnum matchStatusGroup) {
        return fallbackFeedLimitThresholds.get(matchStatusGroup);
    }
}
