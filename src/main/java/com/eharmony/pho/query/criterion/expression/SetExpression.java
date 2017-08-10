
package com.eharmony.pho.query.criterion.expression;

import java.util.Arrays;

import com.eharmony.pho.query.criterion.Operator;
import com.google.common.base.Joiner;

/**
 * A set expression (in, contains, etc.).
 */
public class SetExpression extends Expression {

    private final Object[] values;

    public SetExpression(Operator operator, String propertyName,
            final Object[] values) {
        super(operator, propertyName);
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return getPropertyName() + " " + getOperator() + " ["
                + Joiner.on(',').join(values) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SetExpression other = (SetExpression) obj;
        if (!Arrays.equals(values, other.values))
            return false;
        return true;
    }
}
