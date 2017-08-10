
package com.eharmony.pho.query.criterion.expression;

import com.eharmony.pho.query.criterion.Operator;

/**
 * A closed range expression.
 */
public class RangeExpression extends Expression {

    private final Object from;
    private final Object to;

    public RangeExpression(Operator operator, String propertyName, Object from,
            Object to) {
        super(operator, propertyName);
        this.from = from;
        this.to = to;
    }

    public Object getFrom() {
        return from;
    }

    public Object getTo() {
        return to;
    }

    @Override
    public String toString() {
        return getPropertyName() + " " + getOperator() + " " + from + "," + to;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
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
        RangeExpression other = (RangeExpression) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }
}
