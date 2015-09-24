package com.eharmony.services.datastore.cassandra.model;

import java.util.Date;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.mongodb.core.index.Indexed;

@Table(value = "user_matches")
public class CassandraMatchFeedItem {

	@PrimaryKey
	private MatchFeedKey feedKey;
	
	@Column(value = "uid2")
	private Integer matchedUserId;

	@Indexed
	@Column(value = "delivered_date")
	private Date deliveredDate;

	@Column(value = "l_modified")
	private Date lastModified;

	@Column(value = "closed_status")
	private Integer closedStatus;

	@Column(value = "arc_status")
	private Integer archivedStatus;

	@Indexed
	@Column(value = "mstate")
	private Integer matchState;

	/*
	 * @Column(value="delivered_date") private Integer oneWayStatus;
	 */

	@Column(value = "is_user")
	private Boolean isUser;

	@Column(value = "closed_date")
	private Date closedDate;

	@Column(value = "relaxed")
	private Integer relaxed;

	/*
	 * @Indexed
	 * 
	 * @Column(value="delivered_date") private Integer distance;
	 */

	@Column(value = "initializer")
	private Integer initializer;

	/*
	 * cstage int, l_comm_date timestamp, comm_started_date timestamp, read_details_date timestamp, IB_status int,
	 * fst_status int, turn_owner int, fname text, bdate timestamp, gid int, city text, state text, country int,
	 * has_photo boolean,
	 */

	@Column(value="cstage")
	private Integer stage;

	// @Indexed
	@Column(value="l_comm_date")
	private Date lastCommDate;

	@Column(value="comm_started_date")
	private Date commStartedDate;

	@Column(value="read_details_date")
	private Date readDate;

	/*
	 * @Column(value="chooseMHCSDate") private Date chooseMHCSDate;
	 */

	@Column(value="IB_status")
	private Integer icebreakerStatus;

	@Column(value="fst_status")
	private Integer fastTrackStatus;

	@Column(value="turn_owner")
	private Integer turnOwner;

	// private MatchCommunication communication;

	@Column(value="fname")
	private String firstName;

	@Column(value="bdate")
	private Date birthDate;

	@Column(value="gid")
	private Integer genderId;

	@Column(value="city")
	private String city;

	@Column(value="state")
	private String state;

	@Column(value="country")
	private Integer countryId;

	@Column(value="has_photo")
	private Boolean hasPhoto;

	public MatchFeedKey getFeedKey() {
		return feedKey;
	}

	public void setFeedKey(MatchFeedKey feedKey) {
		this.feedKey = feedKey;
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

	public Integer getRelaxed() {
		return relaxed;
	}

	public void setRelaxed(Integer relaxed) {
		this.relaxed = relaxed;
	}

	public Integer getInitializer() {
		return initializer;
	}

	public void setInitializer(Integer initializer) {
		this.initializer = initializer;
	}

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getGenderId() {
		return genderId;
	}

	public void setGenderId(Integer genderId) {
		this.genderId = genderId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Boolean getHasPhoto() {
		return hasPhoto;
	}

	public void setHasPhoto(Boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
	}

	public Integer getMatchedUserId() {
		return matchedUserId;
	}

	public void setMatchedUserId(Integer matchedUserId) {
		this.matchedUserId = matchedUserId;
	}

	/*
	 * @Column(value="locale") private String locale;
	 */


}
