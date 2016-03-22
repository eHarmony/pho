package com.eharmony.pho.translator;

/**
 * Description for a type that resolves a datastore specific table/collection name for an entity class
 */
public interface EntityResolver {

    /**
     * Resolve the datastore specific collection / table name for the provided entity class
     * 
     * @param entityClass
     *            Class
     * @return String
     */
    public String resolve(Class<?> entityClass);

}
