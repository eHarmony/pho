package com.eharmony.services.mymatchesservice.rest;


public class SingleMatchQueryContext {

	public static final int SRC_HBASE_REDIS = 1;
	public static final int SRC_SORA = 2;
	public static final int SRC_MRS_MATCHSUMMARIES = 4;
	public static final int ALL_SOURCES = 7;
	
    private long matchId;
    private long userId;
    private int sources;
    
	public boolean isHBaseRedisEnabled() {
		return (sources & SRC_HBASE_REDIS) > 0;
	}

	public boolean isSORAEnabled() {
		return (sources & SRC_SORA) > 0;
	}
	
	public boolean isMRSMatchSummariesEnabled() {
		return (sources & SRC_MRS_MATCHSUMMARIES) > 0;
	}
	
	public SingleMatchQueryContext setSources(int sources) {
		this.sources = sources;
		return this;
	}
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
	
	public String toString(){
		
		return "userId= " + userId + ",matchId=" + matchId + ",sources=" + 
				(isHBaseRedisEnabled() ? "HBASE/REDIS ":"") + 
				(isSORAEnabled() ? "SORA ":"") + 
				(isMRSMatchSummariesEnabled() ? "MRS/MatchSummaries ":"");
					
	}

}
