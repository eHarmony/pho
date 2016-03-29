package com.eharmony.services.mymatchesservice.service.transform;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDtoWrapper;
import com.google.common.collect.Maps;

@Component
public class HBASEToLegacyFeedTransformer {

    @Resource
    private LegacyMatchFeedTransformer legacyMatchFeedTransformer;
    
    public void transformHBASEFeedToLegacyFeedIfRequired(MatchFeedRequestContext context) {
        // this mapping is required only when there is no feed from voldy and we got ALL info from HBASE as a fallback
        if(!context.isFallbackRequest()) {
            return;
        }
        
        transformHBASEFeedToLegacyFeed(context);
    }
    
    public void transformHBASEFeedToLegacyFeed(MatchFeedRequestContext context) {

        // transform the hbase data
        LegacyMatchDataFeedDto legacyMatchFeedDto = legacyMatchFeedTransformer.transform(context);
        
        // set it as though it was voldy data
        LegacyMatchDataFeedDtoWrapper legacyMatchDataFeedDtoWrapper = new LegacyMatchDataFeedDtoWrapper(context.getUserId());
        legacyMatchDataFeedDtoWrapper.setFeedAvailable(true);
        legacyMatchDataFeedDtoWrapper.setLegacyMatchDataFeedDto(legacyMatchFeedDto);
        int matchesCount = 0;
        if(legacyMatchFeedDto != null && MapUtils.isNotEmpty(legacyMatchFeedDto.getMatches()) ){
            matchesCount = legacyMatchFeedDto.getMatches().size();
        }
        legacyMatchDataFeedDtoWrapper.setVoldyMatchesCount(matchesCount);
        context.setLegacyMatchDataFeedDtoWrapper(legacyMatchDataFeedDtoWrapper);
        
        // clear up hbase records after transformation
        context.setHbaseFeedItemsByStatusGroup(Maps.newHashMap());
    }
}
