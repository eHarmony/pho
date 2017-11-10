package com.eharmony.pho.query.criterion.expression;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Operator;
import com.eharmony.pho.query.criterion.WithOperator;
import com.eharmony.pho.query.criterion.WithProperty;
import com.eharmony.pho.query.criterion.projection.AggregateProjection;

/**
 * An abstract expression with an operator that acts on a property name.
 */
public abstract class Expression implements Criterion, WithOperator, WithProperty {

    private final Operator operator;
    private final String propertyName;
    private final AggregateProjection aggregateProjection;

    protected Expression(Operator operator, String propertyName, AggregateProjection aggregateProjection) {
        this.operator = operator;
        this.propertyName = propertyName;
        this.aggregateProjection = aggregateProjection;
    }

    protected Expression(Operator operator, String propertyName) {
        this.operator = operator;
        this.propertyName = propertyName;
        this.aggregateProjection = null;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    public AggregateProjection getAggregateProjection() {
        return aggregateProjection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
                + ((propertyName == null) ? 0 : propertyName.hashCode());
        result = prime * result
                + ((aggregateProjection == null) ? 0 : aggregateProjection.hashCode());
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
        Expression other = (Expression) obj;
        if (operator != other.operator)
            return false;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName))
            return false;
        if (aggregateProjection == null) {
            if (other.aggregateProjection != null)
                return false;
        } else if (!aggregateProjection.equals(other.aggregateProjection))
            return false;
        return true;
    }

}
