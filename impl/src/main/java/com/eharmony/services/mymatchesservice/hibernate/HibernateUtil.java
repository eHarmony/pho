/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package com.eharmony.services.mymatchesservice.hibernate;

// gotten from : https://github.com/openmrs/openmrs-module-reporting/blob/master/api/src/main/java/org/openmrs/module/reporting/common/HibernateUtil.java

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;

/**
 * A utility class for Hibernate-related functionality
 */
public class HibernateUtil {
	
	/**
	 * Hibernate made a non-backwards-compatible change in version 3.6 (which we use starting in
	 * OpenMRS 1.9). See https://hibernate.onjira.com/browse/HHH-5138.
	 * For example Hibernate.STRING is now StandardBasicTypes.STRING.
	 * 
	 * @param typeName name of type to convert to modern type.
	 * @return the org.hibernate.type.Type, fetched as a static constant from either the Hibernate class or
	 * the StandardBasicTypes class, depending on which is available.
	 */
	public static Type standardType(String typeName) {
		try {
            try {
            	Field field = Hibernate.class.getField(typeName);
				return (Type) field.get(null);
            }
            catch (NoSuchFieldException ex) {
                Field field =
                    HibernateUtil.class.getClassLoader()
                                       .loadClass("org.hibernate.type.StandardBasicTypes")
                                       .getField(typeName);
                return (Type) field.get(null);
            }
		} catch (Exception ex) {
			throw new RuntimeException("Cannot get Hibernate type: " + typeName, ex);
		}
	}

	/**
	 * Hibernate made a non-backwards-compatible change in version 3.6 (which we use starting in
	 * OpenMRS 1.9). See https://hibernate.onjira.com/browse/HHH-5138.
	 * TypeFactory.basic no longer exists.
	 * (I don't know if we need both this method and {@link #standardType(String)}, but I'm hackily
	 * replacing bits of code that I don't understand deeply, and they do two things.)
	 * @param name  the name of the type to create
	 * @param parameters hints for heuristic type creator
     * @return Given the name of a Hibernate basic type, return an instance of org.hibernate.type.Type
     */
    public static Type getBasicType(String name, Properties parameters) {
    	try {
    		// use reflection to do: return TypeFactory.basic(name);
    		Class<?> clazz = HibernateUtil.class.getClassLoader().loadClass("org.hibernate.type.TypeFactory");
    		Method method = clazz.getMethod("basic", String.class);
    		return (Type) method.invoke(null, name);
    	} catch (Exception ex) {
    		// use reflection to do: return new TypeResolver().heuristicType(name, parameters);
			try {
	    		Object typeResolver = HibernateUtil.class.getClassLoader().loadClass("org.hibernate.type.TypeResolver").newInstance();
	    		Method method = typeResolver.getClass().getMethod("heuristicType", String.class, Properties.class);
	    		return (Type) method.invoke(typeResolver, name, parameters);
			} catch (Exception e) {
				throw new RuntimeException("Error getting hibernate type", e);
			}
    	}
    }

	/**
     * Hibernate made a non-backwards-compatible change in version 3.6 (which we use starting in
	 * OpenMRS 1.9). See https://hibernate.onjira.com/browse/HHH-5138.
     * There are a few places where in 3.2.5 we'd have a NullableType, but in 3.6 we'll have something
     * like a AbstractSingleColumnStandardBasicType, but either way there's a sqlType() method
     * 
     * @param type the Hibernate type
     * @return The JDBC type associated with the given Hibernate type
     */
    public static Integer sqlType(Type type) {
    	try {
	        return (Integer) type.getClass().getMethod("sqlType").invoke(type);
        }
        catch (Exception ex) {
	        throw new RuntimeException("Error calling sqlType() method on " + type, ex);
        }
    }
}
