package com.eharmony.services.mymatchesservice.rest;

import java.util.Map;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.services.mymatchesservice.service.MRSDto;
import com.eharmony.services.mymatchesservice.store.data.MatchDo;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;

/**
 * Manages data from all the required sources during a getMatch request.
 * - Redis feed + HBase match item
 * - SORA EHMATCH
 * - SORA MATCH_SUMMARIES
 * - MRS match item
 * 
 * @author kmunroe
 *
 */
public class SingleMatchRequestContext {

	// The resultant single match
	private Map<String, Map<String, Object>> singleMatch;

	// Data sources we gather to build single match
    private Map<String, Map<String, Object>> redisMatch;
    private MatchDataFeedItemDto hbaseMatch;
    private MatchDo matchDo;
    private MatchSummaryDo matchSummaryDo;
    private MRSDto mrsDto;
    
    // Query parameters
    private SingleMatchQueryContext queryContext;
    
    public SingleMatchRequestContext(SingleMatchQueryContext queryCtx){
    	this.queryContext = queryCtx;
    }
    
    public boolean matchIsAvailable(){
    	return singleMatch != null;
    }
    
    public SingleMatchQueryContext getQueryContext() {
		return queryContext;
	}

	public MatchDo getMatchDo() {
		return matchDo;
	}

	public void setMatchDo(MatchDo matchDo) {
		this.matchDo = matchDo;
	}

	public MatchSummaryDo getMatchSummaryDo() {
		return matchSummaryDo;
	}

	public void setMatchSummaryDo(MatchSummaryDo matchSummaryDo) {
		this.matchSummaryDo = matchSummaryDo;
	}

	public MRSDto getMrsDto() {
		return mrsDto;
	}

	public void setMrsDto(MRSDto mrsDto) {
		this.mrsDto = mrsDto;
	}
    
	public Map<String, Map<String, Object>> getSingleMatch() {
		return singleMatch;
	}
	
	public Map<String, Map<String, Object>> getRedisMatch() {
		return redisMatch;
	}
	
	public void setRedisMatch(Map<String, Map<String, Object>> match) {
		this.redisMatch = match;
	}
	
	public MatchDataFeedItemDto getHbaseMatch() {
		return hbaseMatch;
	}
	
	public void setHbaseMatch(MatchDataFeedItemDto hbaseMatch) {
		this.hbaseMatch = hbaseMatch;
	}
	
	public void setSingleMatch(Map<String, Map<String, Object>> singleMatch) {
		this.singleMatch = singleMatch;
	}
}
