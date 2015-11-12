package com.eharmony.services.mymatchesservice.service.filter;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMatchFeedFilter implements IMatchFeedFilter {
	
	Logger logger = LoggerFactory.getLogger(AbstractMatchFeedFilter.class);

	@Override
	public MatchFeedFilterContext processMatchFeed(MatchFeedFilterContext context) {

        if (context == null) {
        	logger.debug("Match feed context is null, returning without processing. Context={}",
                      context);
            return context;
        }

        for (Iterator<Map.Entry<String, Map<String, Map<String, Object>>>> matchIterator =
             context.getFeedMap().entrySet().iterator(); matchIterator.hasNext();) {

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
     * @param   context    MatchFeedFilterContext
     *
     * @return  true if the transformation is successful, false otherwise
     */
    protected abstract boolean processMatchSection( Map<String, Object> matchSection,
    											MatchFeedFilterContext context);

}
