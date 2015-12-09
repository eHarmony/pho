package com.eharmony.services.mymatchesservice.service;

import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.eharmony.services.mymatchesservice.util.MatchStatusGroupEnum;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MatchStatusGroupResolverTest {
    private void validateMatchStatusGroupsFullSet(long userId,
        Set<String> statuses) {
        MatchStatusGroupResolver resolver = new MatchStatusGroupResolver();

        Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> groups = resolver.buildMatchesStatusGroups(userId,
                statuses);

        assertEquals(3, groups.size());

        Set<MatchStatusEnum> oneGroup = groups.get(MatchStatusGroupEnum.NEW);
        assertNotNull(oneGroup);
        assertEquals(1, oneGroup.size());
        assertTrue(oneGroup.contains(MatchStatusEnum.NEW));
        assertTrue(!oneGroup.contains(MatchStatusEnum.CLOSED));

        oneGroup = groups.get(MatchStatusGroupEnum.ARCHIVE);
        assertNotNull(oneGroup);
        assertEquals(1, oneGroup.size());
        assertTrue(oneGroup.contains(MatchStatusEnum.ARCHIVED));
        assertTrue(!oneGroup.contains(MatchStatusEnum.CLOSED));

        oneGroup = groups.get(MatchStatusGroupEnum.COMMUNICATION);
        assertNotNull(oneGroup);
        assertEquals(3, oneGroup.size());
        assertTrue(oneGroup.contains(MatchStatusEnum.MYTURN));
        assertTrue(oneGroup.contains(MatchStatusEnum.THEIRTURN));
        assertTrue(oneGroup.contains(MatchStatusEnum.OPENCOMM));
        assertTrue(!oneGroup.contains(MatchStatusEnum.CLOSED));
    }

    @Test
    public void testGroupResolverSortsIntoBuckets() {
        long userId = 62837364L;
        Set<String> statuses = new HashSet<String>();
        statuses.add("NEW");
        statuses.add("ARCHIVED");
        statuses.add("OPENCOMM");
        statuses.add("MYTURN");
        statuses.add("THEIRTURN");
        statuses.add("CLOSED");

        validateMatchStatusGroupsFullSet(userId, statuses);
    }

    @Test
    public void testGroupResolverNoStatusSpecified() {
        long userId = 62837364L;
        Set<String> statuses = new HashSet<String>();

        validateMatchStatusGroupsFullSet(userId, statuses);
    }
    
    @Test
    public void testGroupResolverAll() {
        long userId = 62837364L;
        Set<String> statuses = new HashSet<String>();
        statuses.add("ALL");

        validateMatchStatusGroupsFullSet(userId, statuses);
    }
    
    @Test
    public void testGroupResolverNewOnly() {
        long userId = 62837364L;
        Set<String> statuses = new HashSet<String>();
        statuses.add("NEW");

        MatchStatusGroupResolver resolver = new MatchStatusGroupResolver();

        Map<MatchStatusGroupEnum, Set<MatchStatusEnum>> groups = resolver.buildMatchesStatusGroups(userId,
                statuses);
        
        assertEquals(1, groups.size());
        
        Set<MatchStatusEnum> oneGroup = groups.get(MatchStatusGroupEnum.NEW);
        assertNotNull(oneGroup);
        assertEquals(1, oneGroup.size());
        assertTrue(oneGroup.contains(MatchStatusEnum.NEW));
        assertTrue(!oneGroup.contains(MatchStatusEnum.CLOSED));
        assertTrue(!oneGroup.contains(MatchStatusEnum.ARCHIVED));
        assertTrue(!oneGroup.contains(MatchStatusEnum.MYTURN));
        assertTrue(!oneGroup.contains(MatchStatusEnum.THEIRTURN));
        assertTrue(!oneGroup.contains(MatchStatusEnum.OPENCOMM));
    }
}
