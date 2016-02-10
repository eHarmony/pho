package com.eharmony.services.mymatchesservice.rest;

public class MatchCountRequestContext {
	private boolean viewHidden = false;
	private long userId;
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

}
