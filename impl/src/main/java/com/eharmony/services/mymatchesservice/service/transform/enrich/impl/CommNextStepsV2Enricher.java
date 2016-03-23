package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.communication.MapBackedMatchImpl;
import com.eharmony.communication.Match;
import com.eharmony.communication.nextcommunicationaction.NextCommunicationAction;
import com.eharmony.communication.nextcommunicationaction.NextCommunicationActionService;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

/**
 * Enriches the match feed with information about the next action suggested to
 * advance communication with this match
 * 
 * @author aricheimer
 *
 */
public class CommNextStepsV2Enricher
        extends AbstractMatchFeedTransformer {

    private static final Logger logger = LoggerFactory
            .getLogger(CommNextStepsV2Enricher.class);

    @Resource
    private NextCommunicationActionService nextCommunicationActionService;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean processMatch(Map<String, Map<String, Object>> matchMap,
            MatchFeedRequestContext context) {
        
        if(!context.isUseV2CommNextSteps()){
            // Using V1, so this can be skipped
            return true;
        }

        Map<String, Object> commSection = matchMap
                .get(MatchFeedModel.SECTIONS.COMMUNICATION);

        long userId = context.getUserId();

        // Only add if it does not already exist, or if it contains action, which means this is a V1 Object
        if (commSection.get(MatchFeedModel.COMMUNICATION.NEXT_STEP) == null 
                || ((Map<String, Object>) commSection.get(MatchFeedModel.COMMUNICATION.NEXT_STEP)).containsKey(MatchFeedModel.COMMUNICATION.MESSAGE)) {

            Match match = new MapBackedMatchImpl(
                    matchMap.get(MatchFeedModel.SECTIONS.MATCH));
            NextCommunicationAction nextCommunicationAction = nextCommunicationActionService
                    .determineNextCommunicationAction(match);

            Map<String, Object> nextCommunicationActionData = new HashMap<String, Object>();

            nextCommunicationActionData.put(MatchFeedModel.COMMUNICATION.ACTION,
                    nextCommunicationAction.getAction());
            nextCommunicationActionData.put(MatchFeedModel.COMMUNICATION.STEP,
                    nextCommunicationAction.getStep());

            // finally, add the next steps to the communication section
            commSection.put(
                    MatchFeedModel.COMMUNICATION.NEXT_STEP,
                    nextCommunicationActionData);

            logger.debug(
                    "Enriched comm next communication step data={} for userId={} and matchInfo={}",
                    nextCommunicationActionData, userId, matchMap);

        }

        return true;
    }

}
