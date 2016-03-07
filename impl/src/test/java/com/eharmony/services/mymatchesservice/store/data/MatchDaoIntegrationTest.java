/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2011 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.store.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.eharmony.services.mymatchesservice.store.dao.MatchDao;


@ContextConfiguration(locations =  {
    "/mymatchesservice/application-context.xml"}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class MatchDaoIntegrationTest {
    private static final Long TEST_MALE_USER_ID = 1L;
    private static final Long TEST_FEMALE_USER_ID = 4L;
    private static final Long TEST_MATCHID = 482581281L;
    private static final Long TEST_EDITABLE_MATCHID = 17893452L;
    @Resource
    private MatchDao dao;
    private Logger log = LoggerFactory.getLogger(MatchDaoIntegrationTest.class);

    @Test
    @Transactional
    public void testFindByMatchId() {
        log.debug("START: testFindByMatchId");

        try {
            MatchDo match = dao.findMatchById(TEST_MATCHID);

            log.debug("found match {" + match + "}");

            assertNotNull(match);
            assertEquals(TEST_MATCHID, match.getId());
        } catch (Exception e) {
            log.error("exception testing find match by Id", e);
            fail(e.getMessage());
        }
    }

    @Test
    @Transactional
    public void testFindMatchesForFemale() {
        log.debug("START: testFindMatchesForFemale");

        try {
            MatchDo match = dao.findMatchById(TEST_FEMALE_USER_ID);

            log.debug("found match[ {}] for user", match);

            assertNotNull(match);
            assertEquals(TEST_FEMALE_USER_ID, match.getUserId());
        } catch (Exception e) {
            log.error("exception testing find matches for female", e);
            fail(e.getMessage());
        }
    }

    @Test
    @Transactional
    public void testFindMatchesForMale() {
        log.debug("START: testFindMatchesForMale");

        try {
            MatchDo match = dao.findMatchById(TEST_MALE_USER_ID);

            log.debug("found matches [{}]  for user", match);

            assertNotNull(match);
            assertEquals(TEST_MALE_USER_ID, match.getCandidateUserId());
        } catch (Exception e) {
            log.error("exception testing find matches for male", e);
            fail(e.getMessage());
        }
    }

    @Test
    @Transactional
    public void testImmutability() {
        log.debug("START: testImmutability");

        try {
            MatchDo match = dao.findMatchById(TEST_EDITABLE_MATCHID);

            log.debug("found match [{}]", match);

            assertNotNull(match);
            assertEquals(TEST_EDITABLE_MATCHID, match.getId());

            try {
                dao.delete(match);
                fail("expected exception deleting match");
            } catch (Exception e) {
                assertTrue(e instanceof UnsupportedOperationException);
            }

            try {
                match.setCandidateFirstName("changed");
                dao.save(match);
                fail("expected exception saving match");
            } catch (Exception e) {
                assertTrue(e instanceof UnsupportedOperationException);
            }
        } catch (Exception e) {
            log.error("exception testing match immutability", e);
            fail(e.getMessage());
        }
    }
}
