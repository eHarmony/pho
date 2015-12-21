package com.eharmony.services.mymatchesservice.service.client;

import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.eharmony.matching.common.value.MatchScoreProtoBuffs.PairingPurposeProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PairingRelaxTypeProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PairingsProto;
import com.eharmony.matching.common.value.PairingProtoBuffers.PairingProtos.PartialPairingProto;
import com.eharmony.matching.common.value.ScoredUserListProtoBuffs.ScoredUserListProto;
import com.eharmony.protorest.RestClientImpl;

@RunWith(MockitoJUnitRunner.class)
//@Ignore
public class ScoreServiceClientIntegrationTest {

	private String host = "r4-scorer-service.np.vip.dc1.eharmony.com";

	private String port = "80";

	@Test
	public void scoreMatchTest() throws MalformedURLException {

		RestClientImpl rc = new RestClientImpl(10, 6000, 6000, false);
		PairingsProto.Builder pairingBuilder = PairingsProto.newBuilder();
		pairingBuilder.setUserId(61841066);
		PartialPairingProto.Builder pppBuilder = PartialPairingProto.newBuilder();
		pppBuilder.setCandId(61841069);
		pppBuilder.setCandRelaxedState(PairingRelaxTypeProto.RELAXED);
		pppBuilder.setUserRelaxedState(PairingRelaxTypeProto.RELAXED);
		pairingBuilder.addCandidates(pppBuilder.build());
		pairingBuilder.build();
		ScoreServiceClient client = new ScoreServiceClient(rc, host, port);
		ScoredUserListProto result = client.scoreMatches((long) 61841066, PairingPurposeProto.KISMET,
				pairingBuilder.build());
		System.out.println("Result -> " + result.getScoredUsersList().get(0).getScore());
		assertFalse(result.getScoredUsersList().get(0).getScore() == 0);

	}

}
