package com.eharmony.services.mymatchesservice.service;

import java.util.Set;

import com.eharmony.singles.common.status.MatchStatus;

public class HBaseStoreCountResponse {
	private boolean recentNew = false;
	private MatchStatus matchStatus;
	private Set<Long> matchIds;

	public Set<Long> getMatchIds() {
		return matchIds;
	}

	public void setMatchIds(Set<Long> set) {
		this.matchIds = set;
	}

	public MatchStatus getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus) {
		this.matchStatus = matchStatus;
	}

	public boolean isRecentNew() {
		return recentNew;
	}

	public void setRecentNew(boolean recentNew) {
		this.recentNew = recentNew;
	}

}
