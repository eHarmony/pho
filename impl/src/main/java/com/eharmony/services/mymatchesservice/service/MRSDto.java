package com.eharmony.services.mymatchesservice.service;

public class MRSDto {

	private long userId;
	private long matchId;
	private long matchedUserId;
	private int distance;
	private long deliveryDate;
	private int oneWay;
	private boolean relaxed;
	private int oneWayStatus;
	private int archiveStatus;
	private int closedStatus;
	
	public int getClosedStatus() {
		return closedStatus;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getMatchedUserId() {
		return matchedUserId;
	}
	public void setMatchedUserId(long matchedUserId) {
		this.matchedUserId = matchedUserId;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public long getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(long deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public int getOneWay() {
		return oneWay;
	}
	public void setOneWay(int oneWay) {
		this.oneWay = oneWay;
	}
	public long getMatchId() {
		return matchId;
	}
	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public void setArchiveStatus(int as) {
		this.archiveStatus = as;
	}
	public boolean getRelaxed() {
		return relaxed;
	}
	public int getOneWayStatus() {
		return oneWayStatus;
	}
	public int getArchiveStatus() {
		return archiveStatus;
	}
	public void setRelaxed(boolean userRelaxed) {
		this.relaxed = userRelaxed;
	}
	public void setOneWayStatus(int ows) {
		this.oneWayStatus = ows;
	}
	public void setClosedStatus(int number) {

		this.closedStatus = number;
	}
}
