package com.eharmony.pho.hbase.translator;

import com.eharmony.pho.query.criterion.Symbolic;

/**
 * Mappings for Apache Phoenix HBase query operators 
 */
public enum PhoenixHBaseOperator implements Symbolic {
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"), 
    GREATER_THAN_OR_EQUAL(">="), 
    LESS_THAN("<"), 
    LESS_THAN_OR_EQUAL("<="),
    
    OR("OR"),
    AND("AND"),
    
    LIKE("LIKE"),
    LIKE_CASE_INSENSITIVE("ILIKE"),
    
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL");

    private final String symbol;

    private PhoenixHBaseOperator(String symbol) {
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
