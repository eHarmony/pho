package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;

public abstract class AbstractMatchFeedSectionTransformer implements IMatchFeedTransformer{
	
	Logger logger = LoggerFactory.getLogger(AbstractMatchFeedSectionTransformer.class);

	public MatchFeedRequestContext processMatchFeed(MatchFeedRequestContext context) {

        if (context == null) {
        	logger.debug("Match feed context is null, returning without processing. Context={}",
                      context);
            return context;
        }
        
        if(context.getLegacyMatchDataFeedDto() == null){
        	logger.debug("LegacyMatchDataFeedDto is null, returning without processing. Context={}", context);
        	return context;
        }

        for (Iterator<Map.Entry<String, Map<String, Map<String, Object>>>> matchIterator =
             context.getLegacyMatchDataFeedDto().getMatches().entrySet().iterator(); matchIterator.hasNext();) {

            Map<String, Map<String, Object>> matchInfo = matchIterator.next().getValue();
            Map<String, Object> section = matchInfo.get(getMatchSectionName());

            if (!processMatchSection(section, context)) {

                // remove the current matchInfo from the iterator, continue with the next matchInfo
            	matchIterator.remove();
            	logger.debug("MatchInfo filtered out of result set, matchInfo={}", matchInfo);

            }
        }
        
        return context;
	}
	
	/**
	 * Get name of match section to process. See MatchFeedModel.SECTIONS.
	 * 
	 * @return String
	 */
	protected abstract String getMatchSectionName();

    /**
     * Run filter on a matchInfo object. If the method
     * returns false, the object is removed from the feed.
     *
     * @param   matchSection  match info object
     * @param   context    MatchFeedRequestContext
     *
     * @return  true if the transformation is successful, false otherwise
     */
    protected abstract boolean processMatchSection( Map<String, Object> matchSection,
    											MatchFeedRequestContext context);

}
