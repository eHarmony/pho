package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

public class CountProjection extends AggregateProjection{
    public CountProjection(String propertyName) {
        super(Aggregate.COUNT, propertyName);
    }
}
