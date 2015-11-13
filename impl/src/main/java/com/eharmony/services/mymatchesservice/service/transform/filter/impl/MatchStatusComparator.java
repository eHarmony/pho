/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2012 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.service.transform.filter.impl;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import com.eharmony.singles.common.status.MatchStatus;
import com.google.common.collect.Maps;


/**
 * Compares match statuses such that the following order is produced: 
 * NEW, MYTURN, OPENCOMM, THEIRTURN, ARCHIVED
 *
 * @author  adenissov
 */
public class MatchStatusComparator
          implements Comparator<String> {

    private Map<String, Integer> statusOrder = Maps.newHashMapWithExpectedSize(5);

    public MatchStatusComparator() {

        statusOrder.put(MatchStatus.NEW.name().toLowerCase(Locale.US), 1);
        statusOrder.put(MatchStatus.MYTURN.name().toLowerCase(Locale.US), 2);
        statusOrder.put(MatchStatus.OPENCOMM.name().toLowerCase(Locale.US), 3);
        statusOrder.put(MatchStatus.THEIRTURN.name().toLowerCase(Locale.US), 4);
        statusOrder.put(MatchStatus.ARCHIVED.name().toLowerCase(Locale.US), 5);

    }

    @Override public int compare(String status1,
                                 String status2) {

        if ((status1 == null) && (status2 == null)) {

            return 0;

        }
        if (status1 == null) {

            return 1; // unknown goes last

        }
        if (status2 == null) {

            return -1; // unknown goes last

        }

        Integer order1 = statusOrder.get(status1);
        Integer order2 = statusOrder.get(status2);

        if ((order1 == null) && (order2 == null)) {

            return 0;

        }
        if (order1 == null) {

            return 1; // unknown goes last

        }
        if (order2 == null) {

            return -1; // unknown goes last

        }

        return order1 - order2;

    }

}
