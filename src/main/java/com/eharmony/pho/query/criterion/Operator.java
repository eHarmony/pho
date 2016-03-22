package com.eharmony.pho.query.criterion;

import com.google.common.collect.ImmutableMap;

/**
 * Query operator type enumeration
 */
public enum Operator implements Symbolic {
    EQUAL("="), NOT_EQUAL("!="), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL(
            "<="),

    BETWEEN("between"),

    NULL("null"), NOT_NULL("not null"), EMPTY("empty"), NOT_EMPTY("not empty"),

    IN("in"), NOT_IN("not in"), CONTAINS("contains"),

    WITHIN("within"),

    AND("and"), OR("or");

    private final String symbol;
    private static final ImmutableMap<String, Operator> map = SymbolicLookup.map(values());

    private Operator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static Operator fromString(String val) {
        return SymbolicLookup.resolve(val, map, Operator.class);
    }

    @Override
    public String toString() {
        return symbol();
    }
}
