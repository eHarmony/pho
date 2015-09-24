package com.eharmony.services.mymatchesservice.configuration;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.RequestContextFilter;

import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import com.eharmony.services.mymatchesservice.monitoring.GraphiteReportingConfiguration;

public class JerseyApp extends ResourceConfig {
    Logger log = LoggerFactory.getLogger(getClass());

    public JerseyApp() {
        log.info("Initializing App");
        register(RequestContextFilter.class);
        register(JacksonFeature.class);
        register(new InstrumentedResourceMethodApplicationListener(
                GraphiteReportingConfiguration.getRegistry()));
    }

}
