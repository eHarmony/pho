
package com.eharmony.pho.query.criterion.expression;

import com.eharmony.pho.query.criterion.Criterion;

/**
 * A typed, native datastore query component.
 */
public class NativeExpression implements Criterion {

    private final Class<?> expressionClass;
    private final Object expression;

    public <T> NativeExpression(Class<T> expressionClass, T expression) {
        this.expressionClass = expressionClass;
        this.expression = expression;
    }

    public Class<?> getExpressionClass() {
        return expressionClass;
    }

    public Object getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "NativeExpression [" + expression + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result
                + ((expressionClass == null) ? 0 : expressionClass.hashCode());
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
        NativeExpression other = (NativeExpression) obj;
        if (expression == null) {
            if (other.expression != null)
                return false;
        } else if (!expression.equals(other.expression))
            return false;
        if (expressionClass == null) {
            if (other.expressionClass != null)
                return false;
        } else if (!expressionClass.equals(other.expressionClass))
            return false;
        return true;
    }
}
