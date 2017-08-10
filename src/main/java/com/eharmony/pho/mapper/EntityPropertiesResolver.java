package com.eharmony.pho.mapper;

import java.util.List;
import java.util.Map;

import com.eharmony.pho.translator.PropertyResolver;

public class EntityPropertiesResolver implements PropertyResolver {

    private final EntityPropertiesMappingContext entityPropertiesMappingContext;

    public EntityPropertiesResolver(final EntityPropertiesMappingContext entityPropertiesMappingContext) {
        this.entityPropertiesMappingContext = entityPropertiesMappingContext;
    }

    @Override
    public EntityPropertyBinding resolveEntityPropertyBindingByStoreMappingName(String mappingName, Class<?> clz) {
        return entityPropertiesMappingContext.resolveEntityPropertyBindingByStoreMappingName(clz, mappingName);
    }

    @Override
    public EntityPropertyBinding resolveEntityPropertyBindingByEntityFieldName(String entityFieldName,
            Class<?> entityClass) {
        return entityPropertiesMappingContext.resolveEntityPropertyBindingByEntityFieldName(entityClass,
                entityFieldName);
    }

    @Override
    public List<String> resolveEntityMappingPropertyNames(List<String> fieldPropertyNames, Class<?> clz) {
        return entityPropertiesMappingContext.resolveEntityMappingPropertyNames(clz, fieldPropertyNames);
    }

    @Override
    public String resolve(String fieldPropertyName, Class<?> entityClass) {
        return entityPropertiesMappingContext.resolveEntityMappingPropertyName(entityClass, fieldPropertyName);
    }

    @Override
    public <T> Map<String, EntityPropertyBinding> getStoreFieldNamePropertyBindingMap(Class<T> clz) {
        return entityPropertiesMappingContext.getStoreFieldNamePropertyBindingMap(clz);
    }

    @Override
    public <T> Map<String, EntityPropertyBinding> getEntityPropertyNamePropertyBindingMap(Class<T> clz) {
        return entityPropertiesMappingContext.getEntityPropertyNamePropertyBindingMap(clz);
    }

}
