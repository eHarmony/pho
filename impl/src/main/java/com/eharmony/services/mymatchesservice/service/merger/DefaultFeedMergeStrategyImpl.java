package com.eharmony.services.mymatchesservice.service.merger;

import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.BIRTHDATE;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.CITY;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.COUNTRY;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.FIRSTNAME;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.GENDER;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.STATE_CODE;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE.USERID;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.SECTIONS.PROFILE;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.model.MatchProfileElement;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.UserMatchesHBaseStoreFeedService;
import com.eharmony.services.mymatchesservice.service.transform.LegacyMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;

public class DefaultFeedMergeStrategyImpl implements FeedMergeStrategy{

    private static final Logger log = LoggerFactory.getLogger(DefaultFeedMergeStrategyImpl.class);
    
    @Override
    public void merge(MatchFeedRequestContext requestContext, UserMatchesHBaseStoreFeedService userMatchesFeedService) {

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
            
        }else if(legacyMatchesFeed == null || MapUtils.isEmpty(legacyMatchesFeed.getMatches())){
            log.warn("{} Records exist in HBase for user {}, none in voldy. Using FULL HBase record.", 
                    storeMatchesFeed.size(), requestContext.getUserId());

            // call hbase with no strategy in context, so all fields are returned.
            // TODO: limit size of hbase response.
            // TODO: null is ugly, clean up.
           
            requestContext.setFeedMergeType(null);
            requestContext.setFallbackRequest(true);
            userMatchesFeedService.getUserMatchesFromHBaseStoreSafe(requestContext)
            	.subscribe(response -> {
            		
            		requestContext.setNewStoreFeed(response);
            		
            		LegacyMatchDataFeedDto xformLegacyFeed = LegacyMatchFeedTransformer.transform(requestContext);
                    LegacyMatchDataFeedDtoWrapper feedWrapper = new LegacyMatchDataFeedDtoWrapper(requestContext.getUserId());
                    feedWrapper.setFeedAvailable(true);
                    feedWrapper.setLegacyMatchDataFeedDto(xformLegacyFeed);   
                    
                    // Update the request context
                    requestContext.setLegacyMatchDataFeedDtoWrapper(feedWrapper);
             });
                        
        }else{
        
	        Map<String, Map<String,  Map<String, Object>>> matches = legacyMatchesFeed.getMatches();
	        mergeHBaseProfileIntoMatchFeed(matches, storeMatchesFeed);
	   }
    }
    
    private void mergeHBaseProfileIntoMatchFeed(Map<String, Map<String,  Map<String, Object>>> matches,
                                                    Set<MatchDataFeedItemDto> hbaseFeed) {
        
        for(MatchDataFeedItemDto hbaseMatch : hbaseFeed) {
            
            // find this match in feed...
            String matchId = Long.toString(hbaseMatch.getMatch().getMatchId());
            Map<String,  Map<String, Object>> feedMatch = matches.get(matchId);
            if(feedMatch == null){
                
                log.warn("HBase match {} not found in voldy feed for user {}", matchId, hbaseMatch.getMatch().getUserId());
                continue;
                // TODO: what does it mean if feed is missing here?
            }
 
            
            // get feed profile
            Map<String, Object> feedProfile = feedMatch.get(PROFILE);

            // overwrite feed with HBase values
            MatchProfileElement profile = hbaseMatch.getMatchedUser();
            if(profile.getGender() > 0) {
            	feedProfile.put(GENDER, profile.getGender());
            }
            if(profile.getCountry() > 0) {
            	feedProfile.put(COUNTRY, profile.getCountry());
            }
            feedProfile.put(USERID, hbaseMatch.getMatch().getMatchedUserId());
            if(StringUtils.isNotBlank(profile.getCity())) {
                feedProfile.put(CITY, profile.getCity());
            }
            if(StringUtils.isNotBlank(profile.getFirstName())) {
                feedProfile.put(FIRSTNAME, profile.getFirstName());
            }
            if(StringUtils.isNotBlank(profile.getStateCode())) {
                feedProfile.put(STATE_CODE, profile.getStateCode());
            }
            if(profile.getBirthdate() != null) {
                feedProfile.put(BIRTHDATE, profile.getBirthdate().getTime());
            }

        }
    }
}
