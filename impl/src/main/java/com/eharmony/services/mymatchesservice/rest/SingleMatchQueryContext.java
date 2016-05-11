package com.eharmony.services.mymatchesservice.rest;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;


public class SingleMatchQueryContext {
	
    private long matchId;
    private long userId;
    private Set<String> disabledSources;
    
    public static final String SRC_HBASE = "hbase";
    public static final String SRC_SORA = "sora";
	
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
	public Set<String> getDisabledSources() {
		return disabledSources;
	}
	
	public void setDisabledSources(String sources) {
		disabledSources = new HashSet<String>();
		if(StringUtils.isEmpty(sources)){
			return;
		}
		
		StringTokenizer st = new StringTokenizer(sources, ",");
		
		while(st.hasMoreTokens()){
			String source = st.nextToken();
			if(source.equalsIgnoreCase("hbase")){
				disabledSources.add(SRC_HBASE);
			}else if(source.equalsIgnoreCase("sora")){
				disabledSources.add(SRC_SORA);
			}
		}
	}

	
	public boolean isHBaseRedisEnabled(){
		System.err.println("*** [SingleMatchQueryContext] - HBASE/REDIS DISABLED: " + disabledSources.contains(SRC_HBASE) + " ***");
		return(!disabledSources.contains(SRC_HBASE));
	}

	public boolean isSORAEnabled(){
		System.err.println("*** [SingleMatchQueryContext] - SORA DISABLED: " + disabledSources.contains(SRC_SORA) + " ***");
		return(!disabledSources.contains(SRC_SORA));
	}
}
