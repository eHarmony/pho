package com.eharmony.services.mymatchesservice.service;

import com.google.common.base.Preconditions;

public class HBaseStoreSingleMatchRequestContext {
	
	private long userId;
	private long matchId;
	
	public HBaseStoreSingleMatchRequestContext(long userId, long matchId){ 
		Preconditions.checkNotNull(matchId, "matchId must not be null");
		Preconditions.checkNotNull(userId, "userId must not be null");
		
		this.matchId = matchId;
		this.userId = userId;
	}

	public long getUserId() {
		return userId;
	}

	public long getMatchId() {
		return matchId;
	}

}
