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

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Path("/v1/async/")
public class MatchfeedAsyncResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MatchFeedAsyncRequestHanlder requesthandler;

    @GET
    @Path("/users/{userId}/matches")
    @Produces(MediaType.APPLICATION_JSON)
    public void getMatchesFeed(@PathParam("userId") final int userId, 
            @Suspended final AsyncResponse asyncResponse) {


        log.info("fetching match feed for user ={}", userId);

        requesthandler.getMatchesFeed(userId, asyncResponse);

    }
}
