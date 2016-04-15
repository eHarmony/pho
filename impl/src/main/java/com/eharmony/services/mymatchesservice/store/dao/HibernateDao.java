package com.eharmony.services.mymatchesservice.store.dao;

import java.io.Serializable;

import org.hibernate.SessionFactory;

import com.eharmony.singles.common.data.Persistable;


/**
 * HibernateDao defines some Hibernate specific methods that all Dao's using a
 * Hibernate Session, or SessionFactory need to supply.
 *
 * @author  eberry
 *
 * @param   <K>  The ID/Primary Key type, needs to be Serializable.
 * @param   <T>  The Persistable type.
 */
public interface HibernateDao<K extends Serializable, T extends Persistable<K>>
          extends Dao<K, T> {

    /**
     * Get the Persistent class type for this Dao.
     *
     * @return  the Persistent class type for this Dao.
     */
    public Class<T> getPersistentClass();

    /**
     * Get the SessionFactory backing this Dao.
     *
     * @return  the SessionFactory backing this Dao.
     */
    public SessionFactory getSessionFactory();

}

