package com.eharmony.services.mymatchesservice.util;

public enum MatchStatusGroupEnum {

    NEW(0, "NEW"), COMMUNICATION(1, "COMMUNICATION"), ARCHIVE(2, "ARCHIVE");
    private final int code;
    private final String name;

    private MatchStatusGroupEnum(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
