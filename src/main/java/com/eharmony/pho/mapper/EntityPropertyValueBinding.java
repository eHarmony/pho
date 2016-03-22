package com.eharmony.pho.mapper;
/**
 * Contains the information about entity property to store property mapping and value of the property
 * 
 * @author vvangapandu
 *
 */
public class EntityPropertyValueBinding {

    private final EntityPropertyBinding entityPropertyBinding;
    private int position;
    private Object value;
    
    public EntityPropertyValueBinding(EntityPropertyBinding entityPropertyBinding) {
        this.entityPropertyBinding = entityPropertyBinding;
    }
    public EntityPropertyBinding getEntityPropertyBinding() {
        return entityPropertyBinding;
    }
    
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
    
}
