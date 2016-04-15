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
    private static final Long TEST_MATCHID = 482581281L;
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
}
