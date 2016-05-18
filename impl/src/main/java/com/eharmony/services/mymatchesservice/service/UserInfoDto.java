package com.eharmony.services.mymatchesservice.service;

public class UserInfoDto {

	private long userId;
	private int gender;
	private int status;
	private String locale;
	private long lastLoginDate;
	private long registrationDate;
	private int redFlagFilterStatus;
	private Object userOptions;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public long getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(long lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public long getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(long registrationDate) {
		this.registrationDate = registrationDate;
	}
	public int getRedFlagFilterStatus() {
		return redFlagFilterStatus;
	}
	public void setRedFlagFilterStatus(int redFlagFilterStatus) {
		this.redFlagFilterStatus = redFlagFilterStatus;
	}
	public Object getUserOptions() {
		return userOptions;
	}
	public void setUserOptions(Object userOptions) {
		this.userOptions = userOptions;
	}
	
	
}
