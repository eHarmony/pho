package com.eharmony.pho.translator;

/**
 * The Simple Entity Resolver uses the simple name of the class for the table/collection name
 */
public class SimpleEntityResolver implements EntityResolver {

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.translator.EntityResolver#resolve(java.lang.Class)
     */
    // @Override
    public String resolve(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

}
