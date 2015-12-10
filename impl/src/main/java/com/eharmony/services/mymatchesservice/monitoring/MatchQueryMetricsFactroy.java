package com.eharmony.services.mymatchesservice.monitoring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer.Context;
import com.google.common.base.Joiner;
/**
 * Factory to get the metrics.
 * @author gwang
 *
 */
@Component
public class MatchQueryMetricsFactroy {
    private static final Joiner hierarchyJoiner = Joiner.on(".").skipNulls();
    private static final String HISTOGRAM_SUFFIX = "number";

    /**
     * get the timer context type of metrics.
     * @param metricsHierarchyParts metrics hierarchy parts
     * @return a timer context
     */
    public Context getTimerContext(Object... metricsHierarchyParts) {
        return GraphiteReportingConfiguration.getRegistry()
                .timer(hierarchyJoiner.join(metricsHierarchyParts)).time();
    }
    /**
     * get the histogram type of metrics.
     * @param metricsHierarchyParts metrics hierarchy parts
     * @return a histogram
     */
    public Histogram getHistogram(Object... metricsHierarchyParts) {
        List<Object> historgramHierarchyParts = new LinkedList<Object> (Arrays.asList(metricsHierarchyParts));
        historgramHierarchyParts.add(HISTOGRAM_SUFFIX);
        return GraphiteReportingConfiguration.getRegistry()
                .histogram(hierarchyJoiner.join(historgramHierarchyParts));
    }
}
