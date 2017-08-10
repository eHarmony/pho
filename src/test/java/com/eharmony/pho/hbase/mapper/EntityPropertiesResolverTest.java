package com.eharmony.pho.hbase.mapper;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.eharmony.pho.hbase.model.EmbededEntityExample;
import com.eharmony.pho.hbase.model.NestedEntity;
import com.eharmony.pho.mapper.EntityPropertiesMappingContext;
import com.eharmony.pho.mapper.EntityPropertiesResolver;
import com.eharmony.pho.mapper.EntityPropertyBinding;

public class EntityPropertiesResolverTest {

    @Test
    public void testMapToEntityProperties() throws ClassNotFoundException {
        NestedEntity nestedClass = new NestedEntity();
        nestedClass.setName("eharmony");
        nestedClass.setNestedClassDescription("property mapper test");

        EmbededEntityExample embdedClassExample = new EmbededEntityExample();
        embdedClassExample.setNestedObject(nestedClass);
        embdedClassExample.setAnnotatedProperty("annotated property value");
        embdedClassExample.setProperty2("second property");

        List<String> entityClassNames = new ArrayList<String>();
        entityClassNames.add("com.eharmony.pho.hbase.model.EmbededEntityExample");
        EntityPropertiesMappingContext mappingContext = new EntityPropertiesMappingContext(entityClassNames);
        EntityPropertiesResolver resolver = new EntityPropertiesResolver(mappingContext);
        EntityPropertyBinding propBinding = resolver.resolveEntityPropertyBindingByStoreMappingName("annotatedProperty",EmbededEntityExample.class);
        Assert.assertNotNull(propBinding);
    }
}
