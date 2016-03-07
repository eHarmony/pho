/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2009 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.store.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eharmony.services.mymatchesservice.store.data.MatchDo;


/**
 * Dao implementation of retrieving MatchDo instances
 */
@Component(value="matchDao")
public class MatchDaoImpl
        extends AbstractDao<Long, MatchDo>
        implements MatchDao {

    private final Logger log = LoggerFactory.getLogger(MatchDaoImpl.class);

    public MatchDaoImpl() {
        super(MatchDo.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional("txManager")
    public MatchDo findMatchById(Long matchId) {

        log.debug("getting match {}", matchId);
        return findByPrimaryKey(matchId);
    }   
}
