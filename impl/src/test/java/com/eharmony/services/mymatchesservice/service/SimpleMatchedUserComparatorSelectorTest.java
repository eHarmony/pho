package com.eharmony.services.mymatchesservice.service;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.Assert.*;
public class SimpleMatchedUserComparatorSelectorTest {
	SimpleMatchedUserComparatorSelector compSelector = new SimpleMatchedUserComparatorSelector();
	@Test
	public void testSelectComparator() {
		Comparator<SimpleMatchedUserDto> c = compSelector.selectComparator("name");
		assertNotNull(c);
	}
	@Test
	public void testSelectComparatorNegtive() {
		Comparator<SimpleMatchedUserDto> c = compSelector.selectComparator("nosuchfield");
		// default comparator is applied.
		assertNotNull(c);
	}
	@Before
	public void setup() {
		ReflectionTestUtils.invokeMethod(compSelector, "initialzeMap", (Object[]) null);
	}

}
