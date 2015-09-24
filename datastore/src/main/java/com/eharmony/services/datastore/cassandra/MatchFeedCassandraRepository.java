package com.eharmony.services.datastore.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.eharmony.services.datastore.cassandra.model.CassandraMatchFeedItem;

@Repository
public interface MatchFeedCassandraRepository extends CassandraRepository<CassandraMatchFeedItem> {

	@Query("select * from user_matches where uid = ?0")
    public Iterable<CassandraMatchFeedItem> findByUserId(Integer userId);
    /*public CassandraMatchFeedItem findByUserIdAndMatchId(Integer userId, Long matchId);
    public Iterable<CassandraMatchFeedItem> findByUserIdAndDistanceLessThan(Integer userId, int distance);*/
    
}
