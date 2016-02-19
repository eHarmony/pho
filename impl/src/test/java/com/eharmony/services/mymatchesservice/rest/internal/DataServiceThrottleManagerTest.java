package com.eharmony.services.mymatchesservice.rest.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DataServiceThrottleManagerTest {

	
	@Test
	public void testRedisSamplingEnabled(){
		
		DataServiceThrottleManager throttle1 = new DataServiceThrottleManager(false, 1, "101");
		DataServiceThrottleManager throttle2 = new DataServiceThrottleManager(true, 10, "101");
		DataServiceThrottleManager throttle3 = new DataServiceThrottleManager(true, 100, "101");
		DataServiceThrottleManager throttle4 = new DataServiceThrottleManager(true, 0, "101");
				
		assertFalse(throttle1.isRedisSamplingEnabled(12345L)); // flag is false
		
		assertTrue(throttle2.isRedisSamplingEnabled(10000L));   // flag is true, in range
		assertTrue(throttle2.isRedisSamplingEnabled(10002L));   // flag is true, in range
		assertTrue(throttle2.isRedisSamplingEnabled(10004L));   // flag is true, in range
		assertTrue(throttle2.isRedisSamplingEnabled(10006L));   // flag is true, in range
		assertTrue(throttle2.isRedisSamplingEnabled(10008L));   // flag is true, in range
		assertTrue(throttle2.isRedisSamplingEnabled(10009L));   // flag is true, border of range
		assertFalse(throttle2.isRedisSamplingEnabled(10010L));  // flag is true, one step over
		
		// all accepted
		assertTrue(throttle3.isRedisSamplingEnabled(100L));
		assertTrue(throttle3.isRedisSamplingEnabled(0L));
		assertTrue(throttle3.isRedisSamplingEnabled(50L));
		
		// none accepted
		assertFalse(throttle4.isRedisSamplingEnabled(100L));
		assertFalse(throttle4.isRedisSamplingEnabled(0L));
		assertFalse(throttle4.isRedisSamplingEnabled(50L));

	}
	
	@Test
	public void testWhitelist(){
		
		DataServiceThrottleManager throttle1 = new DataServiceThrottleManager(true, 10, "1001,1002,1060");
				
		assertTrue(throttle1.isRedisSamplingEnabled(1001L));  // In whitelist
		assertTrue(throttle1.isRedisSamplingEnabled(1003L)); // not in whitelist, but mod sampled
		assertTrue(throttle1.isRedisSamplingEnabled(1060L));  // not mod sampled but in whitelist
		assertFalse(throttle1.isRedisSamplingEnabled(1061L));  // not mod sampled nor in whitelist
	}
	
	@Test
	public void testEmptyWhitelist(){
		
		DataServiceThrottleManager throttle1 = new DataServiceThrottleManager(true, 10, "");
				
		assertTrue(throttle1.isRedisSamplingEnabled(1001L));  
		assertFalse(throttle1.isRedisSamplingEnabled(1060L));  
	}
}
