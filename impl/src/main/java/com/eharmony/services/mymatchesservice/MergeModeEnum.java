package com.eharmony.services.mymatchesservice;

public enum MergeModeEnum {
	
	VOLDEMORT_ONLY("VOLDEMORT_ONLY"),
    VOLDEMORT_WITH_HBASE_PROFILE("VOLDEMORT_WITH_HBASE_PROFILE"),
    HBASE_ONLY("HBASE_ONLY");

    private String modeName;

    MergeModeEnum(String name) {
        this.modeName = name;
    }

    public String getModeName() {
        return modeName;
    }

    public static MergeModeEnum fromString(String str) {
    	
        for (MergeModeEnum mode : MergeModeEnum.values()) {
            if (mode.toString().equals(str)) {
                return mode;
            }
        }

        throw new IllegalArgumentException(String.format("'%s' is not a valid MergeMode", str));
    }
}
