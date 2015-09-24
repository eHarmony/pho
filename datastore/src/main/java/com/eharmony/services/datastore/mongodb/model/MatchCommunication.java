package com.eharmony.services.datastore.mongodb.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class MatchCommunication {

	@Field("stage")
	private Integer stage;
	
	@Indexed
	@Field("lcommdate")
	private Date  lastCommDate;
	
	@Field("commStartedDate")
	private Date commStartedDate;
	
	@Field("readDate")
	private Date readDate;
	
	@Field("chooseMHCSDate")
	private Date chooseMHCSDate;
	
	@Field("ibStatus")
	private Integer icebreakerStatus;
	
	@Field("fstatus")
	private Integer fastTrackStatus;
	
	@Field("turnOwner")
	private Integer turnOwner;
	
	public Integer getStage() {
		return stage;
	}
	public void setStage(Integer stage) {
		this.stage = stage;
	}
	public Date getLastCommDate() {
		return lastCommDate;
	}
	public void setLastCommDate(Date lastCommDate) {
		this.lastCommDate = lastCommDate;
	}
	public Date getCommStartedDate() {
		return commStartedDate;
	}
	public void setCommStartedDate(Date commStartedDate) {
		this.commStartedDate = commStartedDate;
	}
	public Date getReadDate() {
		return readDate;
	}
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	public Date getChooseMHCSDate() {
		return chooseMHCSDate;
	}
	public void setChooseMHCSDate(Date chooseMHCSDate) {
		this.chooseMHCSDate = chooseMHCSDate;
	}
	public Integer getIcebreakerStatus() {
		return icebreakerStatus;
	}
	public void setIcebreakerStatus(Integer icebreakerStatus) {
		this.icebreakerStatus = icebreakerStatus;
	}
	public Integer getFastTrackStatus() {
		return fastTrackStatus;
	}
	public void setFastTrackStatus(Integer fastTrackStatus) {
		this.fastTrackStatus = fastTrackStatus;
	}
	public Integer getTurnOwner() {
		return turnOwner;
	}
	public void setTurnOwner(Integer turnOwner) {
		this.turnOwner = turnOwner;
	}
	
}
