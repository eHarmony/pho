package com.eharmony.services.mymatchesservice.service.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import rx.Observable;

import com.eharmony.datastore.model.MatchCommunicationElement;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchElement;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.services.mymatchesservice.service.BasicStoreFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedResponse;
import com.eharmony.services.mymatchesservice.service.RedisStoreFeedService;
import com.eharmony.services.mymatchesservice.service.transform.LegacyMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;


public class MockRedisStoreFeedServiceImpl implements RedisStoreFeedService {

    private static final Logger log = LoggerFactory.getLogger(MockRedisStoreFeedServiceImpl.class);

	@Override
	public Observable<RedisStoreFeedResponse> getUserMatchesSafe(
			BasicStoreFeedRequestContext request) {

		// TODO: integrate with Redis DataStoreApi
		log.warn("REDIS STORE NOT YET IMPLEMENTED, RETURNING MOCK DATA FOR R3 USERID=63177222 MATCHID=11790513735");
		
		Set<MatchDataFeedItemDto> mockData = new HashSet<MatchDataFeedItemDto>();
		MatchDataFeedItemDto mockItem = new MatchDataFeedItemDto();
		MatchCommunicationElement comm = mockItem.getCommunication();
		comm.setViewedProfile(true);
		comm.setLastCommDate(new Date());

		long matchId = 11790513735L;
		
		MatchElement match = mockItem.getMatch();
		match.setDeliveredDate(new Date());
		match.setRelaxed(1);
		match.setMatchId(matchId);
		match.setStatus(28);
		match.setDistance(30);
		match.setLastModifiedDate(new Date());
		
		MatchProfileElement profile = mockItem.getMatchedUser();
		profile.setCountry(1);
		profile.setFirstName("Matcheduser6");
		profile.setGender(2);
		profile.setCity("Los Angeles");
		profile.setStateCode("CA");
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.YEAR, -35);
		profile.setBirthdate(cal.getTime());
		
		mockData.add(mockItem);
		
		LegacyMatchFeedTransformer transformer = new LegacyMatchFeedTransformer();
		LegacyMatchDataFeedDto mockDto = transformer.transform(mockData, 63177222L, "en_GB");
		
		mockDto.getMatches().get(String.valueOf(matchId)).get(MatchFeedModel.SECTIONS.MATCH).put("updatedAt", String.valueOf(System.currentTimeMillis()));
		
		RedisStoreFeedResponse response = new RedisStoreFeedResponse();
		response.setDataAvailable(true);
		response.setRedisStoreFeedDto(mockDto);
		
		return Observable.just(response);
	}

}
