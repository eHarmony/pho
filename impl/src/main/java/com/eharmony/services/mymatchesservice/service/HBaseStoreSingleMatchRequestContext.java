package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.rest.SingleMatchQueryContext;
import com.google.common.base.Preconditions;

public class HBaseStoreSingleMatchRequestContext {

	private SingleMatchQueryContext singleMatchQueryContext;
	
	public HBaseStoreSingleMatchRequestContext(SingleMatchQueryContext queryCtx){ 
		Preconditions.checkNotNull(queryCtx, "SingleMatchQueryContext must not be null");
		this.singleMatchQueryContext = queryCtx;
	}

	public SingleMatchQueryContext getSingleMatchQueryContext() {
		return singleMatchQueryContext;
	}


}
