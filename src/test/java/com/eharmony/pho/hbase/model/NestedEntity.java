package com.eharmony.pho.hbase.model;

import com.google.code.morphia.annotations.Property;

public class NestedEntity {
    @Property(value = "nestedClassName")
    private String name;

    private String nestedClassDescription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNestedClassDescription() {
        return nestedClassDescription;
    }

    public void setNestedClassDescription(String nestedClassDescription) {
        this.nestedClassDescription = nestedClassDescription;
    }
}
