package com.eharmony.services.mymatchesservice.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
/**
 * Unit test cases for retrieving the feed limit config value for a match group status.
 * 
 * @author esrinivasan
 */
public class MatchFeedLimitsByStatusConfigurationTest {

	MatchFeedLimitsByStatusConfiguration matchFeedLimitConfiguration;

	@Before
	public void setup() {
		matchFeedLimitConfiguration = new MatchFeedLimitsByStatusConfiguration(1000, 2000, 3000, 4000);
	}

	@Test
	public void getDefaultFeedLimitForGroupTest_New() {
		Integer feedLimit = matchFeedLimitConfiguration.getDefaultFeedLimitForGroup(MatchStatusGroupEnum.NEW);
		Assert.assertNotNull(feedLimit);
		Assert.assertEquals(1000, feedLimit.intValue());
	}

	@Test
	public void getDefaultFeedLimitForGroupTest_Archive() {
		Integer feedLimit = matchFeedLimitConfiguration.getDefaultFeedLimitForGroup(MatchStatusGroupEnum.ARCHIVE);
		Assert.assertNotNull(feedLimit);
		Assert.assertEquals(2000, feedLimit.intValue());
	}

	@Test
	public void getDefaultFeedLimitForGroupTest_Communication() {
		Integer feedLimit = matchFeedLimitConfiguration.getDefaultFeedLimitForGroup(MatchStatusGroupEnum.COMMUNICATION);
		Assert.assertNull(feedLimit);
	}

	@Test
	public void getFallbackFeedLimitForGroupTest_New() {
		Integer feedLimit = matchFeedLimitConfiguration.getFallbackFeedLimitForGroup(MatchStatusGroupEnum.NEW);
		Assert.assertNotNull(feedLimit);
		Assert.assertEquals(3000, feedLimit.intValue());
	}

	@Test
	public void getFallbackFeedLimitForGroupTest_Archive() {
		Integer feedLimit = matchFeedLimitConfiguration.getFallbackFeedLimitForGroup(MatchStatusGroupEnum.ARCHIVE);
		Assert.assertNotNull(feedLimit);
		Assert.assertEquals(4000, feedLimit.intValue());
	}

	@Test
	public void getFallbackFeedLimitForGroupTest_Communication() {
		Integer feedLimit = matchFeedLimitConfiguration
		        .getFallbackFeedLimitForGroup(MatchStatusGroupEnum.COMMUNICATION);
		Assert.assertNull(feedLimit);
	}

}
