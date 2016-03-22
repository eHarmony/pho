
package com.eharmony.pho.hbase.mapper;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.pho.mapper.EntityPropertiesResolver;
import com.eharmony.pho.mapper.EntityPropertyBinding;
import com.eharmony.pho.mapper.ProjectedResultMapper;
import com.eharmony.pho.query.QuerySelect;
/**
 * Maps the results from hbase to entity object based on entity annotations.
 * 
 * @author vvangapandu
 *
 */
public class PhoenixProjectedResultMapper {
    
    private final ProjectedResultMapper mapper;
    private final EntityPropertiesResolver entityPropertiesResolver;
    
    private static final Logger log = LoggerFactory.getLogger(PhoenixProjectedResultMapper.class);
    
    public PhoenixProjectedResultMapper(final EntityPropertiesResolver entityPropertiesResolver) {
        this.mapper = new ProjectedResultMapper();
        this.entityPropertiesResolver = entityPropertiesResolver;
    }
    
    protected <T, R> String[] returnFields(QuerySelect<T, R> query) {
        List<String> list = query.getReturnFields();
        String[] array = new String[list.size()];
        return list.toArray(array);
    }
    
    public <T, R> R mapResult(Object o, QuerySelect<T, R> query, String[] returnFields) {
        return query.getEntityClass().equals(query.getReturnType())
                ? query.getReturnType().cast(o)
                : mapper.mapTo(query.getReturnType(), o, returnFields);
    }
    
    public <T, R> R mapResult(Object o, QuerySelect<T, R> query) {
        return mapResult(o, query, returnFields(query));
    }
    
    public <T, R> List<R> mapResults(List<? extends Object> objects, final QuerySelect<T, R> query) {
        final String[] returnFields = returnFields(query);
        List<R> returnList = new ArrayList<R>();
        for(Object o: objects) {
            returnList.add(mapResult(o, query, returnFields));
        }
        return returnList;
    }
    
    public <T, R> Iterable<R> mapResults(ResultSet resultSet, final QuerySelect<T, R> query) throws SQLException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        return mapResults(resultSet, query.getReturnType());
    }

    public <R> Iterable<R> mapResults(ResultSet resultSet, final Class<R> clz) throws SQLException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Set<String> metadataColumns = extractColumnNames(resultSet);
        List<R> resultsList = new ArrayList<R>();
        boolean resultIsNumber = Number.class.isAssignableFrom(clz);
        while (resultSet.next()) {
            R instance = null;
            if (!resultIsNumber) {
                instance = clz.newInstance();
            }
            for (String columnName : metadataColumns) {
                Object value = resultSet.getObject(columnName);
                if (value != null) {
                    log.debug(value.toString());
                    if (resultIsNumber) {
                        instance = (R) value;
                        break;
                    }
                    
                    EntityPropertyBinding entityProperty = entityPropertiesResolver.resolveEntityPropertyBindingByStoreMappingName(columnName, clz);
                    if (entityProperty != null) {
                        BeanUtils.copyProperty(instance, entityProperty.getNameFullPath(), value);
                    }

                }
            }
            resultsList.add(instance);
        }

        return resultsList;
    }
    
    private Set<String> extractColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metedata = resultSet.getMetaData();
        int columnCount = metedata.getColumnCount();
        Set<String> metadataColumns = new HashSet<String>();
        for(int i=0;i< columnCount;i++) {
            try {
                metadataColumns.add(metedata.getColumnName(i + 1));
            } catch(Exception ex) {
                log.warn("Exception while reading the metadata for class...");
            }
        }
        return metadataColumns;
    }
    

}
