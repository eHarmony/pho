package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;

import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyManager;
import com.eharmony.services.mymatchesservice.service.transform.HBASEToLegacyFeedTransformer;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

/**
 * Feed Response Handler handles transformation, filtering and enrichment  of user matches feed
 * 
 * @author vvangapandu
 *
 */
public abstract class AbstractFeedResponseHandler implements FeedResponseHandler {

    @Resource
    private HBASEToLegacyFeedTransformer hbaseToLegacyFeedTransformer;

    @Resource
    private FeedMergeStrategyManager feedMergeStrategyManager;

    /**
     * Processes the match feed response by applying the transformations, filters and enriching the content
     * 
     * 1. Transforms the content from Store view to API response view (List of Objects to Map of Matches to support
     * legacy application) 2. Invokes the feed merge component to merge different feed views ( ex: HBase and Redis) 3.
     * Invokes the abstract filter component, which will be overriden by specific implementation like teaser. 4. Invokes
     * the abstract enricher component, which will be overriden by specific implementation like teaser.
     * 
     * @param context
     *            MatchFeedRequestContext
     */
    @Override
    public void processMatchFeedResponse(MatchFeedRequestContext context) {
        transformHBaseResponseToMapBasedResponse(context);
        // throwExceptionIfFeedIsNotAvailable(context);
        mergeHbaseFeedWithRedisDelta(context);
        filterResults(context);
        enrichFeedItems(context);
        enrichMatchCount(context);

    }
    
    protected void enrichMatchCount(MatchFeedRequestContext context) {
        LegacyMatchDataFeedDto feedDto = context.getLegacyMatchDataFeedDto();
        if (feedDto != null) {
            feedDto.setTotalMatches(0);
            if (MapUtils.isNotEmpty(feedDto.getMatches())) {
                //TODO doubling the size to make the pagination work on mobile web
                int size = feedDto.getMatches().size();
                feedDto.setTotalMatches(size * 2 );
            }
        }
    }

    /**
     * Transforms response from hbase to API contract response format ( Map of match objects)
     * 
     * @param context  MatchFeedRequestContext
     * 
     */
    protected void transformHBaseResponseToMapBasedResponse(MatchFeedRequestContext context) {
        hbaseToLegacyFeedTransformer.transformHBASEFeedToLegacyFeed(context);
    }

    /**
     * Performs feed items merge between HBase feed and Redis Delta
     * 
     * @param context
     *            MatchFeedRequestContext
     * 
     */
    protected void mergeHbaseFeedWithRedisDelta(MatchFeedRequestContext context) {
        feedMergeStrategyManager.getMergeStrategy(context).merge(context);
    }

    protected abstract void filterResults(MatchFeedRequestContext context);
    protected abstract void enrichFeedItems(MatchFeedRequestContext context);
    

}
