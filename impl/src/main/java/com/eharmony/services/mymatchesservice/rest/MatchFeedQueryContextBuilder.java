package com.eharmony.services.mymatchesservice.rest;

import java.util.Set;

import com.eharmony.services.mymatchesservice.rest.internal.DataServiceStateEnum;

public class MatchFeedQueryContextBuilder {

    private long userId;
    private String locale;
    private int startPage;
    private int pageSize;
    private Set<String> statuses;
    private boolean viewHidden;
    private boolean allowedSeePhotos;
    private int teaserResultSize;
    private boolean useV2CommNextSteps;

    // Internal test flags
    private DataServiceStateEnum voldyState;


    private MatchFeedQueryContextBuilder() {
        
    }
    public MatchFeedQueryContext build() {
        return new MatchFeedQueryContextImpl(userId, locale, startPage, pageSize, statuses, viewHidden,
                allowedSeePhotos, voldyState, teaserResultSize, useV2CommNextSteps);
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
        private final boolean useV2CommNextSteps;

        private final DataServiceStateEnum voldyState;


        @Override
        public DataServiceStateEnum getVoldyState() {
			return voldyState;
		}

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
        
        @Override
        public boolean isUseV2CommNextSteps() {
        
            return useV2CommNextSteps;
            
        }

        private MatchFeedQueryContextImpl(final long userId, final String locale, final int startPage,
                final int pageSize, final Set<String> statuses, final boolean viewHidden, 
                final boolean allowedSeePhotos, final DataServiceStateEnum voldyState, int teaserResultSize, boolean useV2CommNextSteps) {

        	this.userId = userId;
            this.locale = locale;
            this.startPage = startPage;
            this.pageSize = pageSize;
            this.statuses = statuses;
            this.viewHidden = viewHidden;
            this.allowedSeePhotos = allowedSeePhotos;
            this.teaserResultSize = teaserResultSize;
            this.useV2CommNextSteps = useV2CommNextSteps;
            
            this.voldyState = (voldyState == null ? DataServiceStateEnum.ENABLED : voldyState);
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
    
	public MatchFeedQueryContextBuilder setVoldyState(DataServiceStateEnum voldyState) {
		this.voldyState = voldyState;
		return this;
	}
	
	public MatchFeedQueryContextBuilder setTeaserResultSize(int teaserResultSize) {
		this.teaserResultSize = teaserResultSize;
		return this;
	}
	
	public MatchFeedQueryContextBuilder setUseV2CommNextSteps(boolean useV2CommNextSteps) {
	     this.useV2CommNextSteps = useV2CommNextSteps;
	     return this;
	}

}
