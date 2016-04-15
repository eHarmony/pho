package com.eharmony.services.mymatchesservice.store.dao;

import java.io.Serializable;

import com.eharmony.singles.common.data.Persistable;


/**
 * Defines the contract for all eHarmony 3.0 Daos to follow.
 *
 * @param   <K>  The ID/Primary Key type, must be Serializable.
 * @param   <T>  The Persistable type.
 */
public interface Dao<K extends Serializable, T extends Persistable<K>> {

    /**
     * Deletes the given persistentObject.
     *
     * @param   persistentObject
     *
     * @throws  RuntimeException if there is a DB issue.
     */
    public void delete(T persistentObject);

    /**
     * Finds the specific Persistable for the specific ID/Primary Key.
     *
     * @param   primaryKey
     *
     * @return  the specific Persistable for the specific ID/Primary Key.
     *
     */
    public T findByPrimaryKey(K primaryKey);

    /**
     * Saves or updates the given Persistable.
     *
     * @param   persistentObject
     *
     * @return  The persisted version of the given persistent object. This
     *          usually results in the ID/Primary Key field being set on "new"
     *          objects.
     *
     */
    public T save(T persistentObject);

}
