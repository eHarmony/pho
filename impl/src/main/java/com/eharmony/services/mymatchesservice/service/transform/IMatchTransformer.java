package com.eharmony.services.mymatchesservice.service.transform;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;

public interface IMatchTransformer {

	SingleMatchRequestContext processSingleMatch(SingleMatchRequestContext context);

}
