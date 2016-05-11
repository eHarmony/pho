package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.eharmony.protorest.RestClient;
import com.eharmony.services.communication.CommunicationDto;
import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class SingleMatchCommStateEnricher implements IMatchTransformer {

	private static final Logger logger = LoggerFactory.getLogger(SingleMatchCommStateEnricher.class);
	
	private static final String GET_MATCH_STATE_PATH = "communication/v1/matches/%s";

	
	@Resource(name="commServiceRestClient")
	private RestClient commServiceRestClient;
	
	@Value("${comm.service.url}")
	private String commServiceUrl;

	@Override
	public SingleMatchRequestContext processSingleMatch(
			SingleMatchRequestContext context) {

		long matchId = context.getQueryContext().getMatchId();
		Map<String, Map<String, Object>> match = context.getSingleMatch();
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);
		
		String url = String.format(commServiceUrl.concat("/").concat(GET_MATCH_STATE_PATH), matchId);
		CommunicationDto commState = commServiceRestClient.get(url, CommunicationDto.class);
		
		if(commState == null){
			logger.debug("No comm state found for matchId {}", context.getQueryContext().getMatchId());
			setDefaultMatchStateValuesIfNotAlreadySet(matchSection);
			setDefaultCommStateValuesIfNotAlreadySet(commSection);
			return context;
		}
		
		matchSection.put(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE, commState.getUserChooseMhcs());
		matchSection.put(MatchFeedModel.MATCH.COMM_STARTED_DATE, commState.getCommStartedDate());
		matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, commState.getFastTrackAvailable().toInt());
		matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STAGE, commState.getFastTrackStage());
		matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STATUS, commState.getFastTrackStatus().toInt());
		
		if(commState.getCommLastSentByUser().after(commState.getCommLastSentByCandidate())){
			commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, commState.getCommLastSentByUser());
		}else{
			commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, commState.getCommLastSentByCandidate());
		}
		
		boolean isUser = (Boolean) matchSection.get(MatchFeedModel.MATCH.IS_USER);
		mapFieldsByOrientation(isUser, matchSection, commSection, commState);
		
		return context;
	}
	
	private void mapFieldsByOrientation(boolean isUser, 
										Map<String, Object> matchSection, 
										Map<String, Object> commSection, 
										CommunicationDto commState){
		
		if(isUser){
			matchSection.put(MatchFeedModel.MATCH.MATCH_COMM_LAST_SENT, commState.getCommLastSentByCandidate());
			matchSection.put(MatchFeedModel.MATCH.COMM_LAST_SENT, commState.getCommLastSentByUser());
		}else{
			matchSection.put(MatchFeedModel.MATCH.COMM_LAST_SENT, commState.getCommLastSentByCandidate());
			matchSection.put(MatchFeedModel.MATCH.MATCH_COMM_LAST_SENT, commState.getCommLastSentByUser());			
		}
	}
	
	private void setDefaultMatchStateValuesIfNotAlreadySet(Map<String, Object> matchSection){
		
		if(matchSection.get(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE) == null) 
			matchSection.put(MatchFeedModel.MATCH.CHOOSE_MHCS_DATE, null);
		
		if(matchSection.get(MatchFeedModel.MATCH.COMM_STARTED_DATE) == null) 
			matchSection.put(MatchFeedModel.MATCH.COMM_STARTED_DATE, null);
		
		if(matchSection.get(MatchFeedModel.MATCH.DISPLAY_TAB) == null) 
			matchSection.put(MatchFeedModel.MATCH.DISPLAY_TAB, 1);
		
		if(matchSection.get(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB) == null) 
			matchSection.put(MatchFeedModel.MATCH.MATCH_DISPLAY_TAB, 1);
		
		if(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE) == null) 
			matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_AVAILABLE, null);
		
		if(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STAGE) == null) 
			matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STAGE, null);
		
		if(matchSection.get(MatchFeedModel.MATCH.FAST_TRACK_STATUS) == null) 
			matchSection.put(MatchFeedModel.MATCH.FAST_TRACK_STATUS, null);
		
		if(matchSection.get(MatchFeedModel.MATCH.STATUS) == null) 
			matchSection.put(MatchFeedModel.MATCH.STATUS, "new");
		
		if(matchSection.get(MatchFeedModel.MATCH.MATCH_COMM_LAST_SENT) == null) 
			matchSection.put(MatchFeedModel.MATCH.MATCH_COMM_LAST_SENT, null);
		
		if(matchSection.get(MatchFeedModel.MATCH.COMM_LAST_SENT) == null) 
			matchSection.put(MatchFeedModel.MATCH.COMM_LAST_SENT, null);
	}

	private void setDefaultCommStateValuesIfNotAlreadySet(Map<String, Object> commSection){
		
		if(commSection.get(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE) != null)
			commSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, null);
	}
}
