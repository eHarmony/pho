/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2015 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.rest;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.rest.internal.DataServiceStateEnum;
import com.eharmony.singles.common.status.MatchStatus;

@Component
@Path("/v1")
public class MatchFeedAsyncResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MatchFeedAsyncRequestHandler requesthandler;

    private static final int TEASER_MATCH_DEFAULT_PAGINATION_SIZE = 1;
    
    private static final int TEASER_MATCH_DEFAULT_RESULT_SIZE = 5;
    
    @Resource
    private Integer teaserHbaseFetchSize;
    
    private static final String COMM_MATCH_STATUS = "COMM";
    
    @GET
    @Path("/users/{userId}/matches")
    @Produces(MediaType.APPLICATION_JSON)
    public void getMatches(@PathParam("userId") long userId, @MatrixParam("locale") String locale,
            @MatrixParam("status") Set<String> statuses, @QueryParam("viewHidden") boolean viewHidden,
            @QueryParam("allowedSeePhotos") boolean allowedSeePhotos, @QueryParam("pageNum") Integer pageNum,
            @QueryParam("pageSize") Integer pageSize, @Suspended final AsyncResponse asyncResponse, 
            @QueryParam("voldyState") DataServiceStateEnum voldyState) {

        //TODO remove this check and assume user requesting all matches if this field is empty
    	if(CollectionUtils.isEmpty(statuses)){
            throw new WebApplicationException("Missing status.", Status.BAD_REQUEST);
    	}
    	
    	if(StringUtils.isEmpty(locale)){
            throw new WebApplicationException("Missing locale.", Status.BAD_REQUEST);
        }

        Set<String> normalizedStatuses = toLowerCase(statuses);
        int pn = (pageNum == null ? 0 : pageNum.intValue());
        int ps = (pageSize == null ? 0 : pageSize.intValue());

        MatchFeedQueryContext requestContext = MatchFeedQueryContextBuilder.newInstance()
                .setAllowedSeePhotos(allowedSeePhotos).setLocale(locale).setPageSize(ps).setStartPage(pn)
                .setStatuses(normalizedStatuses).setUserId(userId).setViewHidden(viewHidden)
                .setVoldyState(voldyState).build();

        log.info("fetching match feed for user ={}", userId);
        requesthandler.getTeaserMatchesFeed(requestContext, asyncResponse);
    }
    
    
    /**
     * Returns matches with photos and with match status not in (archived or closed) or passed via 'status' param. The matches are sorted in the order of their 
     * score returned from the scorer service. 
     *  
     * @param userId  Id of the logged in user
     * @param statuses  set of match status values. Valid values none or one or both of [ 'new' , 'comm'].
     * @param resultSize  number of matches to be returned
     * @param asyncResponse Asynchronous response stream
     */
    @GET
    @Path("/users/{userId}/teasermatches")
    @Produces(MediaType.APPLICATION_JSON)
    public void getTeaserMatches(
    		@PathParam("userId") long userId, 
    		@MatrixParam("status") Set<String> statuses,
            @QueryParam("resultSize") Integer resultSize, 
            @Suspended final AsyncResponse asyncResponse) {

    	
    	Set<String>	statusSet = new HashSet<String>();
		if (!CollectionUtils.isEmpty(statuses)) {

			statuses.forEach(status -> {

				if (status.equalsIgnoreCase(MatchStatus.NEW.name())) {
					
					statusSet.add(MatchStatus.NEW.name().toLowerCase());
					
				} else if (status.equalsIgnoreCase(COMM_MATCH_STATUS)) {
					
					statusSet.add(MatchStatus.MYTURN.name().toLowerCase(Locale.US));
					statusSet.add(MatchStatus.OPENCOMM.name().toLowerCase(Locale.US));
					statusSet.add(MatchStatus.THEIRTURN.name().toLowerCase(Locale.US));
					
				}
			});

			if (CollectionUtils.isEmpty(statusSet)) {
				throw new WebApplicationException("Invalid status code sent. Valid value set are 'new', 'comm'", Status.BAD_REQUEST);
			}
			
		} else {
			
			//By default the search pool will include only the new matches.
			statusSet.add(MatchStatus.NEW.name().toLowerCase());
			
		}

		resultSize = (resultSize == null ? TEASER_MATCH_DEFAULT_RESULT_SIZE : resultSize.intValue());  //Setting the default result size to 5.

		MatchFeedQueryContext requestContext = MatchFeedQueryContextBuilder.newInstance()
                .setAllowedSeePhotos(true)
                .setPageSize(teaserHbaseFetchSize)   // For phase 1 setting 100 as the default number of records to fetch from HBASE. In V2, there will be DAO service for this.
                .setStartPage(TEASER_MATCH_DEFAULT_PAGINATION_SIZE)  //There will be no pagination. There will be only one page and the resultSize param will decide how many items it consists of.
                .setStatuses(statusSet)
                .setUserId(userId)
                .setTeaserResultSize(resultSize)		// This is the number of results to be returned back to the client/user.
                .build();

        log.debug("fetching teaser match feed for user ={}", userId);
        requesthandler.getTeaserMatchesFeed(requestContext, asyncResponse);
    }

    private Set<String> toLowerCase(Set<String> values) {

        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        
        Set<String> result = new HashSet<String>();
        for (String value : values) {
            result.add(value.toLowerCase());
        }
        return result;

    }
}
