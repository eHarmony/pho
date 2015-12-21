package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.codahale.metrics.Timer;
import com.eharmony.matching.common.value.MatchScoreProtoBuffs.PairingPurposeProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PairingRelaxTypeProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PairingsProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PartialPairingProto;
import com.eharmony.matching.common.value.ScoredUserListProtoBuffs.ScoredUserListProto;
import com.eharmony.matching.common.value.ScoredUserProtoBuffs.ScoredUserProto;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.client.ScoreServiceClient;
import com.eharmony.services.mymatchesservice.service.transform.IMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;

/**
 * Takes the matches from the feed, sends it over to the Scorer Service and
 * enriches the feed data with the Score of each match.
 * 
 * @author esrinivasan
 *
 */
public class MatchScoreEnricher  implements IMatchFeedTransformer {

	private static final Logger logger = LoggerFactory.getLogger(MatchScoreEnricher.class);

	private ScoreServiceClient scoreServiceClient;
	

	public MatchScoreEnricher(ScoreServiceClient scoreServiceClient) {

		Assert.notNull(scoreServiceClient, "Missing client bean");
		this.scoreServiceClient = scoreServiceClient;
		
	}

	@Override
	public MatchFeedRequestContext processMatchFeed(MatchFeedRequestContext context) {

		try {

			if (context == null) {

				logger.warn("Match feed context is null, returning without processing. Context={}", context);
				return context;

			}

			if (context.getLegacyMatchDataFeedDto() == null) {

				logger.warn("LegacyMatchDataFeedDto is null, returning without processing. Context={}", context);
				return context;

			}

			Map<String, Map<String, Map<String, Object>>> matchesFromFeed = context.getLegacyMatchDataFeedDto().getMatches();
			
			if (matchesFromFeed == null || MapUtils.isEmpty(matchesFromFeed)) {

				logger.warn("No matches found in the feed data");
				return context;

			}
			

			List<PartialPairingProto> partialPairingsList = prepareMatchesForScoring(matchesFromFeed);
				
			ScoredUserListProto scoredListProto = scoreMatches(partialPairingsList, context.getUserId());

			if (scoredListProto != null && CollectionUtils.isNotEmpty(scoredListProto.getScoredUsersList())) {
				
				updateMatchesWithScore(matchesFromFeed, scoredListProto.getScoredUsersList());	
				
			}
			
		} catch (Exception e) {
			
			logger.error("Unable to enrich the feed with the score", e);
		
		}
		
		return context;

	}
	
	
	/**
	 * Creates a list of partial pairing protos from the match feed.
	 * 
	 * @param matchesFromFeed Collection of matches from feed.
	 * @return List of partial parings proto
	 */
	private List<PartialPairingProto> prepareMatchesForScoring(Map<String, Map<String, Map<String, Object>>> matchesFromFeed) {
		
		List<PartialPairingProto> partialPairingsProtoList  = new ArrayList();
		
		matchesFromFeed.entrySet().forEach(entryItem -> {
		
			Map<String, Map<String, Object>> matchInfo = entryItem.getValue();

			Map<String, Object> matchSection = matchInfo.get(MatchFeedModel.SECTIONS.MATCH);
			Integer candidateId = (Integer) matchSection.get(MatchFeedModel.MATCH.MATCHEDUSERID);
			
			PairingRelaxTypeProto userRelaxType = PairingRelaxTypeProto.RELAXED;

			Optional<Object> userRelaxTypeOptional = Optional.ofNullable(matchSection.get(MatchFeedModel.MATCH.RELAXED));
			if (userRelaxTypeOptional.isPresent()) {
			
				userRelaxType = (Boolean) userRelaxTypeOptional.get() ? PairingRelaxTypeProto.RELAXED : PairingRelaxTypeProto.STRICT;
			
			}

			PartialPairingProto.Builder pppBuilder = PartialPairingProto.newBuilder();
			pppBuilder.setCandId(candidateId);
			pppBuilder.setCandRelaxedState(PairingRelaxTypeProto.STRICT);  // Explicitly setting the value to STRICT as the feed does not have information on candidate relax type.
			//Based on conversation with matching team. The relax types are not used in What-If model. So, hard coding candidate relax state to STRICT is fine.
			pppBuilder.setUserRelaxedState(userRelaxType);
			partialPairingsProtoList.add(pppBuilder.build());
		});
		
		return partialPairingsProtoList;
	}
	
	
	/**
	 * Sends the matches over to the scorer service to be scored.
	 * 
	 * @param partialPairingsList  List of partial pairings proto which contain match information
	 * @param userId  Id of the user
	 * @return ScoredUserListProto
	 */
	private ScoredUserListProto scoreMatches(List<PartialPairingProto> partialPairingsList, Long userId) {
		
		PairingsProto.Builder pairingBuilder = PairingsProto.newBuilder();
		
		pairingBuilder.setUserId(userId.intValue());
		
		pairingBuilder.addAllCandidates(partialPairingsList);
		
		ScoredUserListProto scoredListProto = null;
		
		long startTime = System.currentTimeMillis();
		String timerName = getClass().getCanonicalName() + ".scoreMatches" ;
    	Timer.Context t = GraphiteReportingConfiguration.getRegistry().timer(timerName).time();

		try {
		
			//Get score based on What-If (KISMET) model.
		    scoredListProto = scoreServiceClient.scoreMatches(userId, PairingPurposeProto.KISMET, pairingBuilder.build());
		    t.stop();
		    return scoredListProto;

		} catch (Exception e) {
			
			logger.error("Problem interacting with pairing scorer service for user - {}", userId, e);
			return scoredListProto; 
		
		} finally {
			  
			t.stop();
			long endTime = System.currentTimeMillis();
			logger.info("Total Score matches for  user {} is {} MS", userId, (endTime - startTime));
	
		  }
		
	  }
	
	
	/**
	 * Take a list of scored users and update update the match information in the feed with the score.
	 *  
	 * @param matchesFromFeed  Feed of matches
	 * @param scoredUserProtoList  List of scored user protos
	 */
	private void updateMatchesWithScore(Map<String, Map<String, Map<String, Object>>> matchesFromFeed, List<ScoredUserProto> scoredUserProtoList){

		scoredUserProtoList.forEach(scoredUserProto -> {

			Map<String, Map<String, Object>> matchInfo = getMatchForUserId(scoredUserProto.getUserId(), matchesFromFeed);
			
			if (matchInfo !=null) {

				Map<String, Object> match = matchInfo.get(MatchFeedModel.SECTIONS.MATCH);
				Integer candidateId = (Integer) match.get(MatchFeedModel.MATCH.MATCHEDUSERID);
				logger.info("CandidateId - {}, Score -{}", candidateId, scoredUserProto.getScore());
				//Update match map with score so that matches can be ordered on basis of score before returning back to client.
				match.put(MatchFeedModel.MATCH.MATCH_ATTRACTIVENESS_SCORE, scoredUserProto.getScore());

			}

		});
	}
	
	

	/**
	 * Takes the match feed and retrieves the match related to a particular
	 * candidate.
	 * 
	 * @param userId This will be userId of the match.
	 * @param matchesFeed Feed of matches
	 * @return Map of match sections and corresponding information map
	 */
	private Map<String, Map<String, Object>> getMatchForUserId(int userId, Map<String, Map<String, Map<String, Object>>> matchesFeed) {
			
		for (Map<String, Map<String, Object>> itemValue : matchesFeed.values()) {

			Map<String, Object> matchSection = itemValue.get(MatchFeedModel.SECTIONS.MATCH);

			int candidateId = (Integer) matchSection.get(MatchFeedModel.MATCH.MATCHEDUSERID);

			if (candidateId == userId) {
			
				return itemValue;
			
			}
	    }
		
		return null;
	}
}
