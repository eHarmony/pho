package com.eharmony.services.mymatchesservice.rest;

/**
 * Manages the User Matches Feed Responses.
 * 
 * 1. Transforms the content from Store view to API response view (List of Objects to Map of Matches to support legacy application)
 * 2. invokes feed merge component to merge different feed views ( ex: HBase and Redis)
 * 3. Applies the filters based on user request and removes sensitive content (ex: user birthdate)
 * 4. Enrcies/Transforms the content as per API contract.
 * 
 * @author vvangapandu
 *
 */
public interface FeedResponseHandler {

    public void processMatchFeedResponse(MatchFeedRequestContext context);
    
}
