package com.eharmony.services.mymatchesservice.rest;


public class SingleMatchQueryContext {

    private long matchId;
    private long userId;
        
	public long getMatchId() {
		return matchId;
	}
	public SingleMatchQueryContext setMatchId(long matchId) {
		this.matchId = matchId;
		return this;
	}
	public long getUserId() {
		return userId;
	}
	public SingleMatchQueryContext setUserId(long userId) {
		this.userId = userId;
		return this;
	}

}
