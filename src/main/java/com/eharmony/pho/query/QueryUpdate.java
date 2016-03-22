package com.eharmony.pho.query;

import java.util.List;

import com.eharmony.pho.query.criterion.Criterion;

/**
 * A generic, object oriented representation of update query
 * 
 * @param <T>
 *            the entity type being saved
 */
public interface QueryUpdate<T> {

    /**
     * The list of properties to return. An empty collection means all properties.
     * 
     * @return the fields to be updated
     */
    public List<String> getSelectedFields();

    /**
     * Get the query criteria. The top level object can either be a single Criterion or a Junction of multiple nested
     * Criterion objects.
     * 
     * @return the root criterion node
     */
    public Criterion getCriteria();

    /**
     * Getter method for Query Operation type for update
     * 
     * @return <code>QueryOperationType</code> 
     */
    public QueryOperationType getQueryOperationType();

    /**
     * Get the entity, which is set to persist
     * @return T
     */
    public T getEntity();

}
