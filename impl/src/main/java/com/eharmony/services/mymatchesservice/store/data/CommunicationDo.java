/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2011 eharmony.com, Inc. All rights reserved.
 *
 */
package com.eharmony.services.mymatchesservice.store.data;

import com.eharmony.singles.common.data.Persistable;
import com.eharmony.singles.common.enumeration.FastTrackAvailableEnum;
import com.eharmony.singles.common.enumeration.FastTrackStatusEnum;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;


/**
 * Read-only persistent object representing a record in
 * singles_owner.ehcommunication.  Despite there being setters for bean
 * reflection, a hibernate exception will be thrown if this object is attempted
 * to be saved.  This data object is relationally accessed via a MatchDo
 * instance.
 *
 * @author  jtafralian
 */
public abstract class CommunicationDo
          implements Persistable<Long> {

    private static final long serialVersionUID = -9119298799884990976L;
    protected Date candidateChooseMhcs;
    protected Date commLastSentByCandidate;
    protected Date commLastSentByUser;
    protected Date commStartedDate;
    protected FastTrackAvailableEnum fastTrackAvailable;
    protected Double fastTrackStage;
    protected FastTrackStatusEnum fastTrackStatus;
    protected Long id;
    protected Long matchId;

    protected Date userChooseMhcs;
    
    public CommunicationDo(){}
    public CommunicationDo(Long matchId) {this.matchId = matchId;}

    @Override public boolean equals(Object obj) {

        boolean equals = obj instanceof CommunicationDo;
        if (equals) {

            CommunicationDo other = (CommunicationDo) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.candidateChooseMhcs, other.candidateChooseMhcs)
                   .append(this.commLastSentByCandidate, other.commLastSentByCandidate)
                   .append(this.commLastSentByUser, other.commLastSentByUser)
                   .append(this.commStartedDate, other.commStartedDate)
                   .append(this.fastTrackAvailable, other.fastTrackAvailable)
                   .append(this.fastTrackStage, other.fastTrackStage)
                   .append(this.fastTrackStatus, other.fastTrackStatus)
                   .append(this.id, other.id)
                   .append(this.matchId, other.matchId)
                   .append(this.userChooseMhcs, other.userChooseMhcs);
            equals = builder.isEquals();

        }

        return equals;

    }

    public Date getCandidateChooseMhcs() {

        return candidateChooseMhcs;

    }

    public Date getCommLastSentByCandidate() {

        return commLastSentByCandidate;

    }

    public Date getCommLastSentByUser() {

        return commLastSentByUser;

    }

    public Date getCommStartedDate() {

        return commStartedDate;

    }

    public FastTrackAvailableEnum getFastTrackAvailable() {

        return fastTrackAvailable;

    }

    public Double getFastTrackStage() {

        return fastTrackStage;

    }

    public FastTrackStatusEnum getFastTrackStatus() {

        return fastTrackStatus;

    }

    public Long getId() {

        return id;

    }

    public Long getMatchId() {

        return matchId;

    }

    public Date getUserChooseMhcs() {

        return userChooseMhcs;

    }

    @Override public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.getCommLastSentByCandidate());
        builder.append(this.getCommLastSentByUser());
        builder.append(this.getFastTrackStatus());
        builder.append(this.getId());
        builder.append(fastTrackAvailable);
        builder.append(fastTrackStage);
        builder.append(commStartedDate);
        builder.append(userChooseMhcs);
        builder.append(candidateChooseMhcs);
        builder.append(matchId);
        return builder.toHashCode();

    }

    public void setCandidateChooseMhcs(Date candidateChooseMhcs) {

        this.candidateChooseMhcs = candidateChooseMhcs;

    }

    public void setCommLastSentByCandidate(Date commLastSentByCandidate) {

        this.commLastSentByCandidate = commLastSentByCandidate;

    }

    public void setCommLastSentByUser(Date commLastSentByUser) {

        this.commLastSentByUser = commLastSentByUser;

    }

    public void setCommStartedDate(Date commStartedDate) {

        this.commStartedDate = commStartedDate;

    }

    public void setFastTrackAvailable(FastTrackAvailableEnum fastTrackAvailable) {

        this.fastTrackAvailable = fastTrackAvailable;

    }

    public void setFastTrackStage(Double fastTrackStage) {

        this.fastTrackStage = fastTrackStage;

    }

    public void setFastTrackStatus(FastTrackStatusEnum fastTrackStatus) {

        this.fastTrackStatus = fastTrackStatus;

    }

    public void setUserChooseMhcs(Date userChooseMhcs) {

        this.userChooseMhcs = userChooseMhcs;

    }

    @Override public String toString() {

        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("commLastSentByCandidate", this.getCommLastSentByCandidate());
        builder.append("commLastSentByUser", this.getCommLastSentByUser());
        builder.append("fastTrackStatus", this.getFastTrackStatus());
        builder.append("id", this.getId());
        builder.append("matchId", this.matchId);
        builder.append("fastTrackAvailable", fastTrackAvailable);
        builder.append("fastTrackStage", fastTrackStage);
        builder.append("commStarted", commStartedDate);
        builder.append("userChooseMhcs", userChooseMhcs);
        builder.append("candidateChooseMhcs", candidateChooseMhcs);
        return builder.toString();

    }

}
