package com.eharmony.services.mymatchesservice.service.merger;

import com.eharmony.datastore.repository.MatchStoreQueryRepository;
import com.eharmony.services.mymatchesservice.MergeModeEnum;
import com.eharmony.services.mymatchesservice.store.LegacyMatchDataFeedDto;
import com.eharmony.services.mymatchesservice.store.MatchDataFeedStore;


/**
 * A FeedMergeStrategy that produces MatchInfoModel "legacy" feed.
 *
 * @author kmunroe
 *
 */
public abstract class LegacyMatchDataFeedMergeStrategy
    implements FeedMergeStrategy<LegacyMatchDataFeedDto> {	


    public static LegacyMatchDataFeedMergeStrategy getMergeInstance(
    												MergeModeEnum mergeMode,
    												MatchStoreQueryRepository repository,
    												MatchDataFeedStore voldemortStore) {
        switch (mergeMode) {
        case VOLDEMORT_ONLY:
            return new VoldyOnlyMergeStrategy(voldemortStore);

        case VOLDEMORT_WITH_HBASE_PROFILE:
            return new VoldyWithHBaseProfileMergeStrategy(voldemortStore, repository);

        default:
            throw new UnsupportedOperationException("Unsupported merge mode " +
                mergeMode);
        }
    }
}
