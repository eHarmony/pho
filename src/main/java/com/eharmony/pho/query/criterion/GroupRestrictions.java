package com.eharmony.pho.query.criterion;

import com.eharmony.pho.query.criterion.expression.*;
import com.eharmony.pho.query.criterion.junction.Conjunction;
import com.eharmony.pho.query.criterion.junction.Disjunction;
import com.eharmony.pho.query.criterion.projection.AggregateProjection;

import java.util.Collection;

/**
 * Hibernate style Restriction expression builder
 */
public class GroupRestrictions {

    private GroupRestrictions() {
    }

    public static EqualityExpression eq(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.EQUAL, projection.getPropertyName(), value, projection);
    }

    public static EqualityExpression ne(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.NOT_EQUAL, projection.getPropertyName(), value);
    }

    public static EqualityExpression lt(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.LESS_THAN, projection.getPropertyName(), value);
    }

    public static EqualityExpression like(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.LIKE, projection.getPropertyName(), value);
    }

    public static EqualityExpression ilike(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.ILIKE, projection.getPropertyName(), value);
    }

    public static EqualityExpression lte(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.LESS_THAN_OR_EQUAL, projection.getPropertyName(), value);
    }

    public static EqualityExpression gt(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.GREATER_THAN, projection.getPropertyName(), value);
    }

    public static EqualityExpression gte(AggregateProjection projection, Object value) {
        return new EqualityExpression(Operator.GREATER_THAN_OR_EQUAL, projection.getPropertyName(), value);
    }

    public static RangeExpression between(AggregateProjection projection, Object from, Object to) {
        return new RangeExpression(Operator.BETWEEN, projection.getPropertyName(), from, to);
    }

    public static SetExpression discreteRange(AggregateProjection projection, int from, int to) {
        return new SetExpression(Operator.IN, projection.getPropertyName(), from <= to ? Restrictions.range(from, to) : Restrictions.range(to, from));
    }

    public static SetExpression in(AggregateProjection projection, Object[] values) {
        return new SetExpression(Operator.IN, projection.getPropertyName(), values);
    }

    public static SetExpression in(AggregateProjection projection, Collection<? extends Object> values) {
        return in(projection, values.toArray());
    }

    public static SetExpression notIn(AggregateProjection projection, Object[] values) {
        return new SetExpression(Operator.NOT_IN, projection.getPropertyName(), values);
    }

    public static SetExpression notIn(AggregateProjection projection, Collection<? extends Object> values) {
        return notIn(projection, values.toArray());
    }

    public static UnaryExpression isNull(AggregateProjection projection) {
        return new UnaryExpression(Operator.NULL, projection.getPropertyName());
    }

    public static UnaryExpression isNotNull(AggregateProjection projection) {
        return new UnaryExpression(Operator.NOT_NULL, projection.getPropertyName());
    }

    public static UnaryExpression isEmpty(AggregateProjection projection) {
        return new UnaryExpression(Operator.EMPTY, projection.getPropertyName());
    }

    public static UnaryExpression isNotEmpty(AggregateProjection projection) {
        return new UnaryExpression(Operator.NOT_EMPTY, projection.getPropertyName());
    }
}
