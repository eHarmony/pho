package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.eharmony.services.mymatchesservice.rest.SingleMatchRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.IMatchTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.profile.ProfileType;
import com.eharmony.services.profile.client.ProfileServiceClient;

public class SingleProfileEnricher implements IMatchTransformer {

    @Resource 
    private ProfileServiceClient profileService;

	@Override
	public SingleMatchRequestContext processSingleMatch(SingleMatchRequestContext context) {
		
		if(context != null && context.matchIsAvailable()){
			
			Map<String, Object> matchSection = context.getSingleMatch().get(MatchFeedModel.SECTIONS.MATCH);
			Map<String, Object> profileSection = context.getSingleMatch().get(MatchFeedModel.SECTIONS.PROFILE);
		
			String userId = matchSection.get(MatchFeedModel.MATCH.USER_ID).toString();
			String candidateId = profileSection.get(MatchFeedModel.PROFILE.USERID).toString();
			List<String> userIds = new ArrayList<String>();
			userIds.add(userId);
			userIds.add(candidateId);
			
			Map<String, Map<String, Object>> userProfiles = profileService.findProfilesForUsersAsMap(userIds, ProfileType.BASIC);
			Map<String, Object> userProfile = userProfiles.get(userId);
			Map<String, Object> candidateProfile = userProfiles.get(candidateId);
			
			profileSection.put(MatchFeedModel.PROFILE.COUNTRY, candidateProfile.get(MatchFeedModel.PROFILE.COUNTRY));
			profileSection.put(MatchFeedModel.PROFILE.FIRSTNAME, candidateProfile.get(MatchFeedModel.PROFILE.FIRSTNAME));
			profileSection.put(MatchFeedModel.PROFILE.GENDER, candidateProfile.get(MatchFeedModel.PROFILE.GENDER));
			profileSection.put(MatchFeedModel.PROFILE.CITY, candidateProfile.get(MatchFeedModel.PROFILE.CITY));
			profileSection.put(MatchFeedModel.PROFILE.STATE_CODE, candidateProfile.get(MatchFeedModel.PROFILE.STATE_CODE));
			profileSection.put(MatchFeedModel.PROFILE.LOCALE, candidateProfile.get(MatchFeedModel.PROFILE.LOCALE));
			profileSection.put(MatchFeedModel.PROFILE.USERID, candidateProfile.get(MatchFeedModel.PROFILE.USERID));
			profileSection.put(MatchFeedModel.PROFILE.AGE, candidateProfile.get(MatchFeedModel.PROFILE.AGE));
			profileSection.put(MatchFeedModel.PROFILE.BIRTHDATE, candidateProfile.get(MatchFeedModel.PROFILE.BIRTHDATE));
			
			matchSection.put(MatchFeedModel.MATCH.FIRST_NAME, userProfile.get(MatchFeedModel.PROFILE.FIRSTNAME));
			matchSection.put(MatchFeedModel.MATCH.MATCH_FIRST_NAME, candidateProfile.get(MatchFeedModel.PROFILE.FIRSTNAME));
		}
		
		return context;
	}
}
