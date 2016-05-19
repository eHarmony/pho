package com.eharmony.pho.hbase.translator;

import org.apache.commons.lang.StringUtils;

import com.eharmony.pho.translator.EntityResolver;
import com.google.code.morphia.annotations.Entity;

/**
 * Resolve the collection name of an entity class using Morphia's mapper.
 */
public class MorphiaEntityResolver implements EntityResolver {

    @Override
    public String resolve(Class<?> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        String mappedName = entityClass.getSimpleName();
        if (entity != null && StringUtils.isNotBlank(entity.value())) {
            mappedName = entity.value();
        }
        return mappedName;
    }

}
