package com.eharmony.services.mymatchesservice.service;

public class MRSDto {

	private long userId;
	private long matchId;
	private long matchedUserId;
	private int distance;
	private String deliveryDate;
	private int closeFlag;
	private int oneWay;
	
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
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public int getCloseFlag() {
		return closeFlag;
	}
	public void setCloseFlag(int closeFlag) {
		this.closeFlag = closeFlag;
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
}
