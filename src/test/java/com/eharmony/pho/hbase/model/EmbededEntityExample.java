package com.eharmony.pho.hbase.model;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Property;

@Entity(value = "embded_model")
public class EmbededEntityExample {
    @Embedded
    private NestedEntity nestedObject;

    @Property(value = "annotatedProperty")
    private String annotatedProperty;
    private String property2;

    public NestedEntity getNestedObject() {
        return nestedObject;
    }

    public void setNestedObject(NestedEntity nestedObject) {
        this.nestedObject = nestedObject;
    }

    public String getAnnotatedProperty() {
        return annotatedProperty;
    }

    public void setAnnotatedProperty(String annotatedProperty) {
        this.annotatedProperty = annotatedProperty;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

}
