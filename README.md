<p align="center">
  <img src="pho.png" width="100" height="100"/>
</p>
PHO (Phoenix-HBase ORM)
=======================

[![Build Status](https://travis-ci.org/eHarmony/pho.svg?branch=master)](https://travis-ci.org/eHarmony/pho)

PHO is a library for building and executing queries on HBase using Apache Phoenix.
It provides ORM-like mappings and DSL-style query building.

Its Interfaces and generic annotations allows the ability to switch the data store api in the future without changing the queries.
Currently, it only supports Hbase integration using Apache Phoenix. However, it's very easy to plugin other implementations if need be.

# Entity Class
Suppose we have the following TestClass we want to query against in our data store:

```java
  //class must be annotated with Entity
  import com.google.code.morphia.annotations.Embedded;
  import com.google.code.morphia.annotations.Entity;
  @Entity(value="user_matches")
  public class MatchDataFeedItemDto {
	  @Embedded
	  private MatchCommunicationElement communication;
	  @Embedded
	  private MatchElement match;
	  @Embedded
	  private MatchProfileElement matchedUser;
  }

  public class MatchElement {
      // row key
      @Property(value = "UID")
      private long userId;
      @Property(value = "MID")
      private long matchId;
      @Property(value = "DLVRYDT")
      private Date deliveredDate;
      @Property(value = "STATUS")
      private int status;
  }
```

# Query Building

Query building can be done in DSL style. More advanced query building is under development but, for now, we will use a combination of the QueryBuilder and the static, Hibernate-style Restrictions methods to construct our queries.

### Simple Queries

Construct a query to find all user matches which are delivered in past 2 days and not in closed state

```java
    import com.eharmony.datastore.api.DataStoreApi;
	import com.eharmony.datastore.model.MatchDataFeedItemDto;
	import com.eharmony.datastore.query.QuerySelect;
	import com.eharmony.datastore.query.builder.QueryBuilder;
	import com.eharmony.datastore.query.criterion.Restrictions;
	@Repository
	public class MatchStoreQueryRepositoryImpl implements MatchStoreQueryRepository {
		final QuerySelect<MatchDataFeedItemDto, MatchDataFeedItemDto> query = QueryBuilder
                .builderFor(MatchDataFeedItemDto.class)
                .select()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.eq("status", 2))
                .add(Restrictions.gt("deliveredDate", timeThreshold.getTime())).build();
        Iterable<MatchDataFeedItemDto> feedItems = dataStoreApi.findAll(query);
```

### Compound Queries

Construct a more complex query where not only do we want to find items with a date older than a day ago, but also find the matches in different status and order the results by deliveryDate and limit the results size to 10:

```java
    //provided
    List<Integer> statusFilters = request.getMatchStatusFilters();
    String sortBy = request.getSortBy()
    Disjunction disjunction = new Disjunction();
    for (Integer statusFilter : statusFilters) {
    	disjunction.add(Restrictions.eq("status", statusFilter));
    }
    final QuerySelect<MatchDataFeedItemDto, MatchDataFeedItemDto> query = QueryBuilder
                .builderFor(MatchDataFeedItemDto.class)
                .select()
                .add(Restrictions.eq("userId", userId))
                .add(Restrictions.gt("deliveredDate", timeThreshold.getTime()));
    			.add(disjunction);
    			.addOrder(new Ordering(sortBy, Order.DESCENDING)).build();
    Iterable<MatchDataFeedItemDto> feedItems = dataStoreApi.findAll(query);
```

*Note:* by default, expressions will be ANDed together when added separately.

### Query Interface
The following query components are supported:

```java
  // equals
  EqualityExpression eq(String propertyName, Object value);

  // does not equal (not equals);
  EqualityExpression ne(String propertyName, Object value);

  // less than
  EqualityExpression lt(String propertyName, Object value);

  // less than or equal
  EqualityExpression lte(String propertyName, Object value);

  // greater than
  EqualityExpression gt(String propertyName, Object value);

  // greater than or equal
  EqualityExpression gte(String propertyName, Object value);

  // between from and to (inclusive)
  RangeExpression between(String propertyName, Object from, Object to);

  // and - takes a variable list of expressions as arguments
  Conjunction and(Criterion... criteria);

  // or - takes a variable list of expressions as arguments
  Disjunction or(Criterion... criteria);

```

### Resolving Entity and Property Names

Always use the property names of your Java objects in your queries.
If these names differ from those used in your datastore you will use annotations to provide the mappings.
Entity Resolvers are configured to map the entity classes to table/collection names.
Property Resolvers are configured to map the names of your object variables to column/field names.

The following annotations are currently supported for the indicated data store type.
Custom EntityResolvers and PropertyResolvers are easy to configure and create.

see [Morphia Annotations](https://code.google.com/p/morphia/wiki/AllAnnotations) for entity class annotation mappings


## Query Execution

The QueryExecutor interface supports the following operations:

```java
  // return an iterable of type R from the query against type T (R and T will often be the same type)
  <T, R> Iterable<R> findAll(QuerySelect<T, R> query);
  
  // return aone R from the query against type T
  <T, R> R findOne(QuerySelect<T, R> query)
  
  // save the entity of type T to the data store
  <T> T save(T entity);
  
  // save all of the entities in the provided iterable to data store
  <T> Iterable<T> save(Iterable<T> entities);
  
  // saves all the entities in batches with configured batch size
  <T> int[] saveBatch(Iterable<T> entities);
```

## Configuration

Here are some example Spring configuration files for Hbase using apache phoenix.

### HBase
configuration proeprties
hbase.connection.url=jdbc:phoenix:zkhost:2181

```xml
    <!-- Register your entity bean here -->
    <util:list id="entityPropertiesMappings">
	    <value>com.eharmony.datastore.model.MatchDataFeedItemDto</value>
	</util:list>
	<bean id="entityPropertiesMappingContext" class="com.eharmony.pho.mapper.EntityPropertiesMappingContext">
	    <constructor-arg ref="entityPropertiesMappings"/>
	</bean>
	
	<bean id="entityPropertiesResolver" class="com.eharmony.pho.mapper.EntityPropertiesResolver">
	    <constructor-arg ref="entityPropertiesMappingContext"/>
	</bean>
	
	<bean id="phoenixHBaseQueryTranslator" class="com.eharmony.pho.hbase.translator.PhoenixHBaseQueryTranslator">
	    <constructor-arg name="propertyResolver" ref="entityPropertiesResolver" />
	</bean>
	
	<bean id="phoenixProjectedResultMapper" class="com.eharmony.pho.hbase.mapper.PhoenixProjectedResultMapper">
	    <constructor-arg name="entityPropertiesResolver" ref="entityPropertiesResolver" />
	</bean>
	
	<bean id="phoenixHBaseQueryExecutor" class="com.eharmony.pho.hbase.query.PhoenixHBaseQueryExecutor"> 
	    <constructor-arg name="queryTranslator" ref="phoenixHBaseQueryTranslator"/>
	    <constructor-arg name="resultMapper" ref="phoenixProjectedResultMapper" />
	 </bean>
	 
	<bean id="dataStoreApi" class="com.eharmony.pho.hbase.PhoenixHBaseDataStoreApiImpl">
	    <constructor-arg name="connectionUrl" value="${hbase.connection.url}"/>
	    <constructor-arg name="queryExecutor" ref="phoenixHBaseQueryExecutor"/>
	</bean>

```

