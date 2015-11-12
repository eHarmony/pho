package com.eharmony.services.mymatchesservice.service.filter;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public interface IMatchFeedFilter {

	MatchFeedRequestContext processMatchFeed(MatchFeedRequestContext context);
}
