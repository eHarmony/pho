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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.service.EcvService;


/**
 * EcvResource is exposed as JAX-RS endpoint and is used by Hobit monitoring to
 * determine whether a server hosting this resource should receive http traffic.
 */

@Component @Path("/v1/ecv")
public class EcvResource {

    private static final Status STATUS_SERVER_UP = Status.OK;
    private static final Status STATUS_SERVER_DOWN = Status.SERVICE_UNAVAILABLE;
    private static final String STR_SERVER_UP = "SERVER UP";
    private static final String STR_SERVER_DOWN = "SERVER DOWN";

    private static final Logger log = LoggerFactory.getLogger(EcvResource.class);

    @Resource(name = "ecvService")
    private EcvService ecvService = null;

    /**
     * Constructs new instance of EcvResource.
     */
    public EcvResource() {

        // log the fact that the endpoint is instantiated
        // should only be one entry in log file to prove we have a singleton
        log.info("Instantiated EcvResource JAX-RS endpoint");

    }

    /**
     * Returns whether a service should be up or down. Internally queries the
     * ecvService for this information.
     *
     * @return  response with 200 code and "SERVICE UP" in the body if the
     *          server should be up response with 503 code and "SERVICE DOWN" in
     *          the body if the server should be down
     */
    @GET @Produces(MediaType.TEXT_PLAIN)
    public Response getServerStatus() {

        if (ecvService.isServerUp()) {

            if (log.isDebugEnabled()) {

                log.debug("ECV check returning {}", STATUS_SERVER_UP);

            }
            return Response.status(STATUS_SERVER_UP)
                           .entity(STR_SERVER_UP)
                           .build();

        } else {

            if (log.isDebugEnabled()) {

                log.debug("ECV check returning {}", STATUS_SERVER_DOWN);

            }
            return Response.status(STATUS_SERVER_DOWN)
                           .entity(STR_SERVER_DOWN)
                           .build();

        }

    }

    /**
     * Sets a reference to ecvService
     *
     * @param  ecvService  a reference to ecvService
     */
    public void setEcvService(EcvService ecvService) {

        this.ecvService = ecvService;

    }

}
