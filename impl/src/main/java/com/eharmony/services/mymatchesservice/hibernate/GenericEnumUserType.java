/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2015 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

//got from https://raw.githubusercontent.com/openmrs/openmrs-module-reporting/master/api/src/main/java/org/openmrs/module/reporting/report/service/db/GenericEnumUserType.java
//and implemented nulSafeGet and nullSafeSet to work with hibernate4


/**
 * Taken primarily from https://www.hibernate.org/272.html Written by Martin
 * Kersten and tweaked by Gavin King Updated by Darius Jazayeri to (hackily)
 * support both Hibernate 3.2.5 and 3.6
 */
public class GenericEnumUserType
          implements UserType,
                     ParameterizedType {

    private static final String DEFAULT_FROM_INT_METHOD_NAME = "fromInt";
    private static final String DEFAULT_TO_INT_METHOD_NAME = "toInt";

    private Class<? extends Enum> enumClass;
    private Method fromIntMethod;
    private Method toIntMethod;
    private Class<?> identifierType = int.class;
    private Class<Integer> intIdentifierType = int.class;
    private Class<String> stringIdentifierType = String.class;

    private int[] sqlTypes;
    private Type type;

    public Object assemble(Serializable cached,
                           Object owner)
        throws HibernateException {

        return cached;

    }

    public Object deepCopy(Object value)
        throws HibernateException {

        return value;

    }

    public Serializable disassemble(Object value)
        throws HibernateException {

        return (Serializable) value;

    }

    public boolean equals(Object x,
                          Object y)
        throws HibernateException {

        return x == y;

    }

    public int hashCode(Object x)
        throws HibernateException {

        return x.hashCode();

    }

    public boolean isMutable() {

        return false;

    }

    @Override public Object nullSafeGet(ResultSet rs,
                                        String[] names,
                                        SessionImplementor session,
                                        Object owner)
        throws HibernateException, SQLException {

        Object identifier;
        try {

            identifier =
                type.getClass()
                    .getMethod("get", ResultSet.class, String.class, SessionImplementor.class)
                    .invoke(type, rs, names[0], session);

        } catch (Exception ex) {

            throw new RuntimeException("Error executing get method on " + type, ex);

        }
        if (rs.wasNull()) {

            return null;

        }

        try {

            return fromIntMethod.invoke(enumClass, identifier);

        } catch (Exception e) {

            throw new HibernateException("Exception while invoking fromtInt method '" + fromIntMethod.getName() +
                                         "' of " + "enumeration class '" + enumClass + "'", e);

        }

    }

    @Override public void nullSafeSet(PreparedStatement st,
                                      Object value,
                                      int index,
                                      SessionImplementor session)
        throws HibernateException, SQLException {
       
        try {

            if (value == null) {

                st.setNull(index, HibernateUtil.sqlType(type));
            
            } else {

                Integer intValue = (Integer) toIntMethod.invoke(value);
                
                type.getClass()
                    .getMethod("set", PreparedStatement.class, Object.class, int.class, SessionImplementor.class)
                    .invoke(type, st, intValue, index, session);

            }

        } catch (Exception e) {

            throw new HibernateException("Exception while invoking identifierMethod '" + toIntMethod.getName() +
                                         "' of " + "enumeration class '" + enumClass + "'", e);
        	
        }

    }

    public Object replace(Object original,
                          Object target,
                          Object owner)
        throws HibernateException {

        return original;

    }

    public Class returnedClass() {

        return enumClass;

    }

    public void setParameterValues(Properties parameters) {

        String enumClassName = parameters.getProperty("enumClass");
        try {

            enumClass = Class.forName(enumClassName)
                             .asSubclass(Enum.class);

        } catch (ClassNotFoundException cfne) {

            throw new HibernateException("Enum class not found", cfne);

        }

        type = HibernateUtil.getBasicType(identifierType.getName(), parameters);

        if (type == null) {

            throw new HibernateException("Unsupported identifier type " + identifierType.getName());

        }

        sqlTypes = new int[] { HibernateUtil.sqlType(type) };

        String fromIntMethodName = parameters.getProperty("fromIntMethod", DEFAULT_FROM_INT_METHOD_NAME);

        try {

            fromIntMethod = enumClass.getMethod(fromIntMethodName, new Class[] { intIdentifierType });

        } catch (Exception e) {

            throw new HibernateException("Failed to obtain fromInt method", e);

        }
        
        String toIntMethodName = parameters.getProperty("toIntMethod", DEFAULT_TO_INT_METHOD_NAME);

        try {

        	toIntMethod = enumClass.getMethod(toIntMethodName);

        } catch (Exception e) {

            throw new HibernateException("Failed to obtain toInt method", e);

        }

    }
    
    public int[] sqlTypes() {

        return sqlTypes;

    }

}
