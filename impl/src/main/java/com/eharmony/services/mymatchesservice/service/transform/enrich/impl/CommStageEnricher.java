package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.CommunicationStage;
import com.eharmony.services.mymatchesservice.service.CommunicationStageResolver;
import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.IMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.singles.common.status.MatchStatusUtilities;

public class CommStageEnricher extends AbstractMatchFeedTransformer  implements IMatchTransformer{

	private static final Integer DEFAULT_MATCH_STAGE = 0;
	
	private static final Logger logger = LoggerFactory.getLogger(CommStageEnricher.class);
	@Resource
	private CommunicationStageResolver commStageResolver;

	@Override
	protected boolean processMatch(Map<String, Map<String, Object>> match, MatchFeedRequestContext context) {

		long userId = context.getUserId();
		return processMatch(match, userId);
	}
	
	private boolean processMatch(Map<String, Map<String, Object>> match, long userId) {

		Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		Object matchStage = matchSection.get(MatchFeedModel.MATCH.STAGE);
		if(matchStage == null){
			
			matchStage = DEFAULT_MATCH_STAGE;
			matchSection.put(MatchFeedModel.MATCH.STAGE, matchStage );
			
		}
		
		logger.debug("Match Stage is {}", matchStage);
	
		CommunicationStage commStage = commStageResolver.resolveCommStage(Integer.parseInt(matchStage.toString()));
		commSection.put(MatchFeedModel.COMMUNICATION.SECTION, commStage.getSectionId());
		commSection.put(MatchFeedModel.COMMUNICATION.SUB_SECTION, commStage.getSubSectionId());
		
		String turnOwner = getTurnOwner(match, commStage);
		commSection.put(MatchFeedModel.COMMUNICATION.STATUS, turnOwner);
		matchSection.put(MatchFeedModel.MATCH.STATUS, turnOwner);
		
		logger.debug("Enriched comm section={} , subSection={} , status={} for userId={}", commStage.getSectionId(),
				commStage.getSubSectionId(), commSection.get(MatchFeedModel.COMMUNICATION.STATUS), userId);


		return true;	
	}

	@Override
	public SingleMatchRequestContext processSingleMatch(
			SingleMatchRequestContext context) {

		if(context.matchIsAvailable()){
			long userId = context.getQueryContext().getUserId();
			processMatch(context.getSingleMatch(), userId);
		}
		return context;
	}

	private String getTurnOwner(Map<String, Map<String, Object>> match, CommunicationStage commStage) {
		Map<String, Object> matchStatusCallParams = new HashMap<>();
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);

		matchStatusCallParams.put("closedStatus", matchSection.get(MatchFeedModel.MATCH.CLOSED_STATUS));
		matchStatusCallParams.put("initializer", matchSection.get(MatchFeedModel.MATCH.INITIALIZER));
		matchStatusCallParams.put("isUser", matchSection.get(MatchFeedModel.MATCH.IS_USER));
		matchStatusCallParams.put("archiveStatus", matchSection.get(MatchFeedModel.MATCH.ARCHIVE_STATUS));
		matchStatusCallParams.put("commLastSent", commSection.get(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE));
		matchStatusCallParams.put("fastTrackStatus", matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STATUS));
		matchStatusCallParams.put("turnOwner", matchSection.get(MatchFeedModel.MATCH.TURN_OWNER));
		matchStatusCallParams.put("userId", matchSection.get(MatchFeedModel.MATCH.USER_ID));
		matchStatusCallParams.put("stage", commStage.getWorkflowId());
		matchStatusCallParams.put("matchId", matchSection.get(MatchFeedModel.MATCH.ID));
		String turnOwner = MatchStatusUtilities.getStatus(matchStatusCallParams);
		
		return turnOwner;
	}
}
