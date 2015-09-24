package com.eharmony.services.mymatchesservice.datastore.mongodb;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eharmony.services.datastore.mongodb.MatchFeedRepository;
import com.eharmony.services.datastore.mongodb.model.MatchCommunication;
import com.eharmony.services.datastore.mongodb.model.MatchFeedItem;
import com.eharmony.services.datastore.mongodb.model.MatchProfile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mymatchesservice/application-context.xml" })
public class MatchFeedRepositoryTest {

	private static final Logger log = LoggerFactory.getLogger(MatchFeedRepositoryTest.class);
	@Autowired MatchFeedRepository repository;

    @Test
    public void readsFirstPageCorrectly() {

    	MatchFeedItem feedItem = null;
      try {
    	  feedItem = repository.save(buildItem());
      }catch(Exception ex) {
    	  log.error("blah", ex);
      }
      Assert.assertNotNull(feedItem);
      Iterable<MatchFeedItem> feed = repository.findAll();
      //Iterable<MatchFeedItem> feed = repository.findAll(new PageRequest(0, 10));
      Assert.assertNotNull(feed);
      
    }
    
    private MatchFeedItem buildItem() {
    	MatchFeedItem feedItem = new MatchFeedItem();
    	feedItem.setMatchId(15l);
    	feedItem.setUserId(1l);
    	feedItem.setMatchUserId(5l);
    	feedItem.setDeliveredDate(DateUtils.addDays(new Date(), -3));
    	
    	feedItem.setArchivedStatus(0);
    	feedItem.setClosedStatus(0);
    	feedItem.setDistance(20);
    	feedItem.setIsUser(true);
    	feedItem.setMatchState(0);
    	feedItem.setOneWayStatus(0);
    	
    	feedItem.setMatchProfile(buildMatchProfile());
    	feedItem.setCommunication(buildMatchCommunication());
    	return feedItem;
    	
    }
    
    private MatchProfile buildMatchProfile() {
    	MatchProfile profile = new MatchProfile();
    	profile.setBirthDate(DateUtils.addYears(new Date(), 20));
    	profile.setFirstName("Ram");
    	profile.setCity("Los Angeles");
    	profile.setCountryId(1);
    	profile.setGenderId(1);
    	profile.setHasPhoto(true);
    	profile.setState("CA");
    	return profile;
    	
    }
    
    private MatchCommunication buildMatchCommunication() {
    	MatchCommunication comm = new MatchCommunication();
    	//comm.setChooseMHCSDate(DateUtils.addDays(new Date(), -2));
    	//comm.setCommStartedDate(DateUtils.addDays(new Date(), -2)););
    	return comm;
    }
}
