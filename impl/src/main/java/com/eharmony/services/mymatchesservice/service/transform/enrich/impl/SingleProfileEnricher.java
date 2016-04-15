package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import javax.annotation.Resource;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.enrich.AbstractProfileEnricher;
import com.eharmony.services.profile.ProfileType;
import com.eharmony.services.profile.client.ProfileServiceClient;

public class SingleProfileEnricher extends AbstractProfileEnricher {

    @Resource 
    private ProfileServiceClient profileService;

	// See MDS.ProfileBatchRetriever
	@Override
	protected boolean processMatchSection(Map<String, Object> profileSection,
			MatchFeedRequestContext context) {

		if(context == null){
			return false;
		}
		
		Integer candidateId = Integer.valueOf(profileSection.get(MatchFeedModel.PROFILE.USERID).toString());
		
		Map<String, Object> userProfile = profileService.findProfileForUserAsMap(candidateId, ProfileType.BASIC);
		
		profileSection.put(MatchFeedModel.PROFILE.COUNTRY, userProfile.get(MatchFeedModel.PROFILE.COUNTRY));
		profileSection.put(MatchFeedModel.PROFILE.FIRSTNAME, userProfile.get(MatchFeedModel.PROFILE.FIRSTNAME));
		profileSection.put(MatchFeedModel.PROFILE.GENDER, userProfile.get(MatchFeedModel.PROFILE.GENDER));
		profileSection.put(MatchFeedModel.PROFILE.CITY, userProfile.get(MatchFeedModel.PROFILE.CITY));
		profileSection.put(MatchFeedModel.PROFILE.STATE_CODE, userProfile.get(MatchFeedModel.PROFILE.STATE_CODE));
		profileSection.put(MatchFeedModel.PROFILE.LOCALE, userProfile.get(MatchFeedModel.PROFILE.LOCALE));
		profileSection.put(MatchFeedModel.PROFILE.USERID, userProfile.get(MatchFeedModel.PROFILE.USERID));
		profileSection.put(MatchFeedModel.PROFILE.AGE, userProfile.get(MatchFeedModel.PROFILE.AGE));
		profileSection.put(MatchFeedModel.PROFILE.BIRTHDATE, userProfile.get(MatchFeedModel.PROFILE.BIRTHDATE));
		
		return true;
	}

}
