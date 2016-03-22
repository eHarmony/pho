package com.eharmony.pho.hbase.translator;

import com.eharmony.pho.query.criterion.Symbolic;

/**
 * Mappings for Apache Phoenix HBase Clauses  
 */
public enum PhoenixHBaseClauses implements Symbolic {
    FROM("FROM"),
    WHERE("WHERE"),
    ORDER_BY("ORDER BY"), 
    LIMIT("LIMIT");

    private final String symbol;

    private PhoenixHBaseClauses(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
    
    @Override
    public String toString() {
        return symbol();
    }

}
