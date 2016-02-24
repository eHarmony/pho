package com.eharmony.services.mymatchesservice.service.client;

import java.net.MalformedURLException;

import com.eharmony.matching.common.value.MatchScoreProtoBuffs.PairingPurposeProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PairingsProto;
import com.eharmony.matching.common.value.ScoredUserListProtoBuffs.ScoredUserListProto;
import com.eharmony.protorest.RestClient;

public class MockScoreServiceClient extends ScoreServiceClient{

	
	public MockScoreServiceClient(RestClient restClient,
			String scoreServiceHost, String scoreServicePort) {
		super(restClient, scoreServiceHost, scoreServicePort);
		// TODO Auto-generated constructor stub
	}
	

	public ScoredUserListProto scoreMatches(Long userId, PairingPurposeProto pairingPurpose, PairingsProto pairingsProto) throws MalformedURLException {
		
		ScoredUserListProto scoredUserList =   ScoredUserListProto.newBuilder().build();
		return scoredUserList;
	}
}
