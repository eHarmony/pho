package com.eharmony.pho.query.criterion.junction;

import java.util.ArrayList;
import java.util.List;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Operator;
import com.eharmony.pho.query.criterion.WithOperator;
import com.google.common.base.Joiner;

/**
 * and / or
 */
public abstract class Junction implements Criterion, WithOperator {

    private final List<Criterion> criteria = new ArrayList<Criterion>();
    private final Operator operator;

    protected Junction(Operator operator) {
        this.operator = operator;
    }

    public Junction add(Criterion criterion) {
        criteria.add(criterion);
        return this;
    }

    public Junction addAll(Criterion... criterions) {
        for (Criterion criterion : criterions) {
            if (criterion != null) {
                criteria.add(criterion);
            }
        }
        return this;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    public List<Criterion> getCriteria() {
        // TODO : immutable list?
        return new ArrayList<Criterion>(criteria);
    }

    @Override
    public String toString() {
        return "(" + Joiner.on(") " + operator.symbol() + " (").join(criteria) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((criteria == null) ? 0 : criteria.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Junction other = (Junction) obj;
        if (criteria == null) {
            if (other.criteria != null)
                return false;
        } else if (!criteria.equals(other.criteria))
            return false;
        if (operator != other.operator)
            return false;
        return true;
    }

}
