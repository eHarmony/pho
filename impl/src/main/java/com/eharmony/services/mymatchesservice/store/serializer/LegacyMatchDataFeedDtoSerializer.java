package com.eharmony.services.mymatchesservice.store.serializer;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.BeanDeserializationException;
import com.eharmony.datastore.store.serializer.AbstractJsonSerializer;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;


public class LegacyMatchDataFeedDtoSerializer extends AbstractJsonSerializer<LegacyMatchDataFeedDto>{
	
    private Logger log = LoggerFactory.getLogger(LegacyMatchDataFeedDtoSerializer.class);

    @Resource(name = "objectMapper")
    private ObjectMapper jsonMapper;

            
    @Override
    public LegacyMatchDataFeedDto fromJson(String json) throws BeanDeserializationException {
        
        if (StringUtils.isEmpty(json)) {
            throw new BeanDeserializationException(
                "Attempting to convert empty json to dto");
        }

        try {
        	
        	LegacyMatchDataFeedDto dto=  mapper.readValue(json, LegacyMatchDataFeedDto.class);            
            return dto;
            
        } catch (IOException e) {
            log.error("Failed to read JSON", e);
            throw new BeanDeserializationException("exception converting json to dto",
                e);
        }
    }
}
