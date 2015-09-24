package com.eharmony.services.datastore.cassandra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
//@PropertySource(value = { "classpath:cassandra.properties" })
@EnableCassandraRepositories(basePackageClasses = {
		MatchFeedCassandraRepository.class
})
public class CassandraStoreConfiguration extends AbstractCassandraConfiguration {

    /*@Autowired
    private Environment environment;*/

	@Value("${cassandra.contactpoints}")
	private String contactPoint;
	
	@Value("${cassandra.port}")
	private int cassandraPort;
	
	@Value("${cassandra.keyspace}")
	private String cassandraKeyspace;
	
    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(contactPoint);
        cluster.setPort(cassandraPort);
        return cluster;
    }

    @Override
    protected String getKeyspaceName() {
        return cassandraKeyspace;
    }

    @Bean
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }
}
