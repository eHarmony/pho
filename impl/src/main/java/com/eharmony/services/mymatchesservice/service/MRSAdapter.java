package com.eharmony.services.mymatchesservice.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eharmony.protorest.RestClient;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.eharmony.services.mymatchesservice.store.dao.MatchSummaryDao;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;

@Component("mrsAdapter")
public class MRSAdapter{
	
	@Value("${mrs.host}")
	private String mrsHost;
	
	@Value("${mrs.port}")
	private String mrsPort;
	
	@Resource(name="restClient")
	private RestClient restClient;
	
	@Resource
	private MatchSummaryDao matchSummaryDao;
	
	private static final Logger logger= LoggerFactory.getLogger(MRSAdapter.class);
	
	private static final String MRS_URL = "http://%s:%s/matching-v1/match/%s";
	
	@Transactional
	public LegacyMatchDataFeedDtoWrapper getSingleUserMatchSafe(
			BasicStoreFeedRequestContext request) {

		
    	long userId = request.getMatchFeedQueryContext().getUserId();
		long matchId = request.getMatchFeedQueryContext().getMatchId();
		
		logger.info("Fetch match {} from MRS", matchId);

		String url = String.format(MRS_URL, mrsHost, mrsPort, matchId);
		
		// TODO: parallelize these 2 calls.
		MRSDto mrsMatch = restClient.get(url, MRSDto.class);
		MatchSummaryDo comm = matchSummaryDao.findMatchSummaryByUserAndMatch(userId, matchId);

		if(mrsMatch != null && comm != null){
			LegacyMatchDataFeedDtoWrapper result = new LegacyMatchDataFeedDtoWrapper(userId);
			LegacyMatchDataFeedDto dto = new LegacyMatchDataFeedDto();
			
			Map<String, Map<String, Map<String, Object>>> oneMatch = new HashMap<>();
			buildMatchFromMRSDto(oneMatch, matchId, mrsMatch);
			buildCommFromMatchSummaries(oneMatch, matchId, comm);
			
			dto.setMatches(oneMatch);
			
			return result;
		}
		
		logger.warn("No match in MRS for matchId {} userId {}", matchId, userId);
		return null;
	}

	private void buildMatchFromMRSDto(
			Map<String, Map<String, Map<String, Object>>> oneMatch, long matchId,
															MRSDto matchDto) {
		
		Map<String, Map<String, Object>> oneMatchSection = new HashMap<>();
		
		Map<String, Object> matchSection = new HashMap<>();
		matchSection.put(MatchFeedModel.MATCH.MATCHEDUSERID, matchDto.getMatchedUserId());
		matchSection.put(MatchFeedModel.MATCH.CLOSED_STATUS, matchDto.getCloseFlag());
		matchSection.put(MatchFeedModel.MATCH.DELIVERED_DATE, matchDto.getDeliveryDate());
		matchSection.put(MatchFeedModel.MATCH.ONE_WAY_STATUS, matchDto.getOneWay());
		matchSection.put(MatchFeedModel.MATCH.USER_ID, matchDto.getUserId());
		matchSection.put(MatchFeedModel.MATCH.DISTANCE, matchDto.getDistance());
		
		oneMatchSection.put(MatchFeedModel.SECTIONS.MATCH, matchSection);
		
		oneMatch.put(Long.toString(matchId), oneMatchSection);
	}
	
	private void buildCommFromMatchSummaries(
			Map<String, Map<String, Map<String, Object>>> oneMatch, long matchId,
			MatchSummaryDo comm) {
		
		Map<String, Map<String, Object>> oneCommSection = new HashMap<>();
		
		Map<String, Object> matchSection = new HashMap<>();
		matchSection.put(MatchFeedModel.COMMUNICATION.LAST_COMM_DATE, comm.getLastCommDate());

		
		oneCommSection.put(MatchFeedModel.SECTIONS.COMMUNICATION, matchSection);
		
		oneMatch.put(Long.toString(matchId), oneCommSection);		
	}

}
