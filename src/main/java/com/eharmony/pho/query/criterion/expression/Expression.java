package com.eharmony.pho.query.criterion.expression;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Operator;
import com.eharmony.pho.query.criterion.WithOperator;
import com.eharmony.pho.query.criterion.WithProperty;

/**
 * An abstract expression with an operator that acts on a property name.
 */
public abstract class Expression implements Criterion, WithOperator, WithProperty {

    private final Operator operator;
    private final String propertyName;

    protected Expression(Operator operator, String propertyName) {
        this.operator = operator;
        this.propertyName = propertyName;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result
                + ((propertyName == null) ? 0 : propertyName.hashCode());
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
        return true;
    }

}
