package com.eharmony.pho.api;

import java.util.List;

import com.eharmony.pho.query.QuerySelect;
/**
 * Provides methods to operate with datastore.
 * @author vvangapandu
 *
 */
public interface DataStoreApi {

    /**
     * Saves given item to store.
     * 
     * @param <T>
     *            class type
     * @param entity
     *            T
     * @return T
     */

    <T> T save(T entity);

    /**
     * Saves all items in iterable, saves will be issues item by item
     * 
     * @param <T>
     *            class type
     * @param entities
     *            Iterable
     * @return Iterable
     */
    <T> Iterable<T> save(Iterable<T> entities);

    /**
     * Saves all items in iterable in batches, batch size is same as number of items in the Iterable.
     * 
     * @param <T>
     *            entity class
     * @param entities
     *            Iterable
     * @return Iterable
     */
    <T> int[] saveBatch(Iterable<T> entities);

    /**
     * Find records that satisfy the provided query.
     *
     * @param <T>
     *            class type
     * @param <R> return param type
     * @param query
     *            Query
     * @return an {@link Iterable} of entity type T
     *
     * @throws DataStoreException
     *             if an error occurs accessing the underlying data store
     */
    <T, R> Iterable<R> findAll(QuerySelect<T, R> query);

    /**
     * Find one record that satisfies the provided query.
     * 
     * @param <T>
     *            class type
     * @param <R> return param type
     * @param query
     *            Query
     * @return an object of entity type T
     *
     * @throws DataStoreException
     *             if an error occurs accessing the underlying data store
     */
    <T, R> R findOne(QuerySelect<T, R> query);

    /**
     * Updates an existing entity, but only for the selected fields.
     * @param <T>
     *            class type
     * @param entity			existing entity whose fields need to be updated.
     * @param selectedFields	list of property names that need to be udpated.
     * @return					updated entity.
     */
    <T> T save(T entity, List<String> selectedFields);
    
}
