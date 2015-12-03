package com.eharmony.services.mymatchesservice.rest.internal;


public enum DataServiceStateEnum {

	DISABLED("disabled"), ENABLED("enabled"), EMPTY("empty");
	
    private String serviceStateName;

	DataServiceStateEnum(String name) {
        this.serviceStateName = name;
    }

    public String getDataServiceStateName() {
        return serviceStateName;
    }

    public static DataServiceStateEnum fromString(String str) {
    	
        for (DataServiceStateEnum state: DataServiceStateEnum.values()) {
            if (state.toString().equals(str)) {
                return state;
            }
        }

        throw new IllegalArgumentException(String.format("'%s' is not a valid DataServiceStateEnum", str));
    }

}
