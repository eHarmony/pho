package com.eharmony.pho.translator;

import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.QueryUpdate;

/**
 * Description of a class that translates a generic Query to a datastore specific query
 * 
 * @param <Q>
 *            the query type
 * @param <O>
 *            the ordering type
 * @param <P>
 *            the projected type
 */
public interface QueryTranslator<Q, O, P> {

    /**
     * Translate the generic Query
     * 
     * @param <T>
     *            entity Type
     * @param <R>
     *            return type
     * @param query
     *            QuerySelect
     * @return Q
     */
    public <T, R> Q translate(QuerySelect<T, R> query);

    /**
     * Translate the generic Orderings
     * 
     * @param <T>
     *            entity Type
     * @param <R>
     *            return type
     * @param query
     *            QuerySelect
     * @return O
     */
    public <T, R> O translateOrder(QuerySelect<T, R> query);

    /**
     * Translate the "Projections"
     * 
     * @param <T>
     *            entity Type
     * @param <R>
     *            return type
     * @param query
     *            QuerySelect
     * @return P
     */
    public <T, R> P translateProjection(QuerySelect<T, R> query);

    /**
     * Translate the generic Query
     * 
     * @param <T>
     *            entity Type
     * @param updateQuery
     *            QueryUpdate
     * @return Q
     */
    public <T> Q translate(QueryUpdate<T> updateQuery);

}
