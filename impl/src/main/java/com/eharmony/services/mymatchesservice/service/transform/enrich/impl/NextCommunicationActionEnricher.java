package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction.NextCommunicationAction;
import com.eharmony.services.mymatchesservice.service.transform.enrich.impl.nextcommaction.NextCommunicationActionService;
import com.eharmony.singles.common.communication.MatchMapWrapper;
import com.eharmony.singles.common.communication.MatchWrapper;

public class NextCommunicationActionEnricher
        extends AbstractMatchFeedTransformer {

    private static final Logger logger = LoggerFactory
            .getLogger(NextCommunicationActionEnricher.class);

    @Resource
    private NextCommunicationActionService nextCommunicationActionService;

    @Override
    protected boolean processMatch(Map<String, Map<String, Object>> match,
            MatchFeedRequestContext context) {

        Map<String, Object> commSection = match
                .get(MatchFeedModel.SECTIONS.COMMUNICATION);

        long userId = context.getUserId();

        if (commSection.get(
                MatchFeedModel.COMMUNICATION.NEXT_COMMUNICATION_ACTION) == null) {

            MatchWrapper matchWrapper = new MatchMapWrapper(
                    match.get(MatchFeedModel.SECTIONS.MATCH));
            NextCommunicationAction nextCommunicationAction = nextCommunicationActionService
                    .determineNextCommunicationAction(matchWrapper);

            Map<String, Object> nextCommunicationActionData = new HashMap<String, Object>();

            nextCommunicationActionData.put(MatchFeedModel.COMMUNICATION.ACTION,
                    nextCommunicationAction.getAction());
            nextCommunicationActionData.put(MatchFeedModel.COMMUNICATION.AREA,
                    nextCommunicationAction.getArea());

            // finally, add the next steps to the communication section
            commSection.put(
                    MatchFeedModel.COMMUNICATION.NEXT_COMMUNICATION_ACTION,
                    nextCommunicationActionData);

            logger.debug(
                    "Enriched comm next communication action data={} for userId={} and matchInfo={}",
                    nextCommunicationActionData, userId, match);

        }

        return true;
    }

}
