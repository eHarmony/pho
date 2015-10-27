package com.eharmony.services.mymatchesservice.service.merger;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
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
	public LegacyMatchDataFeedDto merge(MatchFeedRequestContext requestContext) {

		log.info("merging feed for userId {}", requestContext.getUserId());
		MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest();
		request.setUserId(Long.valueOf(requestContext.getUserId()).intValue());
		
		Set<MatchDataFeedItemDto> hbaseFeed = null;
		
		try{
			hbaseFeed = repository.getMatchDataFeed(request);

		}catch(Exception ex){			
			// HBase unavailable, use only Voldy.
			log.warn("error accessing HBase repository, proceeding without: {}", ex.getMessage());
		}
		
		LegacyMatchDataFeedDto voldyFeed =  voldemortStore.getMatches(request.getUserId());
		
		if(CollectionUtils.isNotEmpty(hbaseFeed)){
			Map<String, Map<String,  Map<String, Object>>> matches = voldyFeed.getMatches();
			
			mergeHBaseProfileIntoMatchFeed(matches, hbaseFeed);
		} else {
		    log.warn("No records exists to merge in HBase for the user {} ", request.getUserId());
		}
		
		return voldyFeed;
	}
	
	/*@Override
    public LegacyMatchDataFeedDto merge(MatchFeedRequestContext requestContext) {

        log.info("merging feed for userId {}", requestContext.getUserId());
        LegacyMatchDataFeedDto legacyMatchesFeed = requestContext.getLegacyMatchDataFeedDto();
        Set<MatchDataFeedItemDto> storeMatchesFeed = requestContext.getNewStoreFeed();
        
        if(CollectionUtils.isEmpty(storeMatchesFeed)) {
            if( legacyMatchesFeed != null && MapUtils.isNotEmpty(legacyMatchesFeed.getMatches())) {
                log.warn("There are no matches in HBase for user {} and found {} matches in voldy", 
                        requestContext.getUserId(), legacyMatchesFeed.getMatches().size());
            } else {
                log.info("no matches found for user {} in both hbase and voldy", requestContext.getUserId());
            }
            return legacyMatchesFeed;
        }
        
        if(legacyMatchesFeed == null || MapUtils.isEmpty(legacyMatchesFeed.getMatches())){
            log.warn("{} Records exist in HBase for the user {} and there are no records in voldy", 
                    storeMatchesFeed.size(), requestContext.getUserId());
        }
        Map<String, Map<String,  Map<String, Object>>> matches = legacyMatchesFeed.getMatches();
        mergeHBaseProfileIntoMatchFeed(matches, storeMatchesFeed);
        
        return legacyMatchesFeed;
    }*/
	
	private static final String MATCHINFOMODEL_MATCH_PROFILE = "matchedUser";

	private void mergeHBaseProfileIntoMatchFeed(Map<String, Map<String,  Map<String, Object>>> matches,
													Set<MatchDataFeedItemDto> hbaseFeed) {
		
		for(MatchDataFeedItemDto hbaseMatch : hbaseFeed){
			
			// find this match in feed...
			String matchId = Long.toString(hbaseMatch.getMatch().getMatchId());
			Map<String,  Map<String, Object>> feedMatch = matches.get(matchId);
			if(feedMatch == null){
				
				log.warn("HBase match {} not found in voldy feed for user {}", matchId, hbaseMatch.getMatch().getUserId());
				continue;
				// TODO: what does it mean if feed is missing here?
			}
			
			// get feed profile
			Map<String, Object> feedProfile = feedMatch.get(MATCHINFOMODEL_MATCH_PROFILE);

			// overwrite feed with HBase values
			MatchProfileElement profile = hbaseMatch.getMatchedUser();
			//TODO: derive age.
			//feedProfile.put("age", profile.getAge());
			feedProfile.put("city", profile.getCity());
			feedProfile.put("country", profile.getCountry());
			feedProfile.put("firstName", profile.getFirstName());
			feedProfile.put("gender", profile.getGender());
			feedProfile.put("stateCode", profile.getStateCode());
			feedProfile.put("userId", hbaseMatch.getMatch().getMatchedUserId());
			//feedProfile.put("version", profile.getVersion());
			feedProfile.put("birthdate", profile.getBirthdate());

		}
	}
}
