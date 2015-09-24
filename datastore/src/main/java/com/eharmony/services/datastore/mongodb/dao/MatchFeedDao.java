package com.eharmony.services.datastore.mongodb.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eharmony.services.datastore.mongodb.MatchFeedRepository;
import com.eharmony.services.datastore.mongodb.model.MatchFeedItem;
import com.google.common.collect.Lists;

@Component("matchFeedDao")
public class MatchFeedDao {

	// private static final Logger log = LoggerFactory.getLogger(MatchFeedDao.class);
	@Autowired
	private MatchFeedRepository repository;

	public List<MatchFeedItem> getUserMatches(Integer userId) {
		Iterable<MatchFeedItem> feedIterable = repository.findByUserId(userId);
		List<MatchFeedItem> feedItems = Lists.newArrayList();

		if (feedIterable == null) {
			return feedItems;
		}
		feedIterable.iterator().forEachRemaining(t -> {
			feedItems.add(t);
		});
		return feedItems;
	}

	public MatchFeedItem getUserMatch(Integer userId, Long matchId) {
		MatchFeedItem feedItem = repository.findByUserIdAndMatchId(userId, matchId);
		return feedItem;
	}

	public List<MatchFeedItem> getUserMatchesFilteredByDistance(Integer userId, Integer distance) {
		Iterable<MatchFeedItem> feedIterable = repository.findByUserIdAndDistanceLessThan(userId, distance);
		List<MatchFeedItem> feedItems = Lists.newArrayList();

		if (feedIterable == null) {
			return feedItems;
		}
		feedIterable.iterator().forEachRemaining(t -> {
			feedItems.add(t);
		});
		return feedItems;
	}
	
	public List<MatchFeedItem> saveMatchFeedItems(List<MatchFeedItem> feedItemsList) {
		return repository.save(feedItemsList);
	}
}
