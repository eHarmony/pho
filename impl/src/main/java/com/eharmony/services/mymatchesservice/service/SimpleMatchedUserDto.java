package com.eharmony.services.mymatchesservice.service;

import java.io.Serializable;
import java.util.Date;

public class SimpleMatchedUserDto implements Serializable{
    
    private static final long serialVersionUID = 2966839896754261974L;
    private String matchId;
    private String matchedUserFirstName;
    private String encryptedMatchedUserId;
    private Boolean hasPrimaryPhoto;
    private Integer age;
    private Date deliveredDate;
    private String matchedUserId;

    public Boolean getHasPrimaryPhoto() {
        return hasPrimaryPhoto;
    }
    public void setHasPrimaryPhoto(Boolean hasPrimaryPhoto) {
        this.hasPrimaryPhoto = hasPrimaryPhoto;
    }

    public String getMatchId() {
		return matchId;
	}
	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}
	public String getMatchedUserId() {
		return matchedUserId;
	}
	public void setMatchedUserId(String matchedUserId) {
		this.matchedUserId = matchedUserId;
	}
	public String getMatchedUserFirstName() {
		return matchedUserFirstName;
	}
	public void setMatchedUserFirstName(String matchedUserFirstName) {
		this.matchedUserFirstName = matchedUserFirstName;
	}
	public String getEncryptedMatchedUserId() {
		return encryptedMatchedUserId;
	}
	public void setEncryptedMatchedUserId(String encryptedMatchedUserId) {
		this.encryptedMatchedUserId = encryptedMatchedUserId;
	}
	public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
	public Date getDeliveredDate() {
		return deliveredDate;
	}
	public void setDeliveredDate(Date deliveredDate) {
		this.deliveredDate = deliveredDate;
	}

    
}
