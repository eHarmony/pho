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
package com.eharmony.services.mymatchesservice.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;


/**
 * Custom version of Jackson ObjectMapper that configures serialization of dates
 * as timestamps and ignores unknown properties on JSON deserialization.
 *
 * @author  adenissov
 */
public class CustomObjectMapper
          extends ObjectMapper {

    /**
     * Creates a new instance with custom settings.
     */
    public CustomObjectMapper() {

        this.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

}
