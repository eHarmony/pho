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
package com.eharmony.services.mymatchesservice.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * EcvService is responsible for figuring out whether whether the server is up.
 * It also has a method to shutdown endpoints to stop inflow of events.
 *
 */
public class EcvService {

    private final String ecvFileName; // injected by Spring @construction
    private static final Logger log = LoggerFactory.getLogger(EcvService.class);

    /**
     * Creates a new instance of the service.
     *
     * @param  ecvFileName  the full filename of the ecv file that signals that
     *                      a service should be down
     * @param  waitTimeout  number of seconds to wait for the executor to finish
     *                      all pending tasks before timing out
     */
    public EcvService(String ecvFileName,
                      int waitTimeout) {

        Assert.hasText(ecvFileName, "ecvFileName parameter can not be blank");
        Assert.isTrue(waitTimeout > -1, "waitTimeout parameter can not be negative");

        log.info("EcvService initialized...");
        this.ecvFileName = ecvFileName;

    }

    /**
     * Gets the full name of the ecv file, presence of which indicates that the
     * server should be down.
     *
     * @return  the full name of the ecv file
     */
    public String getEcvFileName() {

        return ecvFileName;

    }

    /**
     * Determines if the server should be up by checking whether a file with
     * filename provided at construction is present on the file system.
     *
     * @return  true if the server should be up and the file is not present
     *          false if the server should be down and the file is present
     */
    public boolean isServerUp() {

        File file = new File(ecvFileName);
        return !file.isFile();

    }

    /**
     * Shuts down the messaging endpoints and thread pool task executor. Waits
     * for the number of seconds specified at construction for the executor to
     * complete all pending tasks and shutdown.
     *
     * @return  true if the executor has finished all pending tasks false if the
     *          executor has not finished pending tasks and timeout has occured
     */
    public boolean shutdownEndpoints() {

        boolean result = true;

        return result;

    }

}
