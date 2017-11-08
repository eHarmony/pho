package com.eharmony.pho.query.criterion;

import com.google.common.collect.ImmutableMap;

/**
 * Query operator type enumeration
 */
public enum Aggregate implements Symbolic {

    GROUP_BY("group by"),
    COUNT("count"),
    AVG("avg"),
    SUM("sum"),
    MAX("max"),
    MIN("min");

    private final String symbol;
    private static final ImmutableMap<String, Aggregate> map = SymbolicLookup.map(values());

    private Aggregate(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String symbol() {
        return symbol;
    }

    public static Aggregate fromString(String val) {
        return SymbolicLookup.resolve(val, map, Aggregate.class);
    }

    @Override
    public String toString() {
        return symbol();
    }
}
