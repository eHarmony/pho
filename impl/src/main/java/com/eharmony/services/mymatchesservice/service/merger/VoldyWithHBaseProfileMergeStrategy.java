package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

public class VoldyWithHBaseProfileMergeStrategy extends LegacyMatchDataFeedMergeStrategy {

	private MatchDataFeedStore 		voldemortStore;
	private MatchStoreQueryRepository repository;
	
	private Logger log = LoggerFactory.getLogger(VoldyWithHBaseProfileMergeStrategy.class);
	
	public VoldyWithHBaseProfileMergeStrategy(
			MatchDataFeedStore voldemortStore,
			MatchStoreQueryRepository repository) {
		
		this.voldemortStore = voldemortStore;
		this.repository = repository;
	}

	@Override
	public LegacyMatchDataFeedDto merge(MatchDataFeedQueryRequest request) {
		
		Set<MatchDataFeedItemDto> hbaseFeed = null;
		
		try{
			hbaseFeed = repository.getMatchDataFeed(request);

		}catch(Exception ex){			
			// HBase unavailable, use only Voldy.
			log.warn("error accessing HBase repository, proceeding without: {}", ex.getMessage());
		}
		
		String userId = Integer.toString(request.getUserId());		
		LegacyMatchDataFeedDto voldyFeed =  voldemortStore.getMatches(userId);
		
		if(hbaseFeed != null){
			Map<String, Map<String,  Map<String, Object>>> matches = voldyFeed.getMatches();
			
			mergeHBaseProfileIntoMatchFeed(matches, hbaseFeed);
		}
		
		return voldyFeed;
	}
	
	private static final String MATCHINFOMODEL_MATCH_PROFILE = "matchedUser";

	private void mergeHBaseProfileIntoMatchFeed(    Map<String, Map<String,  Map<String, Object>>> matches,
													Set<MatchDataFeedItemDto> hbaseFeed) {
		
		for(MatchDataFeedItemDto hbaseMatch : hbaseFeed){
			
			// find this match in feed...
			String matchId = Long.toString(hbaseMatch.getMatch().getMatchId());
			Map<String,  Map<String, Object>> feedMatch = matches.get(matchId);
			if(feedMatch == null){
				
				log.warn("HBase match {} not found in feed", matchId);
				continue;
				// TODO: what does it mean if feed is missing here?
			}
			
			// get feed profile
			Map<String, Object> feedProfile = feedMatch.get(MATCHINFOMODEL_MATCH_PROFILE);

			// overwrite feed with HBase values
			MatchProfileElement profile = hbaseMatch.getMatchedUser();
			feedProfile.put("age", profile.getAge());
			feedProfile.put("city", profile.getCity());
			feedProfile.put("country", profile.getCountry());
			feedProfile.put("firstName", profile.getFirstName());
			feedProfile.put("gender", profile.getGender());
			feedProfile.put("stateCode", profile.getStateCode());
			feedProfile.put("userId", profile.getUserId());
			feedProfile.put("version", profile.getVersion());
			feedProfile.put("birthdate", profile.getBirthdate());

		}
	}
}
