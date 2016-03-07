package com.eharmony.services.mymatchesservice.store.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.annotation.Timed;
import com.eharmony.services.mymatchesservice.store.data.MatchSummaryDo;
import com.eharmony.singles.common.data.dao.AbstractDao;


@Repository
public class MatchSummaryDaoImpl extends AbstractDao<Long, MatchSummaryDo>
        implements MatchSummaryDao {

    private final Logger log = LoggerFactory.getLogger(MatchSummaryDaoImpl.class);

    protected MatchSummaryDaoImpl() {
        super(MatchSummaryDo.class);
    }

    @Override
    @Transactional
    @Timed(name="MDS.findMatchSummaryByUserAndMatch")
    public MatchSummaryDo findMatchSummaryByUserAndMatch(Long userId, Long matchId) {

        log.info("getting match summeries for match {} and user {}", matchId, userId);

        boolean useCache = false;
        Criteria criteria = createCriteria(useCache)
                .add(Expression.eq("matchId",matchId ))
                .add(Expression.eq("userId",userId));

        return (MatchSummaryDo)criteria.uniqueResult();

    }
}
