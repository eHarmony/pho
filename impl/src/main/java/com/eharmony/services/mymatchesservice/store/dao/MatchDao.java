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

import com.eharmony.services.mymatchesservice.store.data.MatchDo;


/**
 * Dao interface for modifying MatchDo instances
 *
 * @author  kmunroe
 */
public interface MatchDao extends HibernateDao<Long, MatchDo> {
    /**
     * Finds a specific match
     *
     * @param   matchId
     *
     * @return
     */
    public MatchDo findMatchById(Long matchId);
}
