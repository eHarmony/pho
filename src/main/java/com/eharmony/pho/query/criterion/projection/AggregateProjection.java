package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

public class AggregateProjection extends Projection {
    protected AggregateProjection(Aggregate function, String propertyName) {
        super(function, propertyName);
    }
}
