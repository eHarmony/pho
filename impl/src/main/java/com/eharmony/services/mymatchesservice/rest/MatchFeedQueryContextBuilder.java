package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

public class MatchFeedQueryContextBuilder {

    private long userId;
    private String locale;
    private int startPage;
    private int pageSize;
    private Set<String> statuses;
    private boolean viewHidden;
    private boolean allowedSeePhotos;

    private boolean internalDisableVoldy;

    private MatchFeedQueryContextBuilder() {
        
    }
    public MatchFeedQueryContext build() {
        return new MatchFeedQueryContextImpl(userId, locale, startPage, pageSize, statuses, viewHidden,
                allowedSeePhotos, internalDisableVoldy);
    }

    private class MatchFeedQueryContextImpl implements MatchFeedQueryContext {

        private final long userId;
        private final String locale;
        private final int startPage;
        private final int pageSize;
        private final Set<String> statuses;
        private final boolean viewHidden;
        private final boolean allowedSeePhotos;
        
        private final boolean internalDisableVoldy;

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
        public boolean isDisableVoldemort() {
            return internalDisableVoldy;
        }
        
        private MatchFeedQueryContextImpl(final long userId, final String locale, final int startPage,
                final int pageSize, final Set<String> statuses, final boolean viewHidden, 
                final boolean allowedSeePhotos, final boolean internalDisableVoldy) {
            this.userId = userId;
            this.locale = locale;
            this.startPage = startPage;
            this.pageSize = pageSize;
            this.statuses = statuses;
            this.viewHidden = viewHidden;
            this.allowedSeePhotos = allowedSeePhotos;
            
            this.internalDisableVoldy = internalDisableVoldy;
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

    public MatchFeedQueryContextBuilder setDisableVoldemort(boolean disableVoldy) {
    	this.internalDisableVoldy = disableVoldy;
    	return this;
    }
}
