package com.eharmony.services.mymatchesservice.store.dao;

import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;
import com.eharmony.singles.common.data.dao.HibernateDao;


public interface MatchSummaryDao extends HibernateDao<Long, MatchSummaryDo> {

    
	public MatchSummaryDo findMatchSummaryByUserAndMatch(Long userId, Long matchId);
}
