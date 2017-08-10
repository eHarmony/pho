package com.eharmony.pho.mapper;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Maps;

/**
 * Map an array of properties and an array of associated property names to an
 * Object of a specified type.
 */
public class ProjectedResultMapper {
    
    private final ObjectMapper mapper;
    
    public ProjectedResultMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    public ProjectedResultMapper() {
        this(new ObjectMapper());
    }
    
    protected Map<String,Object> propertyMap(Object[] properties,
            String[] propertyNames) {
        if (properties.length != propertyNames.length) {
            throw new IllegalArgumentException("The number of properties ("
                    + properties.length
                    + ") must match the number of property names ("
                    + propertyNames.length + ")");
        }
        int n = properties.length;
        Map<String,Object> map = Maps.newHashMapWithExpectedSize(n);
        for (int i = 0; i < n; i++) {
            map.put(propertyNames[i], properties[i]);
        }
        return map;
    }
    
    /**
     * Map an Object or an Object array with an associated array of property
     * names to the provided type.
     * @param <R> return type
     * @param resultClass
     *            the desired mapped type
     * @param properties
     *            the property or properties to be mapped
     * @param propertyNames
     *            an array of associated property names
     * @return the mapped object of type R
     */
    public <R> R mapTo(Class<R> resultClass, Object properties,
            String[] propertyNames) {
        return propertyNames.length == 1
            ? resultClass.cast(properties)
            : mapper.convertValue(propertyMap((Object[]) properties, propertyNames), resultClass);
    }

}