package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.filter.AbstractProfileFilter;

public class MatchPhotoFilter extends AbstractProfileFilter {

	private static final Logger logger = LoggerFactory.getLogger(MatchPhotoFilter.class);

	@Override
	protected boolean processMatchSection(Map<String, Object> profileSection, MatchFeedRequestContext context) {

		if (profileSection == null) {
			
			logger.warn("profile section is null for userId - {}", context.getUserId());
			return false;
		
		}
		Integer photo = (Integer)profileSection.get(MatchFeedModel.PROFILE.PHOTO_COUNT);

		return photo != null && photo.intValue() > 0 ;
		
	}
}