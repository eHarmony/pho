package com.eharmony.services.mymatchesservice.store;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * Old maps-of-maps-of-maps representation of feed list
 * from Voldemort.
 *
 * @author kmunroe
 *
 */
public class LegacyMatchDataFeedDto implements Serializable{
	
    private static final long serialVersionUID = 8756363618621578727L;
    
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

    public LegacyMatchDataFeedDto(Map<String, Map<String,  Map<String, Object>>> feedMap) {
        if (feedMap != null) {
            this.matches = feedMap;
        } else {
            this.matches = Collections.emptyMap();
        }
    }
    
    public LegacyMatchDataFeedDto(){
        this.matches = Collections.emptyMap();   	
    }

    public LegacyMatchDataFeedDto(@JsonProperty("version") Integer version,
                        @JsonProperty("createdAt") Date createdAt,
                        @JsonProperty("matches") Map<String, Map<String,  Map<String, Object>>> matches) {

        this.version = version;
        this.createdAt = createdAt;
        this.matches = matches;

    }

    public Map<String, Map<String,  Map<String, Object>>> getMatches() {
        return matches;
    }

    public Integer getTotalMatches() {
        return totalMatches;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(Date maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setMatches(Map<String, Map<String,  Map<String, Object>>> matches) {
        this.matches = matches;
    }

    public void setTotalMatches(Integer totalMatches) {
        this.totalMatches = totalMatches;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.getVersion());
        builder.append(this.getMatches());

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = obj instanceof LegacyMatchDataFeedDto;

        if (equals) {
            LegacyMatchDataFeedDto other = (LegacyMatchDataFeedDto) obj;
            EqualsBuilder builder = new EqualsBuilder();
            builder.append(this.getVersion(), other.getVersion());
            builder.append(this.getMatches(), other.getMatches());
            equals = builder.isEquals();
        }

        return equals;
    }
}
