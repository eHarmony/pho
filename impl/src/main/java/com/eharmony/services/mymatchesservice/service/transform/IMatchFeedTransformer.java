package com.eharmony.services.mymatchesservice.service.transform;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public interface IMatchFeedTransformer {

	MatchFeedRequestContext processMatchFeed(MatchFeedRequestContext context);
}
