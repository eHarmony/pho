package com.eharmony.services.mymatchesservice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import rx.Observable;

import com.eharmony.configuration.Configuration;
import com.eharmony.datastore.model.MatchDataFeedItemDto;
import com.eharmony.datastore.repository.MatchDataFeedItemQueryRequest;
import com.eharmony.datastore.repository.MatchDataFeedQueryRequest;
import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.datastore.repository.MatchStoreSaveRepository;
import com.eharmony.services.mymatchesservice.MergeModeEnum;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.merger.LegacyMatchDataFeedMergeStrategy;
import com.eharmony.services.mymatchesservice.service.transform.FeedTransformer;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;

@Service
public class UserMatchesFeedServiceImpl implements UserMatchesFeedService {

    private static final Logger logger = LoggerFactory.getLogger(UserMatchesFeedServiceImpl.class);

    @Resource
    private MatchStoreQueryRepository queryRepository;
    
    @Resource
    private MatchStoreSaveRepository saveRepository;

    @Resource
    private MatchDataFeedStore voldemortStore;

    @Resource
    private Configuration config;

    @Value("${feed.mergeMode}")
    private MergeModeEnum mergeMode;

    @Override
    public List<MatchDataFeedItemDto> getUserMatchesInternal(long userId) {
        MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest();
        request.setUserId(Long.valueOf(userId).intValue());
        try {
            Set<MatchDataFeedItemDto> matchDataFeeditems = queryRepository.getMatchDataFeed(request);
            if (CollectionUtils.isNotEmpty(matchDataFeeditems)) {
                logger.debug("found {} matches for user {}", matchDataFeeditems.size(), userId);
                return new ArrayList<MatchDataFeedItemDto>(matchDataFeeditems);
            }
        } catch (Exception ex) {
            logger.warn("exception while fetching matches", ex);
            throw new RuntimeException(ex);
        }
        logger.debug("no matches found  for user {}", userId);
        return new ArrayList<MatchDataFeedItemDto>();
    }

    @Override
    public LegacyMatchDataFeedDto getUserMatches(long userId) {

        MatchFeedRequestContext request = new MatchFeedRequestContext(userId);
        try {

            LegacyMatchDataFeedMergeStrategy merger = LegacyMatchDataFeedMergeStrategy.getMergeInstance(mergeMode,
                    queryRepository, voldemortStore);
            LegacyMatchDataFeedDto matchDataFeedItems = merger.merge(request);

            if (matchDataFeedItems.getMatches().isEmpty()) {
                logger.info("no matches found for user {}", userId);
                return null;
            }

            logger.info("found {} matches for user {}", matchDataFeedItems.getMatches().size(), userId);
            return matchDataFeedItems;

        } catch (Exception ex) {
            logger.warn("exception while fetching matches", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public MatchDataFeedItemDto getUserMatch(long userId, long matchId) {
        MatchDataFeedItemQueryRequest request = new MatchDataFeedItemQueryRequest();
        request.setUserId(Long.valueOf(userId).intValue());
        request.setMatchId(matchId);
        try {
            MatchDataFeedItemDto matchDataFeeditem = queryRepository.getMatchDataFeedItemDto(request);
            if (matchDataFeeditem != null) {
                logger.debug("found match for user {} and matchid {}", userId, matchId);
                return matchDataFeeditem;
            }
        } catch (Exception ex) {
            logger.warn("exception while fetching matches", ex);
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public Observable<Set<MatchDataFeedItemDto>> getUserMatchesFromStoreObservable(
            MatchFeedRequestContext requestContext) {
        return Observable.defer(() -> Observable.just(getMatchesFeed(requestContext)));
    }

    private Set<MatchDataFeedItemDto> getMatchesFeed(MatchFeedRequestContext request) {
        try {
            long startTime = System.currentTimeMillis();
            MatchDataFeedQueryRequest requestQuery = new MatchDataFeedQueryRequest();
            requestQuery.setUserId(Long.valueOf(request.getUserId()).intValue());
            Set<MatchDataFeedItemDto> matchdataFeed =  queryRepository.getMatchDataFeed(requestQuery);
            long endTime = System.currentTimeMillis();
            logger.info("Total time to get the feed from hbase is {} MS", endTime - startTime);
            return matchdataFeed;
        } catch (Exception e) {
            logger.warn("Exception while fetching the matches from HBase store for user {}", request.getUserId(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshFeedFromVoldemortToHBase(long userId) throws Exception {
    	
        LegacyMatchDataFeedDto voldyFeed =  voldemortStore.getMatches(userId);
        if(isEmptyVoldyFeed(voldyFeed)){
        	
        	throw new IllegalArgumentException("Unknown userId: " + userId);
        }
        
    	Set<MatchDataFeedItemDto> feedList = new HashSet<MatchDataFeedItemDto>();

    	for(String key : voldyFeed.getMatches().keySet()){
 
    		Map<String, Map<String, Object>> match = voldyFeed.getMatches().get(key);
    		
        	try{
	        	MatchDataFeedItemDto xform = FeedTransformer.mapFeedtoMatchDataFeedItemList(match);

	    		feedList.add(xform);
        	}catch(Exception ex){
        		
                logger.warn("Exception while transforming feed for user {}", userId);
                throw new RuntimeException(ex);
        	}
    	}
    	 
        saveRepository.saveMatchDataFeedItems(feedList);      
    }

	private boolean isEmptyVoldyFeed(LegacyMatchDataFeedDto voldyFeed) {
		
		return MapUtils.isEmpty(voldyFeed.getMatches());
	}

}
