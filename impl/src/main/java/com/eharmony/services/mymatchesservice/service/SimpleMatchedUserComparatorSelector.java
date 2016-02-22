package com.eharmony.services.mymatchesservice.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SimpleMatchedUserComparatorSelector {
    private static final Logger log = LoggerFactory.getLogger(SimpleMatchedUserComparatorSelector.class);
    
    static Map<String, Function<? super SimpleMatchedUserDto, ? extends Comparable>> keyExtractorMap = new HashMap<>();
    
    private static final String MATCH_ID_CRITERIA = "matchid";
    private static final String MATCHED_USER_ID_CRITERIA ="matcheduserid" ;
    private static final String NAME_CRITERIA = "name";
    private static final String AGE_CRITERIA = "age";
    private static final String DELIVERED_DATE_CRITERIA = "deliveredDate";
    
    @PostConstruct
    private void initialzeMap() {
        keyExtractorMap.put(MATCH_ID_CRITERIA, SimpleMatchedUserDto::getMatchId);
        keyExtractorMap.put(NAME_CRITERIA, SimpleMatchedUserDto::getMatchedUserFirstName);
        keyExtractorMap.put(AGE_CRITERIA, SimpleMatchedUserDto::getAge);
        keyExtractorMap.put(DELIVERED_DATE_CRITERIA, SimpleMatchedUserDto::getDeliveredDate);
        keyExtractorMap.put(MATCHED_USER_ID_CRITERIA, SimpleMatchedUserDto::getMatchedUserId);
        
    }
    public  Comparator<SimpleMatchedUserDto> selectComparator(String sortBy) {
        if (StringUtils.isEmpty(sortBy)) {
            return null;
        }
        Function<? super SimpleMatchedUserDto, ? extends Comparable> keyExtractor =  keyExtractorMap.get(sortBy);
        if (keyExtractor == null) {
            log.warn("unkown sortBy criteria {}", sortBy);
            return null;
        }
        return Comparator.comparing(keyExtractor);
    }
}
