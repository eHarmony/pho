package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.*;

import java.util.Arrays;
import java.util.List;

public class Projection implements Criterion, WithAggregateFunction {
    private final Aggregate function;
    private final List<String> propertyNames;

    protected Projection(Aggregate function, String... propertyNames) {
        this.function = function;
        this.propertyNames = Arrays.asList(propertyNames);
    }

    @Override
    public Aggregate getOperator() {
        return this.function;
    }

    public List<String> getPropertyNames() {
        return this.propertyNames;
    }
}
