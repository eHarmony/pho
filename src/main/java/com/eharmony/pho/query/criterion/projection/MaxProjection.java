package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

public class MaxProjection extends AggregateProjection {
    public MaxProjection(String propertyName) {
        super(Aggregate.MAX, propertyName);
    }
}
