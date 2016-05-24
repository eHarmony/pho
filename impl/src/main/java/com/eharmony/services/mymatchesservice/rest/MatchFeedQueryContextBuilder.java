package com.eharmony.services.mymatchesservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eharmony.datastore.query.criterion.Ordering;
import com.eharmony.datastore.query.criterion.Ordering.NullOrdering;
import com.eharmony.datastore.query.criterion.Ordering.Order;

public class MatchFeedQueryContextBuilder {

    private long userId;
    private String locale;
    private int startPage;
    private int pageSize;
    private Set<String> statuses;
    private boolean viewHidden;
    private boolean allowedSeePhotos;
    private int teaserResultSize;
    private Map<String, String> requestMetadata;
    private List<Ordering> orderings;
    private boolean excludeClosedMatches;
    private MatchFeedSearchAndFilterCriteria searchFilterCriteria;

    private MatchFeedQueryContextBuilder() {
    }

    public MatchFeedQueryContext build() {

        return new MatchFeedQueryContextImpl(userId, locale, startPage, pageSize, statuses, viewHidden,
                allowedSeePhotos, teaserResultSize, requestMetadata, orderings, excludeClosedMatches, searchFilterCriteria);
    }

    private class MatchFeedQueryContextImpl implements MatchFeedQueryContext {

        private final long userId;
        private final String locale;
        private final int startPage;
        private final int pageSize;
        private final Set<String> statuses;
        private final boolean viewHidden;
        private final boolean allowedSeePhotos;
        private final int teaserResultSize;
        private final Map<String, String> requestMetadata;
        private final List<Ordering> orderings;
        private final boolean excludeClosedMatches;
        private final MatchFeedSearchAndFilterCriteria searchFilterCriteria;
       
        @Override
        public long getUserId() {
            return userId;
        }

        @Override
        public String getLocale() {
            return locale;
        }

        @Override
        public int getStartPage() {
            return startPage;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public Set<String> getStatuses() {
            return statuses;
        }

        @Override
        public boolean isViewHidden() {
            return viewHidden;
        }

        @Override
        public boolean isAllowedSeePhotos() {
            return allowedSeePhotos;
        }

        @Override
        public int getTeaserResultSize() {
            return teaserResultSize;
        }
        
        private MatchFeedQueryContextImpl(final long userId, final String locale, final int startPage,
                final int pageSize, final Set<String> statuses, final boolean viewHidden,
                final boolean allowedSeePhotos, int teaserResultSize,
                final Map<String, String> requestMetadata, final List<Ordering> orderings, final boolean excludeClosedMatches,
                final MatchFeedSearchAndFilterCriteria searchFilterCriteria) {

            this.userId = userId;
            this.locale = locale;
            this.startPage = startPage;
            this.pageSize = pageSize;
            this.statuses = statuses;
            this.viewHidden = viewHidden;
            this.allowedSeePhotos = allowedSeePhotos;
            this.teaserResultSize = teaserResultSize;
            this.requestMetadata = requestMetadata;
            this.orderings = orderings;
            this.excludeClosedMatches = excludeClosedMatches;
            this.searchFilterCriteria = searchFilterCriteria;
        }

        public MatchFeedSearchAndFilterCriteria getSearchFilterCriteria() {
			return searchFilterCriteria;
		}

		@Override
        public Map<String, String> getRequestMetadata() {
            return requestMetadata;
        }

        @Override
        public List<Ordering> getOrderings() {
            return orderings;
        }

        @Override
		public boolean isExcludeClosedMatches() {
			return excludeClosedMatches;
		}
    }

    public static MatchFeedQueryContextBuilder newInstance() {
        return new MatchFeedQueryContextBuilder();
    }

    public MatchFeedQueryContextBuilder setUserId(long userId) {
        this.userId = userId;
        return this;
    }
    
    public MatchFeedQueryContextBuilder setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public MatchFeedQueryContextBuilder setStartPage(int startPage) {
        this.startPage = startPage;
        return this;
    }

    public MatchFeedQueryContextBuilder setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public MatchFeedQueryContextBuilder setStatuses(Set<String> statuses) {
        this.statuses = statuses;
        return this;
    }

    public MatchFeedQueryContextBuilder setViewHidden(boolean viewHidden) {
        this.viewHidden = viewHidden;
        return this;
    }

    public MatchFeedQueryContextBuilder setAllowedSeePhotos(boolean allowedSeePhotos) {
        this.allowedSeePhotos = allowedSeePhotos;
        return this;
    }

    public MatchFeedQueryContextBuilder setTeaserResultSize(int teaserResultSize) {
        this.teaserResultSize = teaserResultSize;
        return this;
    }

    public MatchFeedQueryContextBuilder setRequestMetadata(Map<String, String> requestMetadata) {
        this.requestMetadata = requestMetadata;
        return this;
    }

    public MatchFeedQueryContextBuilder setOrderings(List<Ordering> orderings) {
        this.orderings = orderings;
        return this;
    }
 
    public MatchFeedQueryContextBuilder setSearchAndFilterCriteria(MatchFeedSearchAndFilterCriteria criteria) {
        this.searchFilterCriteria = criteria;
        return this;
    }
    
    /**
     * Add a property name to the list of properties to order the results by.
     * The results will be in descending order and nulls will be last.
     * 
     * @param propertyName the name of the property to order the results by
     * @return this builder
     */
    public MatchFeedQueryContextBuilder addOrderBy(String propertyName) {
        if(orderings == null){
            orderings = new ArrayList<Ordering>();
        }
        Ordering ordering= new Ordering(propertyName, Order.DESCENDING, NullOrdering.LAST);
        orderings.add(ordering);
        return this;
    }
    
	public MatchFeedQueryContextBuilder setExcludeClosedMatches(boolean excludeClosedMatches) {
		this.excludeClosedMatches = excludeClosedMatches;
		return this;
	}

}
