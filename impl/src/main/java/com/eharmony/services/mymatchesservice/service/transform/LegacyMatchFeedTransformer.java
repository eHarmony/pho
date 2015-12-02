package com.eharmony.services.mymatchesservice.service.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

public class LegacyMatchFeedTransformer {

    private static final Logger logger = LoggerFactory.getLogger(LegacyMatchFeedTransformer.class);

    public LegacyMatchDataFeedDto transform(final Set<MatchDataFeedItemDto> hbaseFeedItems) {
        LegacyMatchDataFeedDto feedDto = new LegacyMatchDataFeedDto();
        Map<String, Map<String, Map<String, Object>>> matches = new HashMap<String, Map<String, Map<String, Object>>>();
        hbaseFeedItems.forEach(item -> {
            if (item != null && item.getMatch() != null) {
                String matchId = String.valueOf(item.getMatch().getMatchId());
                matches.put(matchId, buildLegacyFeedItem(item));
            } else {
                logger.warn("Skipping the invalid feed item..");
            }
        });
        return feedDto;
    }

    private Map<String, Map<String, Object>> buildLegacyFeedItem(MatchDataFeedItemDto matchDataFeedItemDto) {
        Map<String, Map<String, Object>> feedItemMap = new HashMap<String, Map<String, Object>>();

        // TODO transformations by section
        return feedItemMap;
    }
}
