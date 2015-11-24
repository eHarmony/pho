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

public enum MatchStatusEnum {

    NEW(0, "NEW"), MYTURN(1, "MYTURN"), THEIRTURN(2, "THEIRTURN"), OPENCOMM(3, "OPENCOMM"), CLOSED(4, "CLOSED"), ARCHIVED(5, "ARCHIVED");

    private final int id;
    private final String name;

    private MatchStatusEnum(final int id, final String name) {

        this.id = id;
        this.name = name;

    }

    public static MatchStatusEnum fromInt(int id) {

        switch (id) {

        case 0:
            return NEW;

        case 1:
            return MYTURN;

        case 2:
            return THEIRTURN;

        case 3:
            return OPENCOMM;

        case 4:
            return CLOSED;

        case 5:
            return ARCHIVED;

        default:
            return NEW;

        }

    }
    
    public static MatchStatusEnum fromName(String name) {
        for(MatchStatusEnum statusEnum : values() ) {
            if(statusEnum.getName().equalsIgnoreCase(name)) {
                return statusEnum;
            }
        }
        return null;
    }
    
    public int toInt() {
        return id;
    }
    public String getName() {
        return name;
    }

}
