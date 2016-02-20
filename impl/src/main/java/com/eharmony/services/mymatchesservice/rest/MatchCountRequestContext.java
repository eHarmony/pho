package com.eharmony.services.mymatchesservice.rest;

import com.eharmony.singles.common.status.MatchStatus;

public class MatchCountRequestContext {
	private boolean viewHidden = false;
	private long userId;
	private MatchStatus status;
	public boolean isViewHidden() {
		return viewHidden;
	}
	public void setViewHidden(boolean viewHidden) {
		this.viewHidden = viewHidden;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public MatchCountRequestContext() {
	}
	
	public MatchCountRequestContext(long userId, boolean viewHidden) {
		this.viewHidden = viewHidden;
		this.userId = userId;
	}
	public MatchStatus getStatus() {
		return status;
	}
	public void setStatus(MatchStatus status) {
		this.status = status;
	}
	
	

}
