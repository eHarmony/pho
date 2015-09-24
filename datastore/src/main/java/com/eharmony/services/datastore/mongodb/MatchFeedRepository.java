package com.eharmony.services.datastore.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.eharmony.services.datastore.mongodb.model.MatchFeedItem;

@Repository
public interface MatchFeedRepository extends MongoRepository<MatchFeedItem, String> {

    public MatchFeedItem findByMatchId(Long matchId);
    public Iterable<MatchFeedItem> findByUserId(Integer userId);
    public MatchFeedItem findByUserIdAndMatchId(Integer userId, Long matchId);
    public Iterable<MatchFeedItem> findByUserIdAndDistanceLessThan(Integer userId, int distance);
    
}
