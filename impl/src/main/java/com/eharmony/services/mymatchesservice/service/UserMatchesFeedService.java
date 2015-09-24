package com.eharmony.services.mymatchesservice.service;

import java.util.List;

import com.eharmony.services.datastore.cassandra.model.CassandraMatchFeedItem;
import com.eharmony.services.datastore.mongodb.model.MatchFeedItem;

public interface UserMatchesFeedService {

	public List<MatchFeedItem> getUserMatches(Integer userId, Integer distance);
	public MatchFeedItem getUserMatch(Integer userId, Long matchId);
	public List<CassandraMatchFeedItem> getUserMatchesFromCassandra(Integer userId);
	
}
