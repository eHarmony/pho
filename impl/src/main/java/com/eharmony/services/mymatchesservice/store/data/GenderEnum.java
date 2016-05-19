package com.eharmony.services.mymatchesservice.store.data;


/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2009 eharmony.com, Inc. All rights reserved.
 *
 */

/**
 * Gender enum.
 */
public enum GenderEnum {UNKNOWN(0, "Unknown"),
    MALE(1, "Male"),
    FEMALE(2, "Female");

    private String name;
    private int value;

    GenderEnum(int val, String name) {
        this.value = val;
        this.name = name;
    }

    /**
     * Returns the GenderEnum instance corresponding to the given code.
     *
     * @param   code number equivalent to a GenderEnum enum.
     *
     * @return matching GenderEnum, or UNKNOWN otherwise.
     */
    public static GenderEnum fromInt(int code) {
        switch (code) {
        case 1:
            return MALE;

        case 2:
            return FEMALE;

        default:
            return UNKNOWN;
        }
    }

    public static GenderEnum fromName(String name) {
        if ("male".equalsIgnoreCase(name)) {
            return MALE;
        } else if ("female".equalsIgnoreCase(name)) {
            return FEMALE;
        } else {
            return UNKNOWN;
        }
    }

    public String getName() {
        return name;
    }

    public int toInt() {
        return value;
    }

    @Override
    public String toString() {
        return getName();
    }

    public GenderEnum oppositeGender() {
        switch (this) {
        case MALE:
            return FEMALE;

        case FEMALE:
            return MALE;

        case UNKNOWN:
            return UNKNOWN;

        default: // not going to happen

            return this;
        }
    }
}
