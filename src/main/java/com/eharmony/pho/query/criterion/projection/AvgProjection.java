package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

public class AvgProjection extends AggregateProjection{
    public AvgProjection(String propertyName) {
        super(Aggregate.AVG, propertyName);
    }
}
