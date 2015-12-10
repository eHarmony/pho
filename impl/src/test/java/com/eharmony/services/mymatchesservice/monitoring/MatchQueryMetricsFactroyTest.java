package com.eharmony.services.mymatchesservice.monitoring;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class MatchQueryMetricsFactroyTest {
	MatchQueryMetricsFactroy metricsFactory = new MatchQueryMetricsFactroy();
	private MetricRegistry registry;

	@Test
	public void testGetTimerContext() {
		registry.removeMatching(MetricFilter.ALL);
		assertFalse(registry.getNames().contains("a.b"));
		Timer.Context t = metricsFactory.getTimerContext("a","b");
		assertNotNull(t);
		assertTrue(registry.getNames().contains("a.b"));
	}

	@Test
	public void testGetHistogram() {
		registry.removeMatching(MetricFilter.ALL);
		assertFalse(registry.getNames().contains("a.b.number"));
		Histogram h = metricsFactory.getHistogram("a","b");
		
		assertNotNull(h);
		assertTrue(registry.getNames().contains("a.b.number"));
	}
	
	@Test
	public void testGetHistogramWithNull() {
		registry.removeMatching(MetricFilter.ALL);
		assertFalse(registry.getNames().contains("a.b.d.number"));
		Histogram h = metricsFactory.getHistogram("a", "b", null, "d");
		
		assertNotNull(h);
		assertTrue(registry.getNames().contains("a.b.d.number"));
	}
	
	@Before
	public void setup() {
		registry = GraphiteReportingConfiguration.getRegistry();
	}
	
	@After
	public void after() {
		registry.removeMatching(MetricFilter.ALL);
	}

}
