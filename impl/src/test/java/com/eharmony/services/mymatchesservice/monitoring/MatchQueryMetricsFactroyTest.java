package com.eharmony.services.mymatchesservice.monitoring;

import static org.junit.Assert.*;

import org.junit.Test;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Timer;

public class MatchQueryMetricsFactroyTest {
	MatchQueryMetricsFactroy metricsFactory = new MatchQueryMetricsFactroy();

	@Test
	public void testGetTimerContext() {
		Timer.Context t = metricsFactory.getTimerContext("a","b");
		assertNotNull(t);
	}

	@Test
	public void testGetHistogram() {
		Histogram h = metricsFactory.getHistogram("a","b");
		assertNotNull(h);
	}

}
