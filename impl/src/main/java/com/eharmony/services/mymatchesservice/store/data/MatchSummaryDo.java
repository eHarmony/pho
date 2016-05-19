/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2016 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.store.data;

import com.eharmony.singles.common.data.Persistable;
import com.eharmony.singles.common.enumeration.MatchDisplayTabEnum;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

public class MatchSummaryDo
          implements Persistable<Long> {

    private static final long serialVersionUID = -7489836232838481100L;
    protected String candidateFirstName;
    protected Long candidateUserId;
    protected Date createdDate;
    protected Date deliveredDate;
    protected MatchDisplayTabEnum displayTab;
    protected Integer distance;
    protected Date lastCommDate;
    protected Long matchId;
    protected Boolean ownerIsUser;
    protected Integer stage;
    protected Long turnOwner;
    protected Date updatedDate;
    protected String userFirstName;
    protected Long userId;

    public MatchSummaryDo() {

    }

    public MatchSummaryDo(Long userId,
                              Long candidateUserId) {

        this.userId = userId;
        this.candidateUserId = candidateUserId;

    }

    @Override public boolean equals(Object obj) {

        boolean equals = obj instanceof MatchSummaryDo;
        if (equals) {

            MatchSummaryDo other = (MatchSummaryDo) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.getCandidateFirstName(), other.getCandidateFirstName());
            builder.append(this.getCreatedDate(), other.getCreatedDate());
            builder.append(this.getCandidateUserId(), other.getCandidateUserId());
            builder.append(this.getDistance(), other.getDistance());
            builder.append(this.getDeliveredDate(), other.getDeliveredDate());
            builder.append(this.getLastCommDate(), other.getLastCommDate());
            builder.append(this.getOwnerIsUser(), other.getOwnerIsUser());
            builder.append(this.getStage(), other.getStage());
            builder.append(this.getTurnOwner(), other.getTurnOwner());
            builder.append(this.getUpdatedDate(), other.getUpdatedDate());
            builder.append(this.getUserFirstName(), other.getUserFirstName());
            builder.append(this.getUserId(), other.getUserId());
            builder.append(this.getMatchId(),other.getMatchId());
            builder.append(displayTab, other.displayTab);

            equals = builder.isEquals();

        }

        return equals;

    }

    public String getCandidateFirstName() {

        return candidateFirstName;

    }

    public Long getCandidateUserId() {

        return candidateUserId;

    }

    public Date getCreatedDate() {

        return createdDate;

    }

    public Date getDeliveredDate() {

        return deliveredDate;

    }

    public MatchDisplayTabEnum getDisplayTab() {

        return displayTab;

    }

    public Integer getDistance() {

        return distance;

    }

    public Long getMatchId() {

        return matchId;

    }

    public Date getLastCommDate() {

        return lastCommDate;

    }


    public Boolean getOwnerIsUser() {

        return ownerIsUser;

    }

    public Integer getStage() {

        return stage;

    }

    public Long getTurnOwner() {

        return turnOwner;

    }

    public Date getUpdatedDate() {

        return updatedDate;

    }

    public String getUserFirstName() {

        return userFirstName;

    }

    public Long getUserId() {

        return userId;

    }

    @Override public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.getCandidateFirstName());
        builder.append(this.getCandidateUserId());
        builder.append(this.getDeliveredDate());
        builder.append(this.getDistance());
        builder.append(this.getLastCommDate());
        builder.append(this.getOwnerIsUser());
        builder.append(this.getStage());
        builder.append(this.getTurnOwner());
        builder.append(this.getUserFirstName());
        builder.append(this.getUserId());
        builder.append(this.getMatchId());
        builder.append(this.getUpdatedDate());
        builder.append(this.getCreatedDate());
        builder.append(displayTab);

        return builder.toHashCode();

    }

    public void setCandidateFirstName(String candidateFirstName) {

        this.candidateFirstName = candidateFirstName;

    }

    public void setCreatedDate(Date createdDate) {

        this.createdDate = createdDate;

    }

    public void setDeliveredDate(Date deliveredDate) {

        this.deliveredDate = deliveredDate;

    }

    public void setDisplayTab(MatchDisplayTabEnum displayTab) {

        this.displayTab = displayTab;

    }

    public void setDistance(Integer distance) {

        this.distance = distance;

    }

    public void setLastCommDate(Date lastCommDate) {

        this.lastCommDate = lastCommDate;

    }



    public void setOwnerIsUser(Boolean ownerIsUser) {

        this.ownerIsUser = ownerIsUser;

    }

    public void setStage(Integer stage) {

        this.stage = stage;

    }

    public void setTurnOwner(Long turnOwner) {

        this.turnOwner = turnOwner;

    }

    public void setUpdatedDate(Date updatedDate) {

        this.updatedDate = updatedDate;

    }

    public void setUserFirstName(String userFirstName) {

        this.userFirstName = userFirstName;

    }

    public void setMatchId(Long matchId) {

        this.matchId = matchId;

    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCandidateUserId(Long candidateUserId) {
        this.candidateUserId = candidateUserId;
    }

    @Override public String toString() {

        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("candidateFirstName", this.getCandidateFirstName());
        builder.append("candidateUserId", this.getCandidateUserId());
        builder.append("createdDate", this.getCreatedDate());
        builder.append("distance", this.getDistance());
        builder.append("deliveredDate", this.getDeliveredDate());
        builder.append("lastCommDate", this.getLastCommDate());
        builder.append("ownerIsUser", this.getOwnerIsUser());
        builder.append("stage", this.getStage());
        builder.append("turnOwner", this.getTurnOwner());
        builder.append("updatedDate", this.getUpdatedDate());
        builder.append("userFirstName", this.getUserFirstName());
        builder.append("userId", this.getUserId());
        builder.append("matchId",this.getMatchId());
        builder.append("displayTab", displayTab);
        return builder.toString();

    }

}
