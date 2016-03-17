package com.eharmony.services.mymatchesservice.service.client;

import java.net.MalformedURLException;

import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.matching.common.value.MatchScoreProtoBuffs.PairingPurposeProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PairingsProto;
import com.eharmony.matching.common.value.ScoredUserListProtoBuffs.ScoredUserListProto;
import com.eharmony.protorest.RestClient;

@Component("scoreServiceClient")
public class ScoreServiceClient {

	private RestClient restClient;

	private String scoreServiceHost;

	private String scoreServicePort;
	
	private final String SCORE_SERVICE_URL_TEMPLATE = "http://{host}:{port}/scorer-service/";

	private final String MATCH_ATTRACT_SCORE_PATH = "1.0/pairings/{pairingPurpose}/users/{userId}/score/";
	
	private static final Logger logger = LoggerFactory.getLogger(ScoreServiceClient.class);

	public ScoreServiceClient(RestClient restClient, String scoreServiceHost, String scoreServicePort) {
		
		this.restClient = restClient;
		this.scoreServiceHost = scoreServiceHost;
		this.scoreServicePort = scoreServicePort;
	
	}

	/**
	 * Send a list of matches as part of pairingsProto to scorer service to be evaluated and scored on the basis of attractiveness.
	 * 
	 * @param userId	User id of the logged in user
	 * @param pairingPurpose pairing purpose
	 * @param pairingsProto  Contains a list of user-matches wrapped in pairingsProto
	 * @return List Of scored users proto objects
	 * @throws MalformedURLException Throws a malformed url exception 
	 */
	public ScoredUserListProto scoreMatches(Long userId, PairingPurposeProto pairingPurpose, PairingsProto pairingsProto) throws MalformedURLException {
		
		String resolvedURL = JerseyUriBuilder.fromPath(SCORE_SERVICE_URL_TEMPLATE + MATCH_ATTRACT_SCORE_PATH)
					        .resolveTemplate("host", scoreServiceHost)
							.resolveTemplate("port", scoreServicePort)
							.resolveTemplate("pairingPurpose", pairingPurpose)
							.resolveTemplate("userId", userId).build().toURL().toString();		

		ScoredUserListProto scoredUserList =  restClient.post(resolvedURL, pairingsProto, ScoredUserListProto.class);
		logger.info("Result from Score service - {}", scoredUserList);
		return scoredUserList;
	}

}
