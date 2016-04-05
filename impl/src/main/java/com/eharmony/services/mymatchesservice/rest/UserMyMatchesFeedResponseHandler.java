package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;
/**
 * Handles the response for MyMatches feed request.
 * 
 * Implements filter and enrichment components specific to MyMatches Feed API
 * 
 * @author vvangapandu
 *
 */
@Component("userMyMatchesFeedResponseHandler")
public class UserMyMatchesFeedResponseHandler extends AbstractFeedResponseHandler {

    @Resource(name = "getMatchesFeedEnricherChain")
    private MatchFeedTransformerChain getMatchesFeedEnricherChain;

    @Resource(name = "getUserSortedMatchesFilterChain")
    private MatchFeedTransformerChain userMyMatchesFeedFilterChain;

    @Override
    public void filterResults(MatchFeedRequestContext context) {
        userMyMatchesFeedFilterChain.execute(context);
    }

    @Override
    public void enrichFeedItems(MatchFeedRequestContext context) {
        getMatchesFeedEnricherChain.execute(context);
    }

}
