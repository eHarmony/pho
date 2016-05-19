package com.eharmony.services.mymatchesservice.service.transform.filter;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;

public interface IMatchFilter {

	SingleMatchRequestContext filterSingleMatch(SingleMatchRequestContext context);

}
