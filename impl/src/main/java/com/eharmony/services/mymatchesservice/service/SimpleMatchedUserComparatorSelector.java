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
    @PostConstruct
    private void initialzeMap() {
        keyExtractorMap.put("userid", SimpleMatchedUserDto::getMatchUserId);
        keyExtractorMap.put("name", SimpleMatchedUserDto::getMatchUserFirstName);
        keyExtractorMap.put("age", SimpleMatchedUserDto::getAge);
    }
    public  Comparator<SimpleMatchedUserDto> selectComparator(String sortBy) {
        if (StringUtils.isEmpty(sortBy)) {
            return null;
        }
        Function<? super SimpleMatchedUserDto, ? extends Comparable> keyExtractor =  keyExtractorMap.get(sortBy);
        if (keyExtractor == null) {
            log.warn("unkown sortby crteria {}", sortBy);
            return null;
        }
        return Comparator.comparing(keyExtractor);
    }
}
