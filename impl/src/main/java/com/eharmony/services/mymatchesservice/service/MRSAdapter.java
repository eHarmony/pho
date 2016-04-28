package com.eharmony.services.mymatchesservice.service;

import java.net.URI;

import javax.annotation.Resource;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.eharmony.mds.model.MRSMatchProtoProtoBuffs.MRSMatchProto;
import com.eharmony.protorest.RestClient;

@Component("mrsAdapter")
public class MRSAdapter{
	
	@Resource(name="restClient")
	private RestClient restClient;
	
    private UriBuilder templateBuilder;
	
	private static final Logger logger= LoggerFactory.getLogger(MRSAdapter.class);
	
    private static final String CONTEXT = "/mrs";
    private static final String VERSION = "2.0";	
    private static final String GET_MATCH_PATH = "/matches/{matchId}";
	
	public MRSDto getMatch(long userId, long matchId){

        // build the URI
        UriBuilder builder = templateBuilder.clone().path(GET_MATCH_PATH);

        // build final URI
        URI requestURI = builder.build(matchId);

        MRSMatchProto match =  restClient.get(requestURI.toString(), MRSMatchProto.class);
        
        return mrsMatchProto2MRSDto(userId, match);
	}
	
    private MRSDto mrsMatchProto2MRSDto(long userId, MRSMatchProto match) {

    	MRSDto result = new MRSDto();
        result.setDistance(match.getDistance());
        result.setOneWayStatus(match.getOneWayStatus().getNumber());
    	result.setArchiveStatus(match.getArchiveStatus().getNumber());
       
    	if(userId == match.getUserId()){
    		return buildMrsDtoFromUserSide(result, match);
    	}else{
    		return buildMrsDtoFromCandidateSide(result, match);
    	}
   	}
    
    private MRSDto buildMrsDtoFromUserSide(MRSDto result, MRSMatchProto match){
 
        result.setDeliveryDate(match.getUserDeliveryMillisUtc());
        result.setMatchedUserId(match.getCandidateId());
        result.setRelaxed(match.getUserRelaxed());
        result.setUserId(match.getUserId());
        
    	return result;
    }

    private MRSDto buildMrsDtoFromCandidateSide(MRSDto result, MRSMatchProto match){
    	
        result.setDeliveryDate(match.getCandidateDeliveryMillisUtc());
        result.setMatchedUserId(match.getUserId());
        result.setRelaxed(match.getCandidateRelaxed());
        result.setUserId(match.getCandidateId());
        
    	return result;
    }
    
    
	@Value("${matchretrieval.service.url}")
    public void setServiceUrl(String serviceUrl) {

        Assert.hasText(serviceUrl, "serviceUrl parameter cannot be null");

        this.templateBuilder = UriBuilder.fromUri(serviceUrl)
                                         .path(CONTEXT + "/" + VERSION);
        logger.debug("Initialized Matchmaker Client for {}", templateBuilder.build());

    }
}
