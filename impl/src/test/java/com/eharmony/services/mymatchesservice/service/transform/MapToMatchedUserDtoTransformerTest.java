package com.eharmony.services.mymatchesservice.service.transform;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.eharmony.photoclient.exceptions.PhotoSecurityException;
import com.eharmony.photoclient.security.PhotosSecurityDelegate;
import com.eharmony.services.mymatchesservice.service.SimpleMatchedUserDto;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import static com.eharmony.services.mymatchesservice.service.transform.MapToMatchedUserDtoTransformer.*;
public class MapToMatchedUserDtoTransformerTest {
	MapToMatchedUserDtoTransformer transformer = new  MapToMatchedUserDtoTransformer();
	@Test
	public void testApply() throws PhotoSecurityException {
		PhotosSecurityDelegate psDelegate = mock(PhotosSecurityDelegate.class);
		when(psDelegate.encode("100")).thenReturn("hundred");
		ReflectionTestUtils.setField(transformer, "photosSecurityDelegate", psDelegate);
		ReflectionTestUtils.setField(transformer, "encryptionCacheEnabled", false);
		
		Map<String, Map<String, Object>> matchFeedMap = new HashMap<>();
		Map<String, Object> userMap = new HashMap<>();
		Map<String, Object> matchMap = new HashMap<>();
		matchMap.put(MatchFeedModel.MATCH.ID, 60000);
		matchFeedMap.put(MATCHED_USER_KEY, userMap);
		matchFeedMap.put(MATCH_KEY, matchMap);
		matchMap.put(DELIVERED_DATE_KEY, new Date().getTime());
		
		userMap.put(USER_ID_KEY, 100l);
		userMap.put(NAME_KEY, "test");
		userMap.put(AGE_KEY, 99);
		userMap.put(PHOTO_KEY, true);
		
		transformer.initCache();
		
		SimpleMatchedUserDto result = transformer.apply(matchFeedMap);
		assertNotNull(result);
		
		assertEquals("60000", result.getMatchId());
		assertEquals("100", result.getMatchedUserId());

		assertEquals("hundred", result.getEncryptedMatchedUserId());
		
	}

}
