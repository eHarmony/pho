package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;


public class FieldSelectorEnricher extends AbstractMatchFeedTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(FieldSelectorEnricher.class);
	
    private List<String> list;
    private SelectionListType listType;
    private String sectionName;

    public enum SelectionListType {
    	BLACKLIST,
        WHITELIST;
    }
    
    public FieldSelectorEnricher( String sectionName, List<String> list, String listType) {
        Assert.hasText(sectionName, "sectionName parameter cannot be blank");
        Assert.notEmpty(list, "list parameter cannot be empty");
        Assert.notNull(listType, "listType parameter cannot be empty");

        this.list = list;
        this.listType = SelectionListType.valueOf(listType);
        this.sectionName = sectionName;
    }

    @Override
    protected String getMatchSectionName() {
        return sectionName;
    }

    @Override
    protected boolean processMatchSection(Map<String, Object> section,
    									MatchFeedRequestContext context) {
 
        if (section == null) {
        	logger.warn("match info section={} is null for matchInfo={}",
                sectionName, context.getLegacyMatchDataFeedDto());

            return true; // the whole section is not present, hence cleaning is success
        }

        if (listType == SelectionListType.WHITELIST) {
            processWhiteList(section);
        } else {
            processBlackList(section);
        }

        logger.debug("{} removed extra fields from MatchInfo for userId={}, section={}",
           listType, context.getUserId(), sectionName);

        return true;
    }

    private void processBlackList(Map<String, Object> section) {
        // remove blacklisted fields
        for (String fieldName : list) {
            section.remove(fieldName);
        }
    }

    private void processWhiteList(Map<String, Object> section) {
        // filter out section fields
        Iterator<String> it = section.keySet().iterator();

        while (it.hasNext()) {
            String fieldName = it.next();

            if (!list.contains(fieldName)) {
                it.remove();
            }
        }
    }
   
}
