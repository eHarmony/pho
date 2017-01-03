package com.eharmony.pho.hbase.translator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.pho.api.DataStoreException;
import com.eharmony.pho.hbase.util.PhoenixDateFormatUtil;
import com.eharmony.pho.mapper.EntityPropertiesResolver;
import com.eharmony.pho.mapper.EntityPropertyBinding;
import com.eharmony.pho.mapper.EntityPropertyValueBinding;
import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.QueryUpdate;
import com.eharmony.pho.query.criterion.Criterion;
import com.eharmony.pho.query.criterion.Ordering;
import com.eharmony.pho.query.criterion.Orderings;
import com.eharmony.pho.query.criterion.Ordering.NullOrdering;
import com.eharmony.pho.query.criterion.Ordering.Order;
import com.eharmony.pho.query.criterion.expression.NativeExpression;
import com.eharmony.pho.translator.AbstractQueryTranslator;
import com.eharmony.pho.translator.QueryTranslator;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Translates the entity to Phoenix query to execute on HBase.
 * 
 * 1. Entity classes must be registered through constructor. 2. SELECT, DELETE and UPSERT queries supported. 3.
 * provided support to escape the special characters while querying and inserting the data ("'", "/" etc..) 4. No
 * support for joins and sub-queries are not tested
 * 
 * @author vvangapandu
 *
 */
public class PhoenixHBaseQueryTranslator extends AbstractQueryTranslator<String, String, String> implements
        QueryTranslator<String, String, String> {
    
    private final MorphiaEntityResolver entityResolver = new MorphiaEntityResolver();
    private EntityPropertiesResolver entityPropertiesResolver;
    private static final String PROJECTION_ALL = "*";
    private static final String SELECT = "SELECT";    
    private static final String STRING_OPERAND_WITH_WILDCARD = "%%%s%%";
    
    private static final Logger logger = LoggerFactory.getLogger(PhoenixHBaseQueryTranslator.class);
    
    private static final int ORDER_EXPRESSION_SUFFIX_MAX_LENGTH = "DESC NULLS FIRST".length();

    public PhoenixHBaseQueryTranslator(Class<String> queryClass, Class<String> orderClass,
            EntityPropertiesResolver propertyResolver) {
        super(queryClass, orderClass, propertyResolver);
        this.entityPropertiesResolver = propertyResolver;
    }

    public PhoenixHBaseQueryTranslator(final EntityPropertiesResolver propertyResolver) {
        super(String.class, String.class, propertyResolver);
        this.entityPropertiesResolver = propertyResolver;
    }

    /**
     *  translates given QuerySelect object to select query string
     *  @param query QuerySelect
     *  @return String
     */
    @Override
    public <T, R> String translate(QuerySelect<T, R> query) {
        return translateSelectQuery(query);
    }

    private <T, R> String translateSelectQuery(QuerySelect<T, R> query) {
        List<String> fields = query.getReturnFields();
        Criterion rootCriterion = query.getCriteria();
        Orderings orders = query.getOrder();
        Integer maxResults = query.getMaxResults();
        Class<T> entityClass = query.getEntityClass();
        Joiner spaceJoiner = Joiner.on(" ");
        String projection = PROJECTION_ALL;
        if (CollectionUtils.isNotEmpty(fields)) {
            projection = Joiner.on(", ").join(
                    entityPropertiesResolver.resolveEntityMappingPropertyNames(fields, entityClass));
        }
        //Add query hint if available
        projection = Strings.isNullOrEmpty(query.getQueryHint()) ? projection : spaceJoiner.join(query.getQueryHint(), PROJECTION_ALL);
        String queryString = spaceJoiner.join(new String[] { SELECT, projection, PhoenixHBaseClauses.FROM.symbol(),
                entityResolver.resolve(entityClass) });

        if (rootCriterion != null) {
            queryString = spaceJoiner.join(queryString, PhoenixHBaseClauses.WHERE.symbol(), translate(rootCriterion, entityClass));
        }

        if (orders != null && CollectionUtils.isNotEmpty(orders.get())) {
            queryString = spaceJoiner.join(queryString, PhoenixHBaseClauses.ORDER_BY.symbol(), translateOrder(query));
        }
        
        if(maxResults != null && maxResults > 0) {
            queryString = spaceJoiner.join(queryString, PhoenixHBaseClauses.LIMIT.symbol(), maxResults);
        }
        return queryString;
    }

    private String resolveMappingName(String fieldName) {
        return fieldName;
    }

    @Override
    public String eq(String fieldName, Object value) {

        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.EQUAL, value);
    }

    @Override
    public String ne(String fieldName, Object value) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.NOT_EQUAL, value);
    }

    @Override
    public String lt(String fieldName, Object value) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.LESS_THAN, value);
    }

    @Override
    public String lte(String fieldName, Object value) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.LESS_THAN_OR_EQUAL, value);
    }

    @Override
    public String gt(String fieldName, Object value) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.GREATER_THAN, value);
    }

    @Override
    public String gte(String fieldName, Object value) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.GREATER_THAN_OR_EQUAL, value);
    }
    
	@Override
    public String insensitiveLike(String fieldName, Object value) {
		return join(resolveMappingName(fieldName), 
					PhoenixHBaseOperator.LIKE_CASE_INSENSITIVE, 
					String.format(STRING_OPERAND_WITH_WILDCARD, value));
	}

	@Override
    public String like(String fieldName, Object value) {
		return join(resolveMappingName(fieldName), 
					PhoenixHBaseOperator.LIKE, 
					String.format(STRING_OPERAND_WITH_WILDCARD, value));	
	}

    @Override
    public String between(String fieldName, Object from, Object to) {
        throw new UnsupportedOperationException("BETWEEN operator is not supported in phoenix hbase library...");
    }

    @Override
    public String in(String fieldName, Object[] values) {
        throw new UnsupportedOperationException("IN operator is not supported in phoenix hbase library...");
    }

    @Override
    public String notIn(String fieldName, Object[] values) {
        throw new UnsupportedOperationException("NOTIN operator is not supported in phoenix hbase library...");
    }

    @Override
    public String contains(String fieldName, Object[] values) {
        throw new UnsupportedOperationException("CONTAINS operator is not supported in phoenix hbase library...");
    }

    @Override
    public String isNull(String fieldName) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.IS_NULL);
    }

    @Override
    public String notNull(String fieldName) {
        return join(resolveMappingName(fieldName), PhoenixHBaseOperator.IS_NOT_NULL);
    }

    @Override
    public String isEmpty(String fieldName) {
        throw new UnsupportedOperationException("ISEMPTY operator is not supported in phoenix hbase library...");
    }

    @Override
    public String notEmpty(String fieldName) {
        throw new UnsupportedOperationException("NOTEMPTY operator is not supported in phoenix hbase library...");
    }

    @Override
    protected <T> String translate(NativeExpression e, Class<T> entityClass) {
        return e.getExpression().toString();
    }

    @Override
    public String order(String... orders) {
        return Joiner.on(", ").join(orders);
    }

    @Override
    public String order(String fieldName, Ordering ordering) {
        if(ordering == null || StringUtils.isBlank(ordering.getPropertyName())){
            return StringUtils.EMPTY;
        }
        StringBuilder orderExpressionBuilder = new StringBuilder(fieldName.length() + ORDER_EXPRESSION_SUFFIX_MAX_LENGTH);
        orderExpressionBuilder.append(fieldName);
        Order order = ordering.getOrder();
        if (order != null) {
            orderExpressionBuilder.append(' ');
            orderExpressionBuilder
                    .append(Order.ASCENDING.equals(order) ? "ASC"
                            : "DESC");
            NullOrdering nullOrdering = ordering.getNullOrdering();
            if (nullOrdering != null) {
                orderExpressionBuilder.append(" NULLS ");
                orderExpressionBuilder.append(nullOrdering.name());
            }
        }
        return orderExpressionBuilder.toString();
        
    }

    @Override
    public <T, R> String translateProjection(QuerySelect<T, R> query) {
        return query.getReturnFields().size() > 0 ? Joiner.on(",").join(query.getReturnFields()) : null;
    }

    protected String join(String fieldName, Object... parts) {
        return fieldName + " " + Joiner.on(" ").join(Lists.transform(Arrays.asList(parts), toString));
    }

    private final Function<Object, String> toString = new Function<Object, String>(){
        @Override
        public String apply(Object o) {
            return string(o);
        }
    };

    protected String string(Object o) {
        if (o instanceof Object[]) {
            return "[" + Joiner.on(",").join(Lists.transform(Arrays.asList((Object[]) o), toString)) + "]";
        } else if (o instanceof String) {
            return "'" + sanitizeString((String) o) + "'";
        } else if (o instanceof Character) {
            logger.debug("Converting char type, value {}", o);
            return "'" + sanitizeString(o.toString()) + "'";
        } else if (o instanceof Date) {
            return PhoenixDateFormatUtil.formatDate((Date) o);
        } else if (o != null) {
            return o.toString();
        }
        logger.warn("Can't convert null object to String");
        return null;

    }

    private String sanitizeString(String stringValue) {
        if (stringValue.contains("'")) {
            stringValue = stringValue.replace("'", "''");
        }
        if (stringValue.contains("\\")) {
            stringValue = stringValue.replace("\\", "\\\\");
        }
        return stringValue;
    }

    @Override
    public <T> String translate(QueryUpdate<T> updateQuery) {
        T entity = updateQuery.getEntity();
        List<String> selectedFields = updateQuery.getSelectedFields();
        try {
            List<EntityPropertyValueBinding> entityPropertyValueBindings = buildParameterBindings(entity,
                    selectedFields);

            if (CollectionUtils.isEmpty(entityPropertyValueBindings)) {
                throw new DataStoreException("Invalid Entity to save :"
                        + (updateQuery.getEntity() != null ? updateQuery.getEntity().getClass() : ""));
            }
            String columnsList = "";
            String valuesList = "";
            Joiner joiner = Joiner.on(", ");
            for (EntityPropertyValueBinding propertyValueBinding : entityPropertyValueBindings) {
                if (propertyValueBinding.getValue() != null) {
                    try {
                        if (StringUtils.isEmpty(columnsList)) {
                            columnsList = propertyValueBinding.getEntityPropertyBinding().getStoreFieldName();
                            valuesList = string(propertyValueBinding.getValue());
                        } else {
                            columnsList = joiner.join(columnsList, propertyValueBinding.getEntityPropertyBinding()
                                    .getStoreFieldName());
                            valuesList = joiner.join(valuesList, string(propertyValueBinding.getValue()));
                        }
                    } catch (Exception ex) {
                        logger.warn("Exception while translating the update query for property {} and value {}",
                                propertyValueBinding.getEntityPropertyBinding().getName(),
                                propertyValueBinding.getValue(), ex);
                        throw new RuntimeException(ex);
                    }

                } else {
                    logger.info("There is no binding value for property {} and skipping the translation.",
                            propertyValueBinding.getEntityPropertyBinding().getNameFullPath());
                    continue;
                }
            }
            String tableName = entityResolver.resolve(entity.getClass());
            StringBuilder finalQuery = new StringBuilder("UPSERT INTO ");
            finalQuery.append(tableName).append("(").append(columnsList).append(")").append(" values ").append("(")
                    .append(valuesList).append(")");

            return finalQuery.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    private <T> List<EntityPropertyValueBinding> buildParameterBindings(T entity, List<String> selectedFields)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        try {

            Map<String, EntityPropertyBinding> entityPropertyBindings = entityPropertiesResolver
                    .getEntityPropertyNamePropertyBindingMap(entity.getClass());
            if (MapUtils.isEmpty(entityPropertyBindings)) {
                throw new DataStoreException("Invalid Entity class " + entity.getClass().getSimpleName());
            }
            List<EntityPropertyValueBinding> entityPropertyValueBindings = new LinkedList<EntityPropertyValueBinding>();
            int position = 1;
            Set<String> entityFields = entityPropertyBindings.keySet();
            if (CollectionUtils.isEmpty(selectedFields)) {
                selectedFields = new ArrayList<String>(entityFields);
            }
            for (String beanPropertyName : selectedFields) {
                EntityPropertyBinding entityPropertyBinding = entityPropertyBindings.get(beanPropertyName);
                if (entityPropertyBinding == null) {
                    String errorMessage = "Invalid bean property " + beanPropertyName + " for bean "
                            + entity.getClass().getSimpleName();
                    throw new DataStoreException(errorMessage);
                }
                Object value = PropertyUtils.getProperty(entity, entityPropertyBinding.getNameFullPath());
                EntityPropertyValueBinding entityPropertyValueBinding = new EntityPropertyValueBinding(
                        entityPropertyBinding);
                entityPropertyValueBinding.setValue(value);
                entityPropertyValueBinding.setPosition(position++);
                entityPropertyValueBindings.add(entityPropertyValueBinding);
            }
            return entityPropertyValueBindings;
        } catch (Exception ex) {
            logger.warn("Exceotion while building parameter bindings for entity {}", entity.getClass(), ex);
            throw new DataStoreException(ex);
        }

    }

    @Override
    public String and(String... subqueries) {
        return junction(PhoenixHBaseOperator.AND, subqueries);
    }

    @Override
    public String or(String... subqueries) {
        return junction(PhoenixHBaseOperator.OR, subqueries);
    }

    protected String junction(PhoenixHBaseOperator operator, String... subqueries) {
        if (subqueries.length < 1) {
            return "";
        } else if (subqueries.length == 1) {
            return subqueries[0];
        } else {
            return "(" + Joiner.on(") " + operator.symbol() + " (").join(subqueries) + ")";
        }
    }

    @Override
    public String limit(Integer value) {
       
        if(value != null && value > 0) {
            return "LIMIT " + value;
        }
        return "";
        
    }

}