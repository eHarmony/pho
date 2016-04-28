package com.eharmony.services.mymatchesservice.rest;

import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.merger.HBaseRedisMatchMerger;
import com.eharmony.services.mymatchesservice.service.merger.MRSAndSORAMatchMerger;

@Component
public class SingleMatchResponseHandler{
    
    private HBaseRedisMatchMerger hbaseRedisMatchMerger = new HBaseRedisMatchMerger();
    
    private MRSAndSORAMatchMerger mrsAndSORAMatchMerger = new MRSAndSORAMatchMerger();

	public void processMatchFromHBaseAndRedis(SingleMatchRequestContext requestContext){
		
    	hbaseRedisMatchMerger.merge(requestContext);
	}
	
	public void processMatchFromMRSAndSORA(SingleMatchRequestContext requestContext){
		
		mrsAndSORAMatchMerger.mergeMatch(requestContext);
	}
}
