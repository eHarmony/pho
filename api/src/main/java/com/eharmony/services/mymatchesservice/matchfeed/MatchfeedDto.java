package com.eharmony.services.mymatchesservice.matchfeed;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MatchFeedDto {

    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(5, 17).append(name).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        MatchFeedDto rhs = (MatchFeedDto) obj;
        return new EqualsBuilder()
                .append(name, rhs.getName()).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).toString();
    }

}
