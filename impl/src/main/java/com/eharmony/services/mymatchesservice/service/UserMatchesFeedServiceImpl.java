package com.eharmony.services.mymatchesservice.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.eharmony.services.datastore.cassandra.dao.MatchFeedCassandraDao;
import com.eharmony.services.datastore.cassandra.model.CassandraMatchFeedItem;
import com.eharmony.services.datastore.mongodb.dao.MatchFeedDao;
import com.eharmony.services.datastore.mongodb.model.MatchFeedItem;

@Service
public class UserMatchesFeedServiceImpl implements UserMatchesFeedService {

	@Resource private MatchFeedDao matchFeedDao;
	@Resource private MatchFeedCassandraDao matchFeedCassandraDao;
	
	@Override
	public List<MatchFeedItem> getUserMatches(Integer userId, Integer distance) {

		if(distance != null && distance > 0) {
			return matchFeedDao.getUserMatchesFilteredByDistance(userId, distance);
		}
		return matchFeedDao.getUserMatches(userId);
	}
	
	@Override
	public List<CassandraMatchFeedItem> getUserMatchesFromCassandra(Integer userId) {

		return matchFeedCassandraDao.getUserMatches(userId);
	}

	@Override
	public MatchFeedItem getUserMatch(Integer userId, Long matchId) {
		return matchFeedDao.getUserMatch(userId, matchId);
	}

}
