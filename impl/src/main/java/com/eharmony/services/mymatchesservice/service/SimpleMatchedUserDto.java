package com.eharmony.services.mymatchesservice.service;

import java.io.Serializable;

public class SimpleMatchedUserDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2966839896754261974L;
	private String matchUserId;
	private String matchUserFirstName;
	private String encryptedMatchUserId;
	private Boolean hasPrimaryPhoto;
	

	public Boolean getHasPrimaryPhoto() {
		return hasPrimaryPhoto;
	}
	public void setHasPrimaryPhoto(Boolean hasPrimaryPhoto) {
		this.hasPrimaryPhoto = hasPrimaryPhoto;
	}
	public String getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(String matchUserId) {
		this.matchUserId = matchUserId;
	}
	public String getMatchUserFirstName() {
		return matchUserFirstName;
	}
	public void setMatchUserFirstName(String matchUserFirstName) {
		this.matchUserFirstName = matchUserFirstName;
	}
	public String getEncryptedMatchUserId() {
		return encryptedMatchUserId;
	}
	public void setEncryptedMatchUserId(String encryptedMatchUserId) {
		this.encryptedMatchUserId = encryptedMatchUserId;
	}
	

}
