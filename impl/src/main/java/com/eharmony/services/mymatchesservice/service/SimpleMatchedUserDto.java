package com.eharmony.services.mymatchesservice.service;

import java.io.Serializable;
import java.util.Date;

public class SimpleMatchedUserDto implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2966839896754261974L;
    private String matchId;
    private String matchUserFirstName;
    private String encryptedMatchUserId;
    private Boolean hasPrimaryPhoto;
    private Integer age;
    private Date deliveredDate;
    

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
