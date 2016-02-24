package com.eharmony.services.mymatchesservice.rest;

import com.eharmony.singles.common.status.MatchStatus;

public class MatchCountRequestContext {
    private boolean recentNew = false;
    private long userId;
    private MatchStatus status;
    
    
    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public MatchCountRequestContext() {
    }
    
    
    public MatchStatus getStatus() {
        return status;
    }
    public void setStatus(MatchStatus status) {
        this.status = status;
    }
    public boolean isRecentNew() {
        return recentNew;
    }
    public void setRecentNew(boolean recentNew) {
        this.recentNew = recentNew;
    }
	public MatchCountRequestContext(long userId) {
		super();
		this.userId = userId;
	}
    
    

}
