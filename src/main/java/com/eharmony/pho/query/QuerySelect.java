package com.eharmony.pho.query;

import java.util.List;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Orderings;

/**
 * A generic, object oriented representation of a query
 * 
 * @param <T>
 *            the entity type being queried
 * @param <R>
 *            the desired return type
 */
public interface QuerySelect<T, R> {

    /**
     * Get the queried entity type.
     * 
     * @return the entity class being queried
     */
    public Class<T> getEntityClass();

    /**
     * Get the return entity type.
     * 
     * @return the class of the desired return type
     */
    public Class<R> getReturnType();

    /**
     * The list of properties to return. An empty collection means all properties.
     * 
     * @return the fields to be returned from the query
     */
    public List<String> getReturnFields();

    /**
     * Get the query criteria. The top level object can either be a single Criterion or a Junction of multiple nested
     * Criterion objects.
     * 
     * @return the root criterion node
     */
    public Criterion getCriteria();

    /**
     * Get the Order clauses.
     * 
     * @return the ordering clauses
     */
    public Orderings getOrder();

    /**
     * Get the max desired results. Null signifies no maximum.
     * 
     * @return the maximum number of results or null if no maximum
     * 
     */
    public Integer getMaxResults();

    /**
     * Getter method for Query Operation type
     * 
     * @return <code>QueryOperationType</code> 
     */
    public QueryOperationType getQueryOperationType();

    /**
     * Getter method for Query Hint
     *
     * @return <code>String</code>
     */
    public String getQueryHint();

}
