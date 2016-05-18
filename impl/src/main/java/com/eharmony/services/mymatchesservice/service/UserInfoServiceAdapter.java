package com.eharmony.services.mymatchesservice.service;

import java.io.IOException;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.datastore.BeanDeserializationException;
import com.eharmony.protorest.RestClient;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;

@Component("userInfoServiceAdapter")
public class UserInfoServiceAdapter {

	@Resource(name="restClient")
	private RestClient restClient;
		
	private static final Logger logger= LoggerFactory.getLogger(UserInfoServiceAdapter.class);
	
    private static final String GET_USER_PATH = "/singles-userservice/v1/users/%s";
    
    @Value("${userinfo.service.url}")
    private String userServiceUrl;
    
    @Resource(name = "objectMapper")
    private ObjectMapper jsonMapper;
    
    public UserInfoDto getUserInfo(long userId){
    	
    	String url = String.format(userServiceUrl.concat(GET_USER_PATH), userId);
 
    	logger.debug("getting userInfo from{}", url);
 
		 try {
			 
			 String json = restClient.get(url, String.class);
		 	
			 UserInfoDto dto=  jsonMapper.readValue(json, UserInfoDto.class);            
		     return dto;
		     
		 } catch (IOException e) {
		     logger.error("Failed to read JSON", e);
		     throw new BeanDeserializationException("exception converting json to dto",
		         e);
		 }
    }
}
