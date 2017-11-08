package com.eharmony.pho.query;

import java.util.List;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.GroupCriterion;
import com.eharmony.pho.query.criterion.Orderings;
import com.eharmony.pho.query.criterion.projection.Projection;

/**
 * The default implementation of the generic Query interface
 * 
 * @param <T>
 *            the entity type being queried
 * @param <R>
 *            the desired return type
 */
public class QuerySelectImpl<T, R> implements QuerySelect<T, R> {

    private final Class<T> entityClass;
    private final Class<R> returnType;
    private final Criterion criteria;
    private final Criterion groupCriterion;
    private final Orderings orderings;
    private final Integer maxResults;
    private final List<String> returnFields;
    private final List<Projection> projections;
    private final QueryOperationType queryOperationType;
    private final String queryHint;

    public QuerySelectImpl(Class<T> entityClass, Class<R> returnType, Criterion criteria, Criterion groupCriterion, Orderings orderings,
                           Integer maxResults, List<String> returnFields, List<Projection> projections, QueryOperationType queryOperationType, String queryHint) {
        this.entityClass = entityClass;
        this.returnType = returnType;
        this.criteria = criteria;
        this.groupCriterion = groupCriterion;
        this.returnFields = returnFields;
        this.orderings = orderings;
        this.maxResults = maxResults;
        this.projections = projections;
        this.queryOperationType = queryOperationType;
        this.queryHint = queryHint;
    }

    @Override
    public QueryOperationType getQueryOperationType() {
        return queryOperationType;
    }

    @Override
    public String getQueryHint() {
        return queryHint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.query.Query#getEntityClass()
     */
    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.query.Query#getReturnType()
     */
    @Override
    public Class<R> getReturnType() {
        return returnType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.query.Query#getReturnFields()
     */
    @Override
    public List<String> getReturnFields() {
        return returnFields;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.query.Query#getCriteria()
     */
    @Override
    public Criterion getCriteria() {
        return criteria;
    }

    @Override
    public Criterion getGroupCriteria() {
        return groupCriterion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.query.Query#getOrder()
     */
    @Override
    public Orderings getOrder() {
        return orderings;
    }

    @Override
    public List<Projection> getProjection() {
        return this.projections;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.eharmony.matching.seeking.query.Query#getMaxResults()
     */
    @Override
    public Integer getMaxResults() {
        return maxResults;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QueryImpl [entityClass=" + entityClass + ", criteria=" + criteria + ", orderings=" + orderings
                + ", maxResults=" + maxResults + "]";
    }
}
