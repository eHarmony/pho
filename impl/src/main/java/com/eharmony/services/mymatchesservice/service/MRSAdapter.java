package com.eharmony.services.mymatchesservice.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.protorest.RestClient;

@Component("mrsAdapter")
public class MRSAdapter{
	
	@Value("${mrs.host}")
	private String mrsHost;
	
	@Value("${mrs.port}")
	private String mrsPort;
	
	@Resource(name="restClient")
	private RestClient restClient;
	
	private static final Logger logger= LoggerFactory.getLogger(MRSAdapter.class);
	
	private static final String MRS_URL = "http://%s:%s/matching-v1/match/%s";
	
	
	public MRSDto getMatch(long matchId){

		String url = String.format(MRS_URL, mrsHost, mrsPort, matchId);

		logger.debug("calling MRS: {}", url);
System.err.println("[MRSAdapter] - calling url: " + url);
		return restClient.get(url, MRSDto.class);
	}
}
