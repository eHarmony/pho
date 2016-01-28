package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.CommunicationStage;
import com.eharmony.services.mymatchesservice.service.CommunicationStageResolver;
import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

public class CommStageEncricher extends AbstractMatchFeedTransformer {

	private static final Logger logger = LoggerFactory.getLogger(CommStageEncricher.class);
	@Resource
	private CommunicationStageResolver commStageResolver;

	@Override
	protected boolean processMatch(Map<String, Map<String, Object>> match, MatchFeedRequestContext context) {

		Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);
		Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
		long userId = context.getUserId();
		Object matchStage = matchSection.get(MatchFeedModel.MATCH.STAGE);
		if (matchStage != null) {
			CommunicationStage commStage = commStageResolver.resolveCommStage(Integer.parseInt(matchStage.toString()));
			commSection.put(MatchFeedModel.COMMUNICATION.SECTION, commStage.getSectionId());
			commSection.put(MatchFeedModel.COMMUNICATION.SUB_SECTION, commStage.getSubSectionId());
			matchSection.remove(MatchFeedModel.MATCH.STAGE);
			if (logger.isDebugEnabled()) {
	            logger.debug("Enriched comm section={} , subsection={} for userId={}",
	                      commStage.getSectionId(), commStage.getSubSectionId(), userId );
	        }
		}
		return true;
	}
}
