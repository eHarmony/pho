package com.eharmony.pho.hbase.translator;

import com.eharmony.pho.query.criterion.Symbolic;

public enum PhoenixHBaseFunction implements Symbolic {
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    COUNT("COUNT"),
    AVG("AVG"),
    SUM("SUM"),
    MAX("MAX"),
    MIN("MIN");

    private final String symbol;

    PhoenixHBaseFunction(String symbol) {
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
