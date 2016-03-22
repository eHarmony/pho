package com.eharmony.pho.query.criterion.junction;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Operator;

/**
 * And
 */
public class Conjunction extends Junction {

    public Conjunction() {
        super(Operator.AND);
    }

    public Conjunction(Criterion... criteria) {
        this();
        addAll(criteria);
    }
}
