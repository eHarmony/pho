package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

import org.apache.commons.collections.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class VoldyWithHBaseProfileMergeStrategy
    extends LegacyMatchDataFeedMergeStrategy {
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
    private MatchDataFeedStore voldemortStore;
    private MatchStoreQueryRepository repository;
    private Logger log = LoggerFactory.getLogger(VoldyWithHBaseProfileMergeStrategy.class);

    public VoldyWithHBaseProfileMergeStrategy(
        MatchDataFeedStore voldemortStore, MatchStoreQueryRepository repository) {
        this.voldemortStore = voldemortStore;
        this.repository = repository;
    }

    @Override
    public LegacyMatchDataFeedDto merge(MatchFeedRequestContext requestContext) {
        log.info("merging feed for userId {}", requestContext.getUserId());

        MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest(requestContext.getUserId());

        Set<MatchDataFeedItemDto> hbaseFeed = null;

        try {
            hbaseFeed = repository.getMatchDataFeed(request);
        } catch (Exception ex) {
            // HBase unavailable, use only Voldy.
            log.warn("error accessing HBase repository, proceeding without: {}",
                ex.getMessage());
        }

        LegacyMatchDataFeedDto voldyFeed = voldemortStore.getMatches(requestContext.getUserId());

        if (CollectionUtils.isNotEmpty(hbaseFeed)) {
            Map<String, Map<String, Map<String, Object>>> matches = voldyFeed.getMatches();

            Map<Long, Pair<MatchProfileElement, Long>> hbaseProfiles = getProfilesByMatchId(hbaseFeed);
            
            mergeHBaseProfileIntoMatchFeed(matches, hbaseProfiles, requestContext.getUserId());
            
        } else {
            log.warn("No records exists to merge in HBase for the user {} ",
                requestContext.getUserId());
        }

        return voldyFeed;
    }

    /**
     * Extract a map of HBase feed profiles where key is matchId, value is [HBase Profile, matched user Id]
     * 
     * @param hbaseFeed
     * @return 
     */
    private Map<Long, Pair<MatchProfileElement, Long>> getProfilesByMatchId(
        Set<MatchDataFeedItemDto> hbaseFeed) {
        Map<Long, Pair<MatchProfileElement, Long>> profilesByMatchId = new HashMap<Long, Pair<MatchProfileElement, Long>>();

        for (MatchDataFeedItemDto feedItem : hbaseFeed) {
            Long key = feedItem.getMatch().getMatchId();

            profilesByMatchId.put(key, new Pair<MatchProfileElement, Long>(feedItem.getMatchedUser(), 
            																feedItem.getMatch().getMatchedUserId()));
        }
        
        return profilesByMatchId;
    }
    
    private void mergeHBaseProfileIntoMatchFeed(
										            Map<String, Map<String, Map<String, Object>>> matches,
										            Map<Long, Pair<MatchProfileElement, Long>> hbaseProfiles,
										            long userId) {
    	
    		Set<String> matchIds = matches.keySet();   	
            for (String matchId : matchIds) {
            	
                Map<String, Map<String, Object>> feedMatch = matches.get(matchId);
                Pair<MatchProfileElement, Long> profileAndMatchedUserId = hbaseProfiles.get(matchId);
                
                if (profileAndMatchedUserId == null) {
                    log.warn("Voldy match {} not found in HBase feed for user {}",
                        matchId, userId);

                    continue;

                    // TODO: what does it mean if feed is missing here?
                }
                
                MatchProfileElement hbaseProfile = profileAndMatchedUserId.getLeft();
                Long matchedUserId = profileAndMatchedUserId.getRight();

                // get feed profile
                Map<String, Object> feedProfile = feedMatch.get(MATCHINFOMODEL_MATCH_PROFILE);

                //TODO: derive age.
                //feedProfile.put("age", profile.getAge());
                feedProfile.put("city", hbaseProfile.getCity());
                feedProfile.put("country", hbaseProfile.getCountry());
                feedProfile.put("firstName", hbaseProfile.getFirstName());
                feedProfile.put("gender", hbaseProfile.getGender());
                feedProfile.put("stateCode", hbaseProfile.getStateCode());
                feedProfile.put("userId", matchedUserId);
                //feedProfile.put("version", profile.getVersion());
                feedProfile.put("birthdate", hbaseProfile.getBirthdate().getTime());
            }
        }

	    /**
	     * Utility class for binding two objects of diffrent types.
	     */
	    public static class Pair<L, R>{
	    	
	    	L left;
	    	R right;
	    	
	    	public Pair(L l, R r){
	    		this.left = l;
	    		this.right = r;
	    	}
	
			public L getLeft() {
				return left;
			}
	
			public R getRight() {
				return right;
			} 	
	    }
    

//    private void mergeHBaseProfileIntoMatchFeed(
//        Map<String, Map<String, Map<String, Object>>> matches,
//        Map<Long, MatchProfileElement> hbaseProfiles) {
//        for (MatchDataFeedItemDto hbaseMatch : hbaseFeed) {
//            // find this match in feed...
//            String matchId = Long.toString(hbaseMatch.getMatch().getMatchId());
//            Map<String, Map<String, Object>> feedMatch = matches.get(matchId);
//
//            if (feedMatch == null) {
//                log.warn("HBase match {} not found in voldy feed for user {}",
//                    matchId, hbaseMatch.getMatch().getUserId());
//
//                continue;
//
//                // TODO: what does it mean if feed is missing here?
//            }
//
//            // get feed profile
//            Map<String, Object> feedProfile = feedMatch.get(MATCHINFOMODEL_MATCH_PROFILE);
//
//            // overwrite feed with HBase values
//            MatchProfileElement profile = hbaseMatch.getMatchedUser();
//            //TODO: derive age.
//            //feedProfile.put("age", profile.getAge());
//            feedProfile.put("city", profile.getCity());
//            feedProfile.put("country", profile.getCountry());
//            feedProfile.put("firstName", profile.getFirstName());
//            feedProfile.put("gender", profile.getGender());
//            feedProfile.put("stateCode", profile.getStateCode());
//            feedProfile.put("userId", hbaseMatch.getMatch().getMatchedUserId());
//            //feedProfile.put("version", profile.getVersion());
//            feedProfile.put("birthdate", profile.getBirthdate());
//        }
//    }
}
