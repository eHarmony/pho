package com.eharmony.services.mymatchesservice.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;

@Component
public class MatchStatusGroupResolver {

    private static final Logger logger = LoggerFactory.getLogger(MatchStatusGroupResolver.class);

    public Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> buildMatchesStatusGroups(long userId, Set<String> statuses) {
        Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> statusGroups = new HashMap<MatchStatusGroupEnum, Set<MatchStatusEnum>>();

        if (CollectionUtils.isNotEmpty(statuses)) {
            for (String status : statuses) {
                MatchStatusEnum matchStatus = MatchStatusEnum.fromName(status);
                if (matchStatus == null) {
                    if (status.equalsIgnoreCase("all")) {
                        populateMapWithAllStatuses(statusGroups);
                        break;
                    }
                    logger.warn("Requested match status {} is not valid", status);
                    continue;
                }
                switch (matchStatus) {
                case NEW:
                    Set<MatchStatusEnum> newMmatchStuses = statusGroups.get(MatchStatusGroupEnum.NEW);
                    if (CollectionUtils.isEmpty(newMmatchStuses)) {
                        newMmatchStuses = new HashSet<MatchStatusEnum>();
                    }
                    newMmatchStuses.add(matchStatus);
                    statusGroups.put(MatchStatusGroupEnum.NEW, newMmatchStuses);
                    break;
                case ARCHIVED:
                    Set<MatchStatusEnum> archiveMatchStuses = statusGroups.get(MatchStatusGroupEnum.ARCHIVE);
                    if (CollectionUtils.isEmpty(archiveMatchStuses)) {
                        archiveMatchStuses = new HashSet<MatchStatusEnum>();
                    }
                    archiveMatchStuses.add(matchStatus);
                    statusGroups.put(MatchStatusGroupEnum.ARCHIVE, archiveMatchStuses);
                    break;
                case OPENCOMM:
                case MYTURN:
                case THEIRTURN:
                    Set<MatchStatusEnum> commMatchStuses = statusGroups.get(MatchStatusGroupEnum.COMMUNICATION);
                    if (CollectionUtils.isEmpty(commMatchStuses)) {
                        commMatchStuses = new HashSet<MatchStatusEnum>();
                    }
                    commMatchStuses.add(matchStatus);
                    statusGroups.put(MatchStatusGroupEnum.COMMUNICATION, commMatchStuses);
                    break;
                case CLOSED:
                    logger.warn("Closed matches are not supported in this system...");
                    break;
                }
            }
        }

        if (MapUtils.isEmpty(statusGroups)) {
            logger.warn("feed request for user {} doesn't contain any status, returning all statuses...", userId);
            populateMapWithAllStatuses(statusGroups);
        }
        return statusGroups;
    }
    
    public static void populateSetWithAllStatuses(Set<String> statuses){
    	
    	statuses.clear();
    	
    	statuses.add(MatchStatusEnum.NEW.getName().toLowerCase());
    	statuses.add(MatchStatusEnum.MYTURN.getName().toLowerCase());
    	statuses.add(MatchStatusEnum.THEIRTURN.getName().toLowerCase());
    	statuses.add(MatchStatusEnum.OPENCOMM.getName().toLowerCase());
    	statuses.add(MatchStatusEnum.ARCHIVED.getName().toLowerCase());
    }

    //TODO make it as static map for all status -VIJAY
    private static void populateMapWithAllStatuses(Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> statusGroups) {
        Set<MatchStatusEnum> newMatchStuses = new HashSet<MatchStatusEnum>();
        Set<MatchStatusEnum> commMatchStuses = new HashSet<MatchStatusEnum>();
        Set<MatchStatusEnum> archiveMatchStuses = new HashSet<MatchStatusEnum>();
        newMatchStuses.add(MatchStatusEnum.NEW);
        commMatchStuses.add(MatchStatusEnum.MYTURN);
        commMatchStuses.add(MatchStatusEnum.THEIRTURN);
        commMatchStuses.add(MatchStatusEnum.OPENCOMM);
        archiveMatchStuses.add(MatchStatusEnum.ARCHIVED);
        statusGroups.put(MatchStatusGroupEnum.NEW, newMatchStuses);
        statusGroups.put(MatchStatusGroupEnum.COMMUNICATION, commMatchStuses);
        statusGroups.put(MatchStatusGroupEnum.ARCHIVE, archiveMatchStuses);

    }
}
