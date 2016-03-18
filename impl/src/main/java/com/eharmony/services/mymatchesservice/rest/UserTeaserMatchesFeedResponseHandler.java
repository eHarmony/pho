package com.eharmony.services.mymatchesservice.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;

@Component("userTeaserMatchesFeedResponseHandler")
public class UserTeaserMatchesFeedResponseHandler extends AbstractFeedResponseHandler {

    @Resource(name = "getMatchesFeedEnricherChain")
    private MatchFeedTransformerChain getMatchesFeedEnricherChain;

    @Resource(name = "getTeaserMatchesFeedFilterChain")
    private MatchFeedTransformerChain teaserMatchesFeedFilterChain;

    @Override
    public void filterResults(MatchFeedRequestContext context) {
        teaserMatchesFeedFilterChain.execute(context);
    }

    @Override
    public void enrichFeedItems(MatchFeedRequestContext context) {
        getMatchesFeedEnricherChain.execute(context);
    }

}
