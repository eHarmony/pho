package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.getIntegerNullSafe;
import static com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.getLongNullSafe;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.fw.util.DateUtil;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel.PROFILE;
import com.eharmony.services.mymatchesservice.service.transform.enrich.AbstractProfileEnricher;

public class AgeCalculatorEnricher extends AbstractProfileEnricher {

	private static final Logger logger = LoggerFactory.getLogger(AgeCalculatorEnricher.class);
	
	@Override
	protected boolean processMatchSection(Map<String, Object> profileSection,
			MatchFeedRequestContext context) {

        Long birthdate = getLongNullSafe(PROFILE.BIRTHDATE, profileSection);
        if (birthdate == null) {

            logger.warn("Birthdate is missing for userId={}", getLongNullSafe(PROFILE.USERID, profileSection));
            return false;

        }
        
        int age = DateUtil.getAgeInYears(new Date(birthdate));

        logger.debug("Calculated age={} for userId={}", age, getLongNullSafe(PROFILE.USERID, profileSection));
       
        profileSection.put(PROFILE.AGE, age);

        return true;
	}

}
