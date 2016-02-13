package com.eharmony.services.mymatchesservice.rest;

import com.eharmony.datastore.model.MatchCountDto;

public class MatchCountContext {
    
    MatchCountDto matchCountDto;

	public MatchCountDto getMatchCountDto() {
		return matchCountDto;
	}

	public void setMatchCountDto(MatchCountDto matchCountDto) {
		this.matchCountDto = matchCountDto;
	}
    

}
