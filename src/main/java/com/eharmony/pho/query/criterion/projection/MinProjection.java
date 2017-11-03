package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

public class MinProjection extends AggregateProjection {
    public MinProjection(String propertyName) {
        super(Aggregate.MIN,  propertyName);
    }
}
