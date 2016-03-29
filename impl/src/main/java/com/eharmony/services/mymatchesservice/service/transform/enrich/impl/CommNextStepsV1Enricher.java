package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.AbstractMatchFeedTransformer;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.singles.common.communication.CommDisplayMessageDto;
import com.eharmony.singles.common.communication.CommDisplayMessageService;
import com.eharmony.singles.common.communication.CommDisplayMessageTypeEnum;

public class CommNextStepsV1Enricher extends AbstractMatchFeedTransformer{

	private static final Logger logger = LoggerFactory.getLogger(CommNextStepsV1Enricher.class);


    @Resource private CommDisplayMessageService commDisplayMessageService;

	@SuppressWarnings("unchecked")
    @Override
	protected boolean processMatch(Map<String, Map<String, Object>> match,
			MatchFeedRequestContext context) {
	    
        if(context.isUseV2CommNextSteps()){
            // Using V2, so this can be skipped
            return true;
        }

		Map<String, Object> commSection = match.get(MatchFeedModel.SECTIONS.COMMUNICATION);
		
		long userId = context.getUserId();
		
		// Only add if it does not already exist, or if it contains action, which means this is a V2 Object
		if(commSection.get(MatchFeedModel.COMMUNICATION.NEXT_STEP) == null
		        || ((Map<String, Object>) commSection.get(MatchFeedModel.COMMUNICATION.NEXT_STEP)).containsKey(MatchFeedModel.COMMUNICATION.ACTION)){

	        CommDisplayMessageDto message = null;

	        Map<String, Object> profile = match.get(MatchFeedModel.SECTIONS.PROFILE);

	        try {

	            Map<String, Object> messageContext =
	                    new HashMap<String, Object>(match.get(MatchFeedModel.SECTIONS.MATCH));
	            	            
	            messageContext.put("locale", profile.get(MatchFeedModel.PROFILE.LOCALE));
	            messageContext.put("gender", profile.get(MatchFeedModel.PROFILE.GENDER));
	            messageContext.put("matchFirstName", profile.get(MatchFeedModel.PROFILE.FIRSTNAME));

	            message =
	                commDisplayMessageService.evaluateAndResolve(messageContext,
	                                                             CommDisplayMessageTypeEnum.MY_MATCHES_MESSAGE);

	        } catch (Exception e) {

	            logger.warn("Comm display message evaluation failed for userId={}", userId, e);
	            return false;

	        }

	  						
	        Map<String, Object> displayMessageData = new HashMap<String, Object>();

	        // this could be caused by the match being in a bad-state
	        if (message == null) {

	        	// allow the user to see it, granted it may appear invalid, which will cause a call to CC to manually fix.
	            logger.warn("Comm display message dto is null for userId={} matchInfo={}", userId, match);

	        }
	        else {

	        	displayMessageData.put(MatchFeedModel.COMMUNICATION.LINK, message.getMessageLink());
	        	displayMessageData.put(MatchFeedModel.COMMUNICATION.CAPTION, message.getCaptionKey());
	        	displayMessageData.put(MatchFeedModel.COMMUNICATION.MESSAGE, message.getMessage());

	        }

	        // finally, add the next steps to the communication section
	        commSection.put(MatchFeedModel.COMMUNICATION.NEXT_STEP, displayMessageData);

	        if (logger.isDebugEnabled()) {

	            logger.debug("Enriched comm next step={} for userId={} and matchInfo={}",
	                      new Object[] { displayMessageData, userId, match });

	        }
		}
		
		return true;
	}
}
