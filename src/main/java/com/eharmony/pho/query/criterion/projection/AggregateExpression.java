package com.eharmony.pho.query.criterion.projection;

import com.eharmony.pho.query.criterion.Aggregate;

public class AggregateExpression  extends Projection{
    protected AggregateExpression(Aggregate function, String propertyName) {
        super(function, propertyName);
    }
}
