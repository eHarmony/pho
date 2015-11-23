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
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContext;
import com.eharmony.services.mymatchesservice.rest.MatchFeedQueryContextBuilder;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.merger.FeedMergeStrategyType;
import com.eharmony.services.mymatchesservice.service.merger.LegacyMatchDataFeedMergeStrategy;
import com.eharmony.services.mymatchesservice.service.transform.FeedDtoTranslator;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;
import com.eharmony.services.mymatchesservice.util.MatchStatusEnum;
import com.google.common.collect.Sets;

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
    
    @Resource(name= "matchFeedProfileFieldsList")
    private List<String> selectedProfileFields;

    @Override
    public List<MatchDataFeedItemDto> getUserMatchesInternal(long userId) {
        MatchDataFeedQueryRequest request = new MatchDataFeedQueryRequest(userId);
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

        MatchFeedQueryContext queryContext = MatchFeedQueryContextBuilder.newInstance().setUserId(userId).build();
        MatchFeedRequestContext request = new MatchFeedRequestContext(queryContext);
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
        MatchDataFeedItemQueryRequest request = new MatchDataFeedItemQueryRequest(userId);
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
    public Observable<Set<MatchDataFeedItemDto>> getUserMatchesFromHBaseStoreSafe(MatchFeedRequestContext requestContext) {
        Observable<Set<MatchDataFeedItemDto>> hbaseStoreFeed =  Observable.defer(() -> Observable.just(getMatchesFeed(requestContext)));
        hbaseStoreFeed.onErrorReturn(ex -> {
            logger.warn("Exception while fetching data from hbase for user {} and returning empty set for safe method", requestContext.getUserId(), ex);
            return Sets.newHashSet();
        });
        return hbaseStoreFeed;
    }
    
    private Set<MatchDataFeedItemDto> getMatchesFeed(MatchFeedRequestContext request) {
        try {
            long startTime = System.currentTimeMillis();
            MatchDataFeedQueryRequest requestQuery = new MatchDataFeedQueryRequest(request.getUserId());
            populateWithQueryParams(request, requestQuery);
            Set<MatchDataFeedItemDto> matchdataFeed =  queryRepository.getMatchDataFeed(requestQuery);
            long endTime = System.currentTimeMillis();
            logger.info("Total time to get the feed from hbase is {} MS", endTime - startTime);
            return matchdataFeed;
        } catch (Exception e) {
            logger.warn("Exception while fetching the matches from HBase store for user {}", request.getUserId(), e);
            throw new RuntimeException(e);
        }
    }
    
    private void populateWithQueryParams(MatchFeedRequestContext request, MatchDataFeedQueryRequest requestQuery) {
        Set<String> statuses = request.getMatchFeedQueryContext().getStatuses();
        List<Integer> matchStatuses = new ArrayList<Integer>();
        if(CollectionUtils.isNotEmpty(statuses)) {
            for(String status : statuses) {
                MatchStatusEnum statusEnum = MatchStatusEnum.fromName(status);
                if(statusEnum != null) {
                    matchStatuses.add(statusEnum.toInt());
                }
            }
            if(CollectionUtils.isNotEmpty(matchStatuses)) {
                requestQuery.setMatchStatusFilters(matchStatuses);
            }
        }
        FeedMergeStrategyType strategy = request.getFeedMergeType();
        if(strategy != null && strategy == FeedMergeStrategyType.VOLDY_FEED_WITH_PROFILE_MERGE) {
            requestQuery.setSelectedFields(selectedProfileFields);
        }
    }

    @Override
    public void refreshFeedFromVoldemortToHBase(long userId) throws Exception {
    	
        LegacyMatchDataFeedDto voldyFeed =  voldemortStore.getMatches(userId);
        
        if(isEmptyVoldyFeed(voldyFeed)){
        	
        	return; // Nothing to do
        }
        
    	Set<MatchDataFeedItemDto> feedList = new HashSet<MatchDataFeedItemDto>();

    	for(String key : voldyFeed.getMatches().keySet()){
 
    		Map<String, Map<String, Object>> match = voldyFeed.getMatches().get(key);
    		
        	try{
	        	MatchDataFeedItemDto xform = FeedDtoTranslator.mapFeedtoMatchDataFeedItemList(match);

	    		feedList.add(xform);
        	}catch(Exception ex){
        		
                logger.warn("Exception while transforming feed for user {}", userId, ex);
                throw new RuntimeException(ex);
        	}
    	}
    	 
        saveRepository.saveMatchDataFeedItems(feedList);      
    }

	private boolean isEmptyVoldyFeed(LegacyMatchDataFeedDto voldyFeed) {
		
		if(voldyFeed == null){
			return true;
		}
		
		return MapUtils.isEmpty(voldyFeed.getMatches());
	}
}
