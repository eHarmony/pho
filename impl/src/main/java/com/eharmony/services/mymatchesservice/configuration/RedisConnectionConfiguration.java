package com.eharmony.services.mymatchesservice.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import  com.google.common.base.Preconditions;
/**
 * Jedis connection factory configuration.
 * This is taken from auth-server project.
 * 
 * @author gwang
 *
 */
@Configuration
public class RedisConnectionConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionConfiguration.class);

    @Bean(name = "matchDataJedisConnectionFactory")
    public RedisConnectionFactory matchDataJedisConnectionFactory(
            @Value("${redis.matchdata.master.name}") final String redisMasterName,
            @Value("${matchdata.store.connection.pairs}") final String storeHostPortPairs,
            @Value("${matchdata.store.connection.timeout:2000}") final int timeout,
            @Value("${matchdata.store.use.sentinel:false}") final boolean matchStoreUseSentinel) {
        Preconditions.checkArgument(StringUtils.isNotBlank(redisMasterName), "matchdata master must not be empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(storeHostPortPairs),
                "storeHostPortPairs must not be empty");
        Preconditions.checkArgument(timeout > 0, "timeout must be positive.");

        logger.info("initializing matchDataJedisConnectionFactory with master {}", redisMasterName);
        JedisConnectionFactory connectionFactory = null;
        if (matchStoreUseSentinel) {
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration().master(redisMasterName);
            sentinelConfig.setSentinels(buildSentinels(storeHostPortPairs));
            connectionFactory = new JedisConnectionFactory(sentinelConfig);
        } else {
            connectionFactory = new JedisConnectionFactory();
            logger.info("initializing matchDataJedisConnectionFactory with host and port {}",
                    storeHostPortPairs);
            String[] hostPortParts = splitHostPortPairs(storeHostPortPairs);
            connectionFactory.setHostName(hostPortParts[0]);
            connectionFactory.setPort(Integer.valueOf(hostPortParts[1]));
        }
        connectionFactory.setUsePool(true);
        connectionFactory.setTimeout(timeout);
        return connectionFactory;
    }

    private String[] splitHostPortPairs(final String hostPortPairString) {
        return hostPortPairString.split(":");
    }

    private List<RedisNode> buildSentinels(final String redisSentinelPairs) {
        
        String[] sentinelPairs = redisSentinelPairs.split(",");
        
        Preconditions.checkArgument(!ArrayUtils.isEmpty(sentinelPairs), "sentinelPairs must not be empty");
        List<RedisNode> sentinels = new ArrayList<RedisNode>(sentinelPairs.length);
        for (String hostPortPairString : sentinelPairs) {
            String[] sentinelParts = splitHostPortPairs(hostPortPairString);
            Preconditions.checkArgument(!ArrayUtils.isEmpty(sentinelParts),
                    "sentinelPair is empty, may be it is not in desired format, it must be host:port");
            Preconditions.checkArgument(sentinelParts.length == 2,
                    "sentinelPair is not well formated, it must be host:port");
            String host = sentinelParts[0];
            int port = Integer.valueOf(sentinelParts[1]);
            logger.info("Configuring the sentinel with host {} : port {}", host, port);
            RedisNode redisNode = new RedisNode(host, port);
            sentinels.add(redisNode);
        }
        return sentinels;
    }
}