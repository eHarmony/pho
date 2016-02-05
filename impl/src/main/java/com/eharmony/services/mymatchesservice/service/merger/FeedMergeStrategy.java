package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
/**
 * Strategy to merge feed from different resource.
 * @author gwang
 *
 */
public interface FeedMergeStrategy{

    /**
     * Merge match feed from different source.
     * @param request context that has feed from different sources.
     */
    void merge(MatchFeedRequestContext request);
}
