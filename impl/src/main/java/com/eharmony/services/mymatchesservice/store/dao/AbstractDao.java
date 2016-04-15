package com.eharmony.services.mymatchesservice.store.dao;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import com.eharmony.singles.common.data.Persistable;


/**
 * The AbstractDao provides default method implementations for all the Dao
 * contract methods. Since it was decided that we will be using Hibernate and
 * probably never change, AbstractDao implements HibernateDao and therefore is
 * backed by a Hibernate SessionFactory.
 *
 * @param   <K>  The ID/Primary Key type, needs to be Serializable.
 * @param   <T>  The Persistable type.
 */
public abstract class AbstractDao<K extends Serializable, T extends Persistable<K>>
          implements HibernateDao<K, T> {

    /**
     * The Persistent class type that is used with the Hibernate Session to
     * fetch, save/update, and delete persisted objects.
     */
    protected Class<T> persistentClass;

    /** The Hibernate SessionFactory backing this Dao. */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * This class requires that the Persistent Class be specified.
     *
     * @param  persistentClass
     */
    protected AbstractDao(Class<T> persistentClass) {

        this.persistentClass = persistentClass;

    }

    public void delete(T persistentObject) {

        getSession().delete(persistentObject);

    }

    @SuppressWarnings("unchecked")
    public T findByPrimaryKey(K primaryKey) {

        return (T) getSession().get(persistentClass, primaryKey);

    }

    public Class<T> getPersistentClass() {

        return persistentClass;

    }

    public SessionFactory getSessionFactory() {

        return sessionFactory;

    }

    public T save(T persistentObject) {

        getSession().saveOrUpdate(persistentObject);

        return persistentObject;

    }

    /**
     * Sets the backing SessionFactory for this Dao. This method is Autowired
     * for easy Spring integration.
     *
     * @param  sessionFactory
     */
    @Autowired public void setSessionFactory(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;

    }

    /**
     * Creates a Criteria for this DAO with the persistent class. If cache is
     * true, then the criteria will be cacheable with a cache region equal to
     * "[persistent class name]QueryCache".
     *
     * @param   cache
     *
     * @return  Criteria for this DAO with the persistent class
     */
    protected Criteria createCriteria(boolean cache) {

        Criteria criteria = getSession().createCriteria(persistentClass);
        if (cache) {

            criteria.setCacheable(true);
            criteria.setCacheRegion(persistentClass.getName() + "QueryCache");

        }
        return criteria;

    }

    /**
     * Finds all persistent objects of the Persistable type T.
     *
     * @return  all persistent objects of the Persistable type T.
     *
     */
    protected List<T> findAll() {

        Criteria criteria = createCriteria(false);

        return castCriteriaList(criteria);

    }

    /**
     * Method to find a collection of persisted objects by a single property.
     * This method is made protected because it should be used as a helper
     * method, and not made available outside of the DAO realm. The suggested
     * usage of this method is wrapping it inside a more specific method. Eg. If
     * you want to find an AffiliateCampaignVo by it's campaignId, the
     * AffiliateCampaignDAO should declare a method called findByCampaignId,
     * which just uses this helper method.
     *
     * @return  A collection of persisted objects by a single property.
     *
     */
    protected final List<T> findByProperties(Map<String, Object> properties) {

        return findByProperties(properties, false);

    }

    /**
     * Method to find a collection of persisted objects by a single property.
     * This method is made protected because it should be used as a helper
     * method, and not made available outside of the DAO realm. The suggested
     * usage of this method is wrapping it inside a more specific method. Eg. If
     * you want to find an AffiliateCampaignVo by it's campaignId, the
     * AffiliateCampaignDAO should declare a method called findByCampaignId,
     * which just uses this helper method.
     *
     * @return  A collection of persisted objects by a single property.
     *
     */
    protected final List<T> findByProperties(Map<String, Object> properties,
                                             boolean cache) {

        Criteria criteria = createCriteria(cache);
        for (String propertyName : properties.keySet()) {

            criteria.add(Expression.eq(propertyName, properties.get(propertyName)));

        }
        return castCriteriaList(criteria);

    }

    /**
     * Method to find a collection of persisted objects by a map of properties
     * and their values. This method is made protected because it should be used
     * as a helper method, and not made available outside the DAO realm. The
     * suggested usage of this method is wrapping it inside a more specific
     * method. Eg. If you want to find an AffiliateCampaignVo by it's
     * campaignId, and where it is active, the AffiliateCampaignDAO should
     * declare a method called findActiveByCampaignId, which just uses this
     * helper method.
     *
     * @param   propertyName
     * @param   propertyValue
     *
     * @return  A collection of persisted objects by a map of properties and
     *          their values.
     *
     */
    protected final List<T> findByProperty(String propertyName,
                                           Object propertyValue) {

        return findByProperty(propertyName, propertyValue, false);

    }

    /**
     * Method to find a unique persisted objects by map of property/value pairs.
     * This method is made protected because it should be used as a helper
     * method, and not made available outside of the DAO realm. The suggested
     * usage of this method is wrapping it inside a more specific method. Eg. If
     * you want to find an AffiliateCampaignVo by it's campaignId, the
     * AffiliateCampaignDAO should declare a method called findByCampaignId,
     * which just uses this helper method.
     *
     * @return  
     *
     */
    @SuppressWarnings("unchecked")
	protected final T findUniqueByProperties(Map<String, Object> properties, boolean cache) {

        Criteria criteria = createCriteria(cache);
        for (String propertyName : properties.keySet()) {

            criteria.add(Expression.eq(propertyName, properties.get(propertyName)));

        }
        return (T) criteria.uniqueResult();


    }

    
    /**
     * Method to find a collection of persisted objects by a map of properties
     * and their values. This method is made protected because it should be used
     * as a helper method, and not made available outside the DAO realm. The
     * suggested usage of this method is wrapping it inside a more specific
     * method. Eg. If you want to find an AffiliateCampaignVo by it's
     * campaignId, and where it is active, the AffiliateCampaignDAO should
     * declare a method called findActiveByCampaignId, which just uses this
     * helper method.
     *
     * @param   propertyName
     * @param   propertyValue
     *
     * @return  A collection of persisted objects by a map of properties and
     *          their values.
     *
     */

    protected final List<T> findByProperty(String propertyName,
                                           Object propertyValue,
                                           boolean cache) {

        Criteria criteria = createCriteria(cache);
        criteria.add(Expression.eq(propertyName, propertyValue));
        return castCriteriaList(criteria);

    }

    /**
     * Gets the current Session from the backing SessionFactory. This method
     * assumes that the SessionFactory has been primed with a Session, either
     * through Spring or some other transaction management API.
     *
     * @return  The current Session from the backing SessionFactory.
     */
    protected Session getSession() {

        return getSessionFactory().getCurrentSession();

    }

    @SuppressWarnings("unchecked")
    protected List<T> castCriteriaList(Criteria criteria) {

        return criteria.list();

    }

}

