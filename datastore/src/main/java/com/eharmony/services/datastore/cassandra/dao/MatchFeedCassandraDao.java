package com.eharmony.services.datastore.cassandra.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eharmony.services.datastore.cassandra.MatchFeedCassandraRepository;
import com.eharmony.services.datastore.cassandra.model.CassandraMatchFeedItem;
import com.google.common.collect.Lists;

@Component("matchFeedCassandraDao")
public class MatchFeedCassandraDao {

	@Autowired
	private MatchFeedCassandraRepository repository;

	public List<CassandraMatchFeedItem> getUserMatches(Integer userId) {
		Iterable<CassandraMatchFeedItem> feedIterable = repository.findByUserId(userId);
		List<CassandraMatchFeedItem> feedItems = Lists.newArrayList();

		if (feedIterable == null) {
			return feedItems;
		}
		feedIterable.iterator().forEachRemaining(t -> {
			feedItems.add(t);
		});
		return feedItems;
	}
	
	public Iterable<CassandraMatchFeedItem> saveFeedItems(List<CassandraMatchFeedItem> feedItems) {
		
		return repository.save(feedItems);
	   // cassandraOperations.ingest(cqlIngest, people);
	}

	/*public CassandraMatchFeedItem getUserMatch(Integer userId, Long matchId) {
		CassandraMatchFeedItem feedItem = repository.findByUserIdAndMatchId(userId, matchId);
		return feedItem;
	}*/

	/*public List<MatchFeedItem> getUserMatchesFilteredByDistance(Integer userId, Integer distance) {
		Iterable<MatchFeedItem> feedIterable = repository.findByUserIdAndDistanceLessThan(userId, distance);
		List<MatchFeedItem> feedItems = Lists.newArrayList();

		if (feedIterable == null) {
			return feedItems;
		}
		feedIterable.iterator().forEachRemaining(t -> {
			feedItems.add(t);
		});
		return feedItems;
	}*/
}
