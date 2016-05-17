package com.eharmony.services.mymatchesservice.service;

import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.mds.model.MRSMatchProtoProtoBuffs.MRSMatchProto;
import com.eharmony.protorest.RestClient;

@Component("mrsAdapter")
public class MRSAdapter{
	
	@Resource(name="restClient")
	private RestClient restClient;
		
	private static final Logger logger= LoggerFactory.getLogger(MRSAdapter.class);
	
	private final String MRS_URL_TEMPLATE = "http://{mrsUrl}/mrs/2.0";

    private static final String GET_MATCH_PATH		  = "/matches/{matchId}/users/{userId}";
    private static final String GET_MATCH_PATH_BACKUP = "/matches/{matchId}";
    
    @Value("${matchretrieval.service.url}")
    private String mrsUrl;

	
	public MRSDto getMatch(long userId, long matchId){

		try{
	        // build final URI
	        String requestURI =  JerseyUriBuilder.fromPath(MRS_URL_TEMPLATE + GET_MATCH_PATH)
	        .resolveTemplate("mrsUrl", StringUtils.remove(mrsUrl, "http://"))
			.resolveTemplate("matchId", matchId)
			.resolveTemplate("userId", userId).build().toURL().toString();
	
	        MRSMatchProto match =  restClient.get(requestURI.toString(), MRSMatchProto.class);
	        if(match != null){
	        	return mrsMatchProto2MRSDto(userId, match);
	        }
	        
	        logger.info("Oriented getMatch for matchId {} NYI, using non-Oriented getMatch.", matchId);
	        // call backup.
	        requestURI =  JerseyUriBuilder.fromPath(MRS_URL_TEMPLATE + GET_MATCH_PATH_BACKUP)
	        .resolveTemplate("mrsUrl", mrsUrl)
			.resolveTemplate("matchId", matchId)
			.build().toURL().toString();
	
	         match =  restClient.get(requestURI.toString(), MRSMatchProto.class);
	         
	         // Discard if match is not for this userId
	         if(match.getUserId() != userId && match.getCandidateId() != userId){
	        	 return null;
	         }

	        return mrsMatchProto2MRSDtoWithOrientation(userId, match);
		}catch(MalformedURLException ex){
			
			logger.warn("Exception while calling mrs for matchId {}: {}", matchId, ex.getMessage());
			return null;
		}
	}

    private MRSDto mrsMatchProto2MRSDto(long userId, MRSMatchProto match) {

    	MRSDto result = new MRSDto();
        result.setDistance(match.getDistance());
        result.setOneWayStatus(match.getOneWayStatus().getNumber());
    	result.setArchiveStatus(match.getArchiveStatus().getNumber());
    	result.setClosedStatus(match.getDiscardStatus().getNumber());
       
    	return buildMrsDtoFromUserSide(result, match);
   	}
    
    private MRSDto mrsMatchProto2MRSDtoWithOrientation(long userId, MRSMatchProto match) {

    	MRSDto result = new MRSDto();
        result.setDistance(match.getDistance());
        result.setOneWayStatus(match.getOneWayStatus().getNumber());
    	result.setArchiveStatus(match.getArchiveStatus().getNumber());
    	result.setClosedStatus(match.getDiscardStatus().getNumber());
     
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

}
