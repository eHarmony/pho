/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2009 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.store.data;

import com.eharmony.singles.common.data.Persistable;
import com.eharmony.singles.common.enumeration.*;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * Read-only persistent object representing a record in singles_owner.ehMatches.
 * Despite there being setters, used for reflection, a persistence attempt would
 * yield a Hibernate exception.  Note: unlike the MutableMatchDo, instances of
 * this class do not have MatchSummaryDos, and hence do not join its respective
 * table
 *
 * @author  jtafralian
 */
public abstract class MatchDo
          implements Persistable<Long> {

    private static final long serialVersionUID = 1259963878564915955L;
    protected MatchArchiveStatusEnum archiveStatus;
    protected Date candidateClosedDate;
    protected Date candidateDeliveredDate;
    protected MatchDisplayTabEnum candidateDisplayTab;
    protected String candidateFirstName;
    protected Integer candidateNewMessageCount;
    protected Boolean candidateReadDetails;
    protected Date candidateReadDetailsDate;
    protected Boolean candidateRelaxed;
    protected Long candidateUserId;
    protected MatchClosedStatusEnum closedStatus;
    protected Integer distance;
    protected IcebreakerStateEnum icebreakerStatus;
    protected Long id;
    protected MatchInitializerEnum initializer;
    protected Date lastNudgeDate;
    protected Integer matchClosedCount;
    protected NudgeStatusEnum nudgeStatus;
    protected OneWayStatusEnum oneWayStatus;
    protected Integer stage;
    protected Long turnOwner;
    protected Date userClosedDate;
    protected Date userDeliveredDate;

    protected MatchDisplayTabEnum userDisplayTab;
    protected String userFirstName;
    protected Long userId;
    protected Integer userNewMessageCount;
    protected Boolean userReadDetails;
    protected Date userReadDetailsDate;
    protected Boolean userRelaxed;
    protected CommunicationDo communication;

    
    
    /**
     * Default constructor
     */
    public MatchDo() {

    }

    /**
     * Parameterized constructor for testing
     *
     * @param  id
     * @param  userId
     * @param  candidateUserId
     */
    public MatchDo(Long id,
                       Long userId,
                       Long candidateUserId) {

        this.id = id;
        this.userId = userId;
        this.candidateUserId = candidateUserId;

    }

    @Override public boolean equals(Object obj) {

        boolean equals = obj instanceof MatchDo;
        if (equals) {

            MatchDo other = (MatchDo) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.getCandidateClosedDate(), other.getCandidateClosedDate());
            builder.append(this.getCandidateDeliveredDate(), other.getCandidateDeliveredDate());
            builder.append(this.getCandidateFirstName(), other.getCandidateFirstName());
            builder.append(this.getCandidateNewMessageCount(), other.getCandidateNewMessageCount());
            builder.append(this.getCandidateReadDetails(), other.getCandidateReadDetails());
            builder.append(this.getCandidateReadDetailsDate(), other.getCandidateReadDetailsDate());
            builder.append(this.getCandidateRelaxed(), other.getCandidateRelaxed());
            builder.append(this.getCandidateUserId(), other.getCandidateUserId());
            builder.append(this.getCommunication(), other.getCommunication());
            builder.append(this.getDistance(), other.getDistance());
            builder.append(this.getIcebreakerStatus(), other.getIcebreakerStatus());
            builder.append(this.getId(), other.getId());
            builder.append(this.getInitializer(), other.getInitializer());
            builder.append(this.getLastNudgeDate(), other.getLastNudgeDate());
            builder.append(this.getClosedStatus(), other.getClosedStatus());
            builder.append(this.getMatchClosedCount(), other.getMatchClosedCount());
            builder.append(this.getNudgeStatus(), other.getNudgeStatus());
            builder.append(this.getOneWayStatus(), other.getOneWayStatus());
            builder.append(this.getStage(), other.getStage());
            builder.append(this.getTurnOwner(), other.getTurnOwner());
            builder.append(this.getUserClosedDate(), other.getUserClosedDate());
            builder.append(this.getUserDeliveredDate(), other.getUserDeliveredDate());
            builder.append(this.getUserFirstName(), other.getUserFirstName());
            builder.append(this.getUserNewMessageCount(), other.getUserNewMessageCount());
            builder.append(this.getUserReadDetails(), other.getUserReadDetails());
            builder.append(this.getUserReadDetailsDate(), other.getUserReadDetailsDate());
            builder.append(this.getUserRelaxed(), other.getUserRelaxed());
            builder.append(this.getUserId(), other.getUserId());
            builder.append(this.getCommunication(), other.getCommunication());
            builder.append(userDisplayTab, other.userDisplayTab);
            builder.append(candidateDisplayTab, other.candidateDisplayTab);
            equals = builder.isEquals();

        }

        return equals;

    }

    public MatchArchiveStatusEnum getArchiveStatus() {

        return archiveStatus;

    }

    public Date getCandidateClosedDate() {

        return candidateClosedDate;

    }

    public Date getCandidateDeliveredDate() {

        return candidateDeliveredDate;

    }

    public MatchDisplayTabEnum getCandidateDisplayTab() {

        return candidateDisplayTab;

    }

    public String getCandidateFirstName() {

        return candidateFirstName;

    }

    public Integer getCandidateNewMessageCount() {

        return candidateNewMessageCount;

    }

    public Boolean getCandidateReadDetails() {

        return candidateReadDetails;

    }

    public Date getCandidateReadDetailsDate() {

        return candidateReadDetailsDate;

    }

    public Boolean getCandidateRelaxed() {

        return candidateRelaxed;

    }

    public Long getCandidateUserId() {

        return candidateUserId;

    }

    public MatchClosedStatusEnum getClosedStatus() {

        return closedStatus;

    }

    public CommunicationDo getCommunication() {
        return communication;
    }

    public void setCommunication(CommunicationDo communication) {
        this.communication = (CommunicationDo) communication;
    }
    
    public Integer getDistance() {

        return distance;

    }

    public IcebreakerStateEnum getIcebreakerStatus() {

        return icebreakerStatus;

    }

    public Long getId() {

        return id;

    }

    public MatchInitializerEnum getInitializer() {

        return initializer;

    }



    public Date getLastNudgeDate() {

        return lastNudgeDate;

    }

    public Integer getMatchClosedCount() {

        return matchClosedCount;

    }

    public NudgeStatusEnum getNudgeStatus() {

        return nudgeStatus;

    }

    public OneWayStatusEnum getOneWayStatus() {

        return oneWayStatus;

    }

    public Integer getStage() {

        return stage;

    }

    public Long getTurnOwner() {

        return turnOwner;

    }

    public Date getUserClosedDate() {

        return userClosedDate;

    }

    public Date getUserDeliveredDate() {

        return userDeliveredDate;

    }

    public MatchDisplayTabEnum getUserDisplayTab() {

        return userDisplayTab;

    }

    public String getUserFirstName() {

        return userFirstName;

    }

    public Long getUserId() {

        return userId;

    }

    public Integer getUserNewMessageCount() {

        return userNewMessageCount;

    }

    public Boolean getUserReadDetails() {

        return userReadDetails;

    }

    public Date getUserReadDetailsDate() {

        return userReadDetailsDate;

    }

    public Boolean getUserRelaxed() {

        return userRelaxed;

    }

    @Override public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.getId());
        builder.append(this.getCandidateClosedDate());
        builder.append(this.getCandidateDeliveredDate());
        builder.append(this.getCandidateFirstName());
        builder.append(this.getCandidateNewMessageCount());
        builder.append(this.getCandidateReadDetails());
        builder.append(this.getCandidateReadDetailsDate());
        builder.append(this.getCandidateRelaxed());
        builder.append(this.getCandidateUserId());
        builder.append(this.getCommunication());
        builder.append(this.getDistance());
        builder.append(this.getIcebreakerStatus());
        builder.append(this.getInitializer());
        builder.append(this.getLastNudgeDate());
        builder.append(this.getMatchClosedCount());
        builder.append(this.getClosedStatus());
        builder.append(this.getNudgeStatus());
        builder.append(this.getOneWayStatus());
        builder.append(this.getStage());
        builder.append(this.getTurnOwner());
        builder.append(this.getUserClosedDate());
        builder.append(this.getUserDeliveredDate());
        builder.append(this.getUserFirstName());
        builder.append(this.getUserNewMessageCount());
        builder.append(this.getUserReadDetails());
        builder.append(this.getUserReadDetailsDate());
        builder.append(this.getUserRelaxed());
        builder.append(this.getUserId());
        builder.append(this.getCommunication());
        builder.append(userDisplayTab);
        builder.append(candidateDisplayTab);
        return builder.toHashCode();

    }

    public void setArchiveStatus(MatchArchiveStatusEnum archiveStatus) {

        this.archiveStatus = archiveStatus;

    }

    public void setCandidateClosedDate(Date candidateClosedDate) {

        this.candidateClosedDate = candidateClosedDate;

    }

    public void setCandidateDeliveredDate(Date candidateDeliveredDate) {

        this.candidateDeliveredDate = candidateDeliveredDate;

    }

    public void setCandidateDisplayTab(MatchDisplayTabEnum candidateDisplayTab) {

        this.candidateDisplayTab = candidateDisplayTab;

    }

    public void setCandidateFirstName(String candidateFirstName) {

        this.candidateFirstName = candidateFirstName;

    }

    public void setCandidateNewMessageCount(Integer candidateNewMessageCount) {

        this.candidateNewMessageCount = candidateNewMessageCount;

    }

    public void setCandidateReadDetails(Boolean candidateReadDetails) {

        this.candidateReadDetails = candidateReadDetails;

    }

    public void setCandidateReadDetailsDate(Date candidateReadDetailsDate) {

        this.candidateReadDetailsDate = candidateReadDetailsDate;

    }

    public void setCandidateRelaxed(Boolean candidateRelaxed) {

        this.candidateRelaxed = candidateRelaxed;

    }

    public void setClosedStatus(MatchClosedStatusEnum matchStatus) {

        this.closedStatus = matchStatus;

    }

    public void setDistance(Integer distance) {

        this.distance = distance;

    }

    public void setIcebreakerStatus(IcebreakerStateEnum icebreakerState) {

        this.icebreakerStatus = icebreakerState;

    }

    public void setInitializer(MatchInitializerEnum initializer) {

        this.initializer = initializer;

    }

    public void setLastNudgeDate(Date lastNudgeDate) {

        this.lastNudgeDate = lastNudgeDate;

    }

    public void setMatchClosedCount(Integer matchClosedCount) {

        this.matchClosedCount = matchClosedCount;

    }

    public void setNudgeStatus(NudgeStatusEnum nudgeStatus) {

        this.nudgeStatus = nudgeStatus;

    }

    public void setOneWayStatus(OneWayStatusEnum oneWayStatus) {

        this.oneWayStatus = oneWayStatus;

    }

    public void setStage(Integer stage) {

        this.stage = stage;

    }

    public void setTurnOwner(Long turnOwner) {

        this.turnOwner = turnOwner;

    }

    public void setUserClosedDate(Date userClosedDate) {

        this.userClosedDate = userClosedDate;

    }

    public void setUserDeliveredDate(Date userDeliveredDate) {

        this.userDeliveredDate = userDeliveredDate;

    }

    public void setUserDisplayTab(MatchDisplayTabEnum userDisplayTab) {

        this.userDisplayTab = userDisplayTab;

    }

    public void setUserFirstName(String userFirstName) {

        this.userFirstName = userFirstName;

    }

    public void setUserNewMessageCount(Integer userNewMessageCount) {

        this.userNewMessageCount = userNewMessageCount;

    }

    public void setUserReadDetails(Boolean userReadDetails) {

        this.userReadDetails = userReadDetails;

    }

    public void setUserReadDetailsDate(Date userReadDetailsDate) {

        this.userReadDetailsDate = userReadDetailsDate;

    }

    public void setUserRelaxed(Boolean userRelaxed) {

        this.userRelaxed = userRelaxed;

    }
   

    @Override public String toString() {

        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", this.getId());
        builder.append("candidateClosedDate", this.getCandidateClosedDate());
        builder.append("candidateDeliveredDate", this.getCandidateDeliveredDate());
        builder.append("candidateFirstName", this.getCandidateFirstName());
        builder.append("candidateNewMessageCount", this.getCandidateNewMessageCount());
        builder.append("candidateReadDetails", this.getCandidateReadDetails());
        builder.append("candidateReadDetailsDate", this.getCandidateReadDetailsDate());
        builder.append("candidateRelaxed", this.getCandidateRelaxed());
        builder.append("candidateUserId", this.getCandidateUserId());
        builder.append("communication", this.getCommunication());
        builder.append("distance", this.getDistance());
        builder.append("icebreakerState", this.getIcebreakerStatus());
        builder.append("initializer", this.getInitializer());
        builder.append("lastNudgeDate", this.getLastNudgeDate());
        builder.append("matchClosedCount", this.getMatchClosedCount());
        builder.append("closedStatus", this.getClosedStatus());
        builder.append("nudgeStatus", this.getNudgeStatus());
        builder.append("oneWayStatus", this.getOneWayStatus());
        builder.append("stage", this.getStage());
        builder.append("turnOwner", this.getTurnOwner());
        builder.append("userClosedDate", this.getUserClosedDate());
        builder.append("userDeliveredDate", this.getUserDeliveredDate());
        builder.append("userFirstName", this.getUserFirstName());
        builder.append("userNewMessageCount", this.getUserNewMessageCount());
        builder.append("userReadDetails", this.getUserReadDetails());
        builder.append("userReadDetailsDate", this.getUserReadDetailsDate());
        builder.append("userRelaxed", this.getUserRelaxed());
        builder.append("userId", this.getUserId());
        builder.append("userDisplayTab", userDisplayTab);
        builder.append("communication", this.getCommunication());
        builder.append("candidateDisplayTab", candidateDisplayTab);
        return builder.toString();

    }

}
