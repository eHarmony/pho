package com.eharmony.pho.query.criterion.expression;

import com.eharmony.pho.query.criterion.Operator;

/**
 * A unary expression (is null, not null, is empty, not empty)
 */
public class UnaryExpression extends Expression {

    public UnaryExpression(Operator operator, String propertyName) {
        super(operator, propertyName);
    }

    @Override
    public String toString() {
        return getPropertyName() + " " + getOperator();
    }

}
