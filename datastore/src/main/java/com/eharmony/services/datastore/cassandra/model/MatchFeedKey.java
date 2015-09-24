package com.eharmony.services.datastore.cassandra.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class MatchFeedKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKeyColumn(name = "uid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private Integer userId;

	@PrimaryKeyColumn(name = "mid", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Long matchId;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(userId).append(matchId).build();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MatchFeedKey other = (MatchFeedKey) obj;
		return new EqualsBuilder().append(this.getUserId(), other.getUserId())
				.append(this.getMatchId(), other.getMatchId()).build();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("userId", userId).append("matchId", matchId).toString();
	}
}
