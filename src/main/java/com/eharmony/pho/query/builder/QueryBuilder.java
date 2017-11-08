package com.eharmony.pho.query.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.eharmony.pho.query.QueryOperationType;
import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.QuerySelectImpl;
import com.eharmony.pho.query.criterion.*;
import com.eharmony.pho.query.criterion.projection.AggregateProjection;
import com.eharmony.pho.query.criterion.projection.Projection;

/**
 * Builder for Query objects
 *
 * @param <T> the entity type being queried
 * @param <R> the desired return type
 */
public class QueryBuilder<T, R> {

    private final Class<T> entityClass;
    private final Class<R> returnType;
    private List<Criterion> criteria = new ArrayList<>();
    private List<Criterion> groupCriteria = new ArrayList<>();
    private Orderings orderings = new Orderings();
    private List<Projection> projections = new ArrayList<>();
    private Integer maxResults;
    private List<String> returnFields = Collections.emptyList();
    private QueryOperationType queryOperationType;
    private String queryHint;

    public QueryBuilder(Class<T> entityClass, Class<R> returnType) {
        this.entityClass = entityClass;
        this.returnType = returnType;
    }

    public static <T, R> QueryBuilder<T, R> builderFor(Class<T> entityClass, Class<R> returnType,
                                                       String... returnFields) {
        return new QueryBuilder<T, R>(entityClass, returnType).setReturnFields(returnFields);
    }

    public static <T> QueryBuilder<T, T> builderFor(Class<T> entityClass) {
        return new QueryBuilder<T, T>(entityClass, entityClass);
    }

    public QueryBuilder<T, R> select() {
        queryOperationType = QueryOperationType.SELECT;
        return this;
    }

    public QueryBuilder<T, R> select(List<String> returnFields) {
        queryOperationType = QueryOperationType.SELECT;
        this.returnFields = returnFields;
        return this;
    }

    public QueryBuilder<T, R> update() {
        queryOperationType = QueryOperationType.UPDATE;
        return this;
    }

    public QueryBuilder<T, R> update(List<String> selectedFields) {
        queryOperationType = QueryOperationType.UPDATE;
        this.returnFields = selectedFields;
        return this;
    }

    public QueryBuilder<T, R> create() {
        queryOperationType = QueryOperationType.INSERT;
        return this;
    }

    public QueryBuilder<T, R> create(List<String> fields) {
        queryOperationType = QueryOperationType.INSERT;
        this.returnFields = fields;
        return this;
    }

    public QueryBuilder<T, R> delete() {
        queryOperationType = QueryOperationType.DELETE;
        return this;
    }

    public QueryBuilder<T, R> add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    /**
     * Add an ordering to the list of orderings
     *
     * @param orders the list of orderings to add
     * @return the builder
     */
    public QueryBuilder<T, R> addOrder(Ordering... orders) {

        return addOrder(Arrays.asList(orders));

    }

    /**
     * Add an ordering to the list of orderings
     *
     * @param orders the list of orderings to add
     * @return the builder
     */
    public QueryBuilder<T, R> addOrder(Iterable<Ordering> orders) {
        for (Ordering order : orders) {
            orderings.add(order);
        }
        return this;
    }

    public QueryBuilder<T, R> addProjection(Projection... projections) {
        this.projections.addAll(Arrays.asList(projections));
        return this;
    }

    public QueryBuilder<T, R> addGroupCriterion(Criterion groupCriterion) {
//        groupCriteria.addAll(Arrays.asList(groupCriterion));
        groupCriteria.add(groupCriterion);
        return this;
    }

    public QueryBuilder<T, R> setReturnFields(String... returnFields) {
        this.returnFields = Arrays.asList(returnFields);
        return this;
    }

    public QueryBuilder<T, R> setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public QueryBuilder<T, R> setQueryOperationType(QueryOperationType queryOperationType) {
        this.queryOperationType = queryOperationType;
        return this;
    }

    public QueryBuilder<T, R> setQueryHint(String queryHint) {
        this.queryHint = queryHint;
        return this;
    }

    public QuerySelect<T, R> build() {
        // if criteria.size == 0, rootCriterion = null
        Criterion rootCriterion = null;
        if (criteria.size() == 1) {
            rootCriterion = criteria.get(0);
        } else if (criteria.size() > 1) {
            rootCriterion = Restrictions.and(criteria.toArray(new Criterion[criteria.size()]));
        }
        Criterion groupCriterion = null;
        if (groupCriteria.size() == 1) {
            groupCriterion = groupCriteria.get(0);
        } else if (groupCriteria.size() > 1) {
            groupCriterion = Restrictions.and(groupCriteria.toArray(new Criterion[groupCriteria.size()]));
        }
        return new QuerySelectImpl<T, R>(entityClass, returnType, rootCriterion, groupCriterion, orderings, maxResults, returnFields,
                projections, queryOperationType, queryHint);
    }

    @Override
    public String toString() {
        return "QueryBuilder [entityClass=" + entityClass + ", criteria=" + criteria + ", orderings=" + orderings
                + ", maxResults=" + maxResults + "]";
    }

}
