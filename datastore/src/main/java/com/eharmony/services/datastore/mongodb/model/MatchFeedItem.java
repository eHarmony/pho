package com.eharmony.services.datastore.mongodb.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class MatchFeedItem {

	@Id
	@Indexed
	@Field("mid")
	private Long matchId;
	
	@Indexed
	@Field("uid")
	private Long userId;
	
	@Field("muid")
	private Long matchUserId;
	
	@Indexed
	@Field("ddate")
	private Date deliveredDate;
	
	@Field("lmodified")
	private Date lastModified;
	
	@Field("closedStatus")
	private Integer closedStatus;
	
	@Field("archStatus")
	private Integer archivedStatus;
	
	@Indexed
	@Field("mstate")
	private Integer matchState;
	
	@Field("1wayStatus")
	private Integer oneWayStatus;
	
	@Field("isUser")
	private Boolean isUser;
	
	@Field("closedDate")
	private Date closedDate;
	
	@Field("relaxed")
	private Boolean relaxed;
	
	@Indexed
	@Field("distance")
	private Integer distance;
	
	@Field("initializer")
	private Integer initializer;
	
	private MatchCommunication communication;
	private MatchProfile matchProfile;
	
	public Long getMatchId() {
		return matchId;
	}
	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}
	
	public Date getDeliveredDate() {
		return deliveredDate;
	}
	public void setDeliveredDate(Date deliveredDate) {
		this.deliveredDate = deliveredDate;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public Integer getClosedStatus() {
		return closedStatus;
	}
	public void setClosedStatus(Integer closedStatus) {
		this.closedStatus = closedStatus;
	}
	public Integer getArchivedStatus() {
		return archivedStatus;
	}
	public void setArchivedStatus(Integer archivedStatus) {
		this.archivedStatus = archivedStatus;
	}
	public Integer getMatchState() {
		return matchState;
	}
	public void setMatchState(Integer matchState) {
		this.matchState = matchState;
	}
	public Integer getOneWayStatus() {
		return oneWayStatus;
	}
	public void setOneWayStatus(Integer oneWayStatus) {
		this.oneWayStatus = oneWayStatus;
	}
	public Boolean getIsUser() {
		return isUser;
	}
	public void setIsUser(Boolean isUser) {
		this.isUser = isUser;
	}
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	
	public Boolean getRelaxed() {
		return relaxed;
	}
	public void setRelaxed(Boolean relaxed) {
		this.relaxed = relaxed;
	}
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public Integer getInitializer() {
		return initializer;
	}
	public void setInitializer(Integer initializer) {
		this.initializer = initializer;
	}
	public MatchCommunication getCommunication() {
		return communication;
	}
	public void setCommunication(MatchCommunication communication) {
		this.communication = communication;
	}
	public MatchProfile getMatchProfile() {
		return matchProfile;
	}
	public void setMatchProfile(MatchProfile matchProfile) {
		this.matchProfile = matchProfile;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getMatchUserId() {
		return matchUserId;
	}
	public void setMatchUserId(Long matchUserId) {
		this.matchUserId = matchUserId;
	}
	
	
}
