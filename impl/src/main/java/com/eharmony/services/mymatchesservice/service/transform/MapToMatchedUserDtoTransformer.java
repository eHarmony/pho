package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Map;
import java.util.function.Function;

import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserDto;

public class MapToMatchedUserDtoTransformer implements Function< Map<String,Object>, SimpleMatchedUserDto> {

	@Override
	public SimpleMatchedUserDto apply(Map<String,Object> userMap ) {
		
		SimpleMatchedUserDto userItem = new SimpleMatchedUserDto();
		userItem.setMatchUserFirstName((String) userMap .get("firstName"));
		userItem.setMatchUserId(Long.toString((Long) userMap.get("userId")));
		userItem.setHasPrimaryPhoto((Boolean) userMap.get("hasPhoto"));
		return userItem;
	}

}
