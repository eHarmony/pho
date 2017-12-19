package com.eharmony.pho.hbase.translator;

import com.eharmony.pho.query.criterion.Symbolic;

public enum PhoenixHBaseAggregate implements Symbolic {

    GROUP_BY("GROUP BY"),
    COUNT("COUNT"),
    AVG("AVG"),
    SUM("SUM"),
    MAX("MAX"),
    MIN("MIN");

    private final String symbol;

    PhoenixHBaseAggregate(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String symbol() {
        return symbol;
    }


    @Override
    public String toString() {
        return symbol();
    }
}
