package com.eharmony.pho.query.criterion.junction;

import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Operator;

/**
 * Or
 */
public class Disjunction extends Junction {

    public Disjunction() {
        super(Operator.OR);
    }

    public Disjunction(Criterion... criteria) {
        this();
        addAll(criteria);
    }

}