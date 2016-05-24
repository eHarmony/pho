package com.eharmony.services.mymatchesservice.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedTransformerChain;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
/**
 * Handles the response for MyMatches feed request.
 * 
 * Implements filter and enrichment components specific to MyMatches Feed API
 * 
 * @author vvangapandu
 *
 */
@Component("userMyMatchesFeedResponseHandler")
public class UserMyMatchesFeedResponseHandler extends AbstractFeedResponseHandler {

    @Resource(name = "getMatchesFeedEnricherChain")
    private MatchFeedTransformerChain getMatchesFeedEnricherChain;

    @Resource(name = "getUserSortedMatchesFilterChain")
    private MatchFeedTransformerChain userMyMatchesFeedFilterChain;

    @Override
    public void filterResults(MatchFeedRequestContext context) {
    	
    	// TODO : apply SearchAndFilter criteria here.
    	LegacyMatchDataFeedDto matchFeed = context.getLegacyMatchDataFeedDto();
    	MatchFeedSearchAndFilterCriteria searchFilterCriteria = context.getMatchFeedQueryContext().getSearchFilterCriteria();
    	
    	applySearchAndFilterCriteria(matchFeed, searchFilterCriteria);
    	
        userMyMatchesFeedFilterChain.execute(context);
    }

    public void applySearchAndFilterCriteria(LegacyMatchDataFeedDto matchFeed, 
    											MatchFeedSearchAndFilterCriteria searchFilterCriteria){

      	matchFeed.getMatches().values().stream().filter(match -> 
      								isFiltered(match, searchFilterCriteria));
    }
    
    /**
     * Remove a match if it does not match ALL of the criteria.
     * 
     * @param match		A single match in the feed
     * @param criteria	set of filtering criteria to potentially disqualify the match.
     * @return <code>true</code> if this match doesn't match all criteria.
     */
    private boolean isFiltered(Map<String, Map<String, Object>> match, 
    								MatchFeedSearchAndFilterCriteria criteria) {
    	
    	Map<String, Object> matchSection = match.get(MatchFeedModel.SECTIONS.MATCH);
    	Map<String, Object> profileSection = match.get(MatchFeedModel.SECTIONS.PROFILE);
    	
    	if(criteria.isHasPhotos() != null && criteria.isHasPhotos() == true &&
    			profileSection.get(MatchFeedModel.PROFILE.PHOTO_COUNT) != null &&
    			((Integer)profileSection.get(MatchFeedModel.PROFILE.PHOTO_COUNT)) == 0){
    			return true;
    	}
    	if(criteria.getAnyTextField() != null && 
    			(!criteria.getAnyTextField().equals(matchSection.get(MatchFeedModel.PROFILE.FIRSTNAME)) &&
    			 !criteria.getAnyTextField().equals(matchSection.get(MatchFeedModel.PROFILE.CITY)))){
    			 
    			return true;
    	}
    
    	if(criteria.getAge() != null &&
    			(!criteria.getAge().equals(profileSection.get(MatchFeedModel.PROFILE.AGE)))){
    		
    			return true;
    	}
    	if(criteria.getName() != null &&
    			(!criteria.getName().equals(matchSection.get(MatchFeedModel.PROFILE.FIRSTNAME)))){
    		
    			return true;
    	}
    	if(criteria.getDistance() != null &&
    			(criteria.getDistance() < (Integer) matchSection.get(MatchFeedModel.MATCH.DISTANCE))){
    		
    			return true;
    	}
    	if(criteria.getCity() != null &&
    			(!criteria.getCity().equals(profileSection.get(MatchFeedModel.PROFILE.CITY)))){
    		
    			return true;
    	}
    	
		return false;
	}

    
	@Override
    public void enrichFeedItems(MatchFeedRequestContext context) {
        getMatchesFeedEnricherChain.execute(context);
    }

}
