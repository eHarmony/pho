package com.eharmony.services.mymatchesservice.service.transform;

import java.util.Map;
import java.util.function.Function;

import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserDto;

public class MapToMatchedUserDtoTransformer implements Function< Map<String,Object>, SimpleMatchedUserDto> {
    static public final String NAME_KEY = "firstName";
    static public final String USER_ID_KEY = "userId";
    static public final String PHOTO_KEY = "hasPhoto";
    static public final String AGE_KEY = "age";
    

    @Override
    public SimpleMatchedUserDto apply(Map<String,Object> userMap) {
        
        SimpleMatchedUserDto userItem = new SimpleMatchedUserDto();
        userItem.setMatchUserFirstName((String) userMap .get(NAME_KEY));
        userItem.setMatchUserId(Long.toString((Long) userMap.get(USER_ID_KEY)));
        userItem.setHasPrimaryPhoto((Boolean) userMap.get(PHOTO_KEY));
        userItem.setAge((Integer) userMap.get(AGE_KEY));
        return userItem;
    }

}
