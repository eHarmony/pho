package com.eharmony.pho.query.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.eharmony.pho.query.QueryOperationType;
import com.eharmony.pho.query.QueryUpdate;
import com.eharmony.pho.query.QueryUpdateImpl;
import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Restrictions;

/**
 * Builder for saving objects
 * 
 * @param <T>
 *            the entity type being saved
 */
public class QueryUpdateBuilder<T> {

    private final T entity;
    private List<Criterion> criteria = new ArrayList<Criterion>();
    private List<String> selectedFields = Collections.emptyList();
    private QueryOperationType queryOperationType = QueryOperationType.UPDATE;

    public QueryUpdateBuilder(final T entity) {
        this.entity = entity;
    }

    public static <T> QueryUpdateBuilder<T> builderFor(final T entity) {
        return new QueryUpdateBuilder<T>(entity);
    }

    public QueryUpdateBuilder<T> create() {
        queryOperationType = QueryOperationType.INSERT;
        return this;
    }

    public QueryUpdateBuilder<T> create(List<String> selectedFields) {
        queryOperationType = QueryOperationType.INSERT;
        this.selectedFields = selectedFields;
        return this;
    }

    public QueryUpdateBuilder<T> update() {
        queryOperationType = QueryOperationType.UPDATE;
        return this;
    }

    public QueryUpdateBuilder<T> update(List<String> selectedFields) {
        queryOperationType = QueryOperationType.UPDATE;
        this.selectedFields = selectedFields;
        return this;
    }

    public QueryUpdateBuilder<T> add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public QueryUpdateBuilder<T> setSelectedFields(String... selectedFields) {
        this.selectedFields = Arrays.asList(selectedFields);
        return this;
    }

    public QueryUpdate<T> build() {
        // if criteria.size == 0, rootCriterion = null
        Criterion rootCriterion = null;
        if (criteria.size() == 1) {
            rootCriterion = criteria.get(0);
        } else if (criteria.size() > 1) {
            rootCriterion = Restrictions.and(criteria.toArray(new Criterion[criteria.size()]));
        }
        return new QueryUpdateImpl<T>(entity, rootCriterion, selectedFields, queryOperationType);
    }

    @Override
    public String toString() {
        return "QueryBuilder [entityClass=" + entity.getClass() + ", criteria=" + criteria + ", selectedFields="
                + selectedFields + "]";
    }

}
