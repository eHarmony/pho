package com.eharmony.services.mymatchesservice.monitoring;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.PickledGraphite;

/**
 * Drives the Graphite reporting instantiation.
 */
@Configuration public class GraphiteReportingConfiguration {

    private static final MetricRegistry registry = new MetricRegistry();

    /** Enables reporting. Defaults to <code>false</code>. */
    @Value("${graphite.enabled:false}")
    private Boolean enabled;

    @Value("${graphite.host}")
    private String host;
    private Logger log = LoggerFactory.getLogger(GraphiteReportingConfiguration.class);

    @Value("${graphite.periodInSeconds}")
    private Long period;

    @Value("${graphite.port}")
    private Integer port;

    @Value("${graphite.prefix}")
    private String prefix;
    
    @Value("${graphite.batch:false}")
    private boolean isBatched;
    
    @Value("${graphite.batch.size:50}")
    private Integer batchSize;

    public static MetricRegistry getRegistry() {

        return registry;

    }

    @PostConstruct public void init() {

    	log.info("Graphite host {} port {} prefix {} enabled {}", host, port, prefix, enabled);
        JmxReporter jmxReporter =
            JmxReporter.forRegistry(registry)
                       .convertDurationsTo(TimeUnit.MILLISECONDS)
                       .filter(MetricFilter.ALL)
                       .build();
        jmxReporter.start();

        if (!enabled) {

            log.info("Graphite reporting disabled, only enabling JMX");
            return;

        }

        log.info("Graphite reporting enabled to {}:{} with prefix {} sending every {} {}",
                 new Object[] { host, port, prefix, period, TimeUnit.SECONDS });

        GraphiteSender graphiteSender = null;
        if(isBatched){
        	log.info("Creating Batched Graphite sender with batch size {}", batchSize);
        	graphiteSender = new PickledGraphite(new InetSocketAddress(host, port), batchSize);
        }else{
        	log.info("Creating per-event Graphite sender");
        	graphiteSender = new Graphite(new InetSocketAddress(host, port));
        }
        
        final GraphiteReporter reporter =
            GraphiteReporter.forRegistry(registry)
                            .prefixedWith(prefix)
                            .convertRatesTo(TimeUnit.SECONDS)
                            .convertDurationsTo(TimeUnit.MILLISECONDS)
                            .filter(MetricFilter.ALL)
                            .build(graphiteSender);
        

        reporter.start(period, TimeUnit.SECONDS);

    }

}
