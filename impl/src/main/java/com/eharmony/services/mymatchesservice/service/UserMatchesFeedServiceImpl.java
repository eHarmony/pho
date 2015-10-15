package com.eharmony.services.mymatchesservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserMatchesFeedServiceImpl implements UserMatchesFeedService {

    @Override
    public List<Object> getUserMatches(Integer userId) {
        // TODO Auto-generated method stub
        return new ArrayList<Object>();
    }

    /*@Override
    public List<MatchDataFeedItemDto> getUserMatches(Integer userId) {
        // TODO Auto-generated method stub
        return new ArrayList<MatchDataFeedItemDto>();
    }

    @Override
    public MatchDataFeedItemDto getUserMatch(Integer userId, Long matchId) {
        // TODO Auto-generated method stub
        return new MatchDataFeedItemDto();
    }*/

	

}
