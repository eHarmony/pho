package com.eharmony.services.mymatchesservice.rest;

import java.util.Map;
import java.util.Set;

public class MatchFeedQueryContextBuilder {

    private long userId;
    private long matchId;
    private String locale;
    private int startPage;
    private int pageSize;
    private Set<String> statuses;
    private boolean viewHidden;
    private boolean allowedSeePhotos;
    private int teaserResultSize;
    private Map<String, String> requestMetadata;
    private String sortBy;
    private boolean excludeClosedMatches;

    private MatchFeedQueryContextBuilder() {
    }

    public MatchFeedQueryContext build() {

        return new MatchFeedQueryContextImpl(userId, matchId, locale, startPage, pageSize, statuses, viewHidden,
        			allowedSeePhotos, teaserResultSize, requestMetadata, sortBy, excludeClosedMatches);

    }

    private class MatchFeedQueryContextImpl implements MatchFeedQueryContext {

        private final long userId;
        private final long matchId;
        private final String locale;
        private final int startPage;
        private final int pageSize;
        private final Set<String> statuses;
        private final boolean viewHidden;
        private final boolean allowedSeePhotos;
        private final int teaserResultSize;
        private final Map<String, String> requestMetadata;
        private final String sortBy;
        private final boolean excludeClosedMatches;
        
        @Override
        public long getUserId() {
            return userId;
        }

        @Override
        public long getMatchId() {
            return matchId;
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
        
        private MatchFeedQueryContextImpl(final long userId, final long matchId, final String locale, final int startPage,
                final int pageSize, final Set<String> statuses, final boolean viewHidden,
                final boolean allowedSeePhotos, int teaserResultSize,
                final Map<String, String> requestMetadata, final String sortBy, final boolean excludeClosedMatches) {

            this.userId = userId;
            this.matchId = matchId;
            this.locale = locale;
            this.startPage = startPage;
            this.pageSize = pageSize;
            this.statuses = statuses;
            this.viewHidden = viewHidden;
            this.allowedSeePhotos = allowedSeePhotos;
            this.teaserResultSize = teaserResultSize;
            this.requestMetadata = requestMetadata;
            this.sortBy = sortBy;
            this.excludeClosedMatches = excludeClosedMatches;

        }

        @Override
        public Map<String, String> getRequestMetadata() {
            return requestMetadata;
        }

        @Override
        public String getSortBy() {
            return sortBy;
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

    public MatchFeedQueryContextBuilder setMatchId(long matchId) {
        this.matchId = matchId;
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

    public MatchFeedQueryContextBuilder setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }
    
	public MatchFeedQueryContextBuilder setExcludeClosedMatches(boolean excludeClosedMatches) {
		this.excludeClosedMatches = excludeClosedMatches;
		return this;
	}

}
