package com.eharmony.services.mymatchesservice.matchfeed;

/*
 * This software is the confidential and proprietary information of
 * eharmony.com and may not be used, reproduced, modified, distributed,
 * publicly displayed or otherwise disclosed without the express written
 * consent of eharmony.com.
 *
 * This software is a work of authorship by eharmony.com and protected by
 * the copyright laws of the United States and foreign jurisdictions.
 *
 * Copyright 2000-2012 eharmony.com, Inc. All rights reserved.
 *
 */

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * Type representing the feed of matches that are stored in Voldy.
 *
 * @author  adenissov, cflockhart
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchFeedDto
          implements Serializable {

    private static final long serialVersionUID = 1765331951363323245L;

    @JsonProperty("auditDate")
    private Date auditDate;

    @JsonProperty("createdAt")
    private Date createdAt = new Date();

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("maintenanceDate")
    private Date maintenanceDate;

    @JsonProperty("matches")
    private Map<String, Map<String, Map<String, Object>>> matches;

    @JsonProperty("updatedAt")
    private Date updatedAt = new Date();

    @JsonProperty("version")
    private Integer version;
    
    @JsonProperty("totalMatches")
    private Integer totalMatches = 0;

    @JsonIgnore
    public Integer getTotalMatches() {
		return totalMatches;
	}

    @JsonIgnore
	public void setTotalMatches(Integer totalMatches) {
		this.totalMatches = totalMatches;
	}

	public MatchFeedDto() {

    }

    public MatchFeedDto(@JsonProperty("version") Integer version,
                        @JsonProperty("createdAt") Date createdAt,
                        @JsonProperty("matches") Map<String, Map<String, Map<String, Object>>> matches) {

        this.version = version;
        this.createdAt = createdAt;
        this.matches = matches;

    }

    @Override public boolean equals(Object obj) {

        boolean equals = obj instanceof MatchFeedDto;
        if (equals) {

            MatchFeedDto other = (MatchFeedDto) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.getVersion(), other.getVersion());
            builder.append(this.getMatches(), other.getMatches());
            equals = builder.isEquals();

        }

        return equals;

    }

    /**
     * Get last audit date
     *
     * @return  the auditDate
     */
    @JsonIgnore public Date getAuditDate() {

        return auditDate;

    }

    @JsonIgnore public Date getCreatedAt() {

        return createdAt;

    }

    /**
     * Get gender of the user owning the feed
     *
     * @return  the gender
     */
    @JsonIgnore public String getGender() {

        return gender;

    }

    /**
     * Get locale of the user owning the feed
     *
     * @return  the locale
     */
    @JsonIgnore public String getLocale() {

        return locale;

    }

    /**
     * Get last maintenance date
     *
     * @return  the maintenanceDate
     */
    @JsonIgnore public Date getMaintenanceDate() {

        return maintenanceDate;

    }

    @JsonIgnore public Map<String, Map<String, Map<String, Object>>> getMatches() {

        return matches;

    }

    @JsonIgnore public Date getUpdatedAt() {

        return updatedAt;

    }

    /**
     * Get the version of the feed.
     *
     * @return  the version
     */
    @JsonIgnore public Integer getVersion() {

        return version;

    }

    @Override public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.getVersion());
        builder.append(this.getMatches());
        return builder.toHashCode();

    }

    /**
     * Set the date of the audit
     *
     * @param  auditDate  the auditDate to set
     */
    @JsonIgnore public void setAuditDate(Date auditDate) {

        this.auditDate = auditDate;

    }

    /**
     * Set gender of the user owning the feed
     *
     * @param  gender  the gender to set
     */
    @JsonIgnore public void setGender(String gender) {

        this.gender = gender;

    }

    /**
     * Set locale of the user owning the feed
     *
     * @param  locale  the locale to set
     */
    @JsonIgnore public void setLocale(String locale) {

        this.locale = locale;

    }

    /**
     * Set the date of the maintenance
     *
     * @param  maintenanceDate  the maintenanceDate to set
     */
    @JsonIgnore public void setMaintenanceDate(Date maintenanceDate) {

        this.maintenanceDate = maintenanceDate;

    }
    
    @JsonIgnore public void setUpdatedAt(Date updatedAt) {

        this.updatedAt = updatedAt;

    }

    @JsonIgnore public void setMatches(Map<String, Map<String, Map<String, Object>>> matches) {

        this.matches = matches;

    }

    @Override public String toString() {

        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("version", this.getVersion());
        builder.append("createdAt", this.getCreatedAt());
        builder.append("updatedAt", this.getUpdatedAt());
        builder.append("matches", this.getMatches());
        return builder.toString();

    }

}

