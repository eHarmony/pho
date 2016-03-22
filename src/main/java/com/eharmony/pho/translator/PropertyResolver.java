package com.eharmony.pho.translator;

import java.util.List;
import java.util.Map;

import com.eharmony.pho.mapper.EntityPropertyBinding;

/**
 * Resolve datastore specific names for properties of an entity class.
 * 
 */
public interface PropertyResolver {

    public String resolve(String entityFieldName, Class<?> entityClass);

    public List<String> resolveEntityMappingPropertyNames(List<String> entityFieldNames, Class<?> entityClass);

    public EntityPropertyBinding resolveEntityPropertyBindingByStoreMappingName(String mappingName, Class<?> clz);

    public EntityPropertyBinding resolveEntityPropertyBindingByEntityFieldName(String entityFieldName,
            Class<?> entityClass);

    public <T> Map<String, EntityPropertyBinding> getStoreFieldNamePropertyBindingMap(Class<T> clz);

    public <T> Map<String, EntityPropertyBinding> getEntityPropertyNamePropertyBindingMap(Class<T> clz);

}
