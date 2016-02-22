package com.eharmony.services.mymatchesservice.rest.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataServiceThrottleManager {
    
	private static final Logger logger = LoggerFactory.getLogger(DataServiceThrottleManager.class);

	@Value("${redis.merge.enabled:false}") 
    private boolean redisMergeMode; 
	
	@Value("${redis.merge.sampling.percent:0}") 
    private int redisSamplingPct;       
	
	@Value("#{new com.eharmony.services.mymatchesservice.rest.internal.DataServiceThrottleManager.WhitelistBuilder().buildWhitelist('${redis.merge.sampling.whitelist:}')}") 
    private Set<Long> redisWhitelistSet;
    
	public DataServiceThrottleManager(){
		this(false, 0, "");		
	}
	
    public DataServiceThrottleManager(boolean redisMergeMode, 
    	    						  int redisSamplingPct,
    	    						  String useridWhitelist){
    	
    	this.redisMergeMode = redisMergeMode;
    	this.redisSamplingPct = redisSamplingPct;
    	
    	redisWhitelistSet = WhitelistBuilder.buildWhitelist(useridWhitelist);
    }
    
    public boolean isRedisSamplingEnabled( long userId){
    	
    	if(!redisMergeMode){
    		return false;
    	}
    	
    	if(redisWhitelistSet.contains(userId)){
    		return true;
    	}
    	
        int mod = (int) (userId % 100);
        return (mod < redisSamplingPct);
    }
    
    
    public static class WhitelistBuilder{
    	    	
        public static Set<Long> buildWhitelist(String redisWhitelist){
        	
        	Set<Long> ret = new HashSet<Long>();
        	StringTokenizer st = new StringTokenizer(redisWhitelist,",");
        	while(st.hasMoreTokens()){
        		String token = st.nextToken();
        		if(!StringUtils.isEmpty(token)){
        			
        			logger.info("Adding userId {} to whitelist.", token);
        			
        			ret.add(Long.valueOf(token));
        		}
        	}
        	
        	return ret;
        }
    }
}
