package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.eharmony.configuration.Configuration;
import com.eharmony.photoclient.enumeration.PhotoSizeEnum;
import com.eharmony.services.mymatchesservice.rest.MatchFeedRequestContext;
import com.eharmony.services.mymatchesservice.service.transform.MatchFeedModel;
import com.eharmony.services.mymatchesservice.service.transform.enrich.AbstractProfileEnricher;
import com.eharmony.singles.common.photo.PhotoUrlDto;


public class PhotoUrlEnricher extends AbstractProfileEnricher {
    private static final Logger logger = LoggerFactory.getLogger(PhotoUrlEnricher.class);

    // INFO: these are fixed values for my matches page
    public static final int DEFAULT_PRIMARY_PHOTO_HEIGHT = 167;
    public static final int DEFAULT_PRIMARY_PHOTO_WIDTH = 210;
    private static final String PHOTO_URL_FOR_NO_PHOTO_ENABLED = "photo.url.for.no.photo.enabled";
    @Resource
    PhotoServiceURLClient photoBuilder;
    
    @Resource
    private Configuration config;
    
    @Value(value="${photo.metadata.service.base.url}")
    private String baseUrl;

    @Override
    protected boolean processMatchSection(Map<String, Object> profile,
        MatchFeedRequestContext context) {
        // Throw away photo from feed
        @SuppressWarnings("unchecked")
        Map<String, Object> photo = (Map<String, Object>) profile.remove(MatchFeedModel.PROFILE.PHOTO);

        // set properties, even if null
        profile.put(MatchFeedModel.PROFILE.PHOTOICON, null);
        profile.put(MatchFeedModel.PROFILE.PHOTOTHUMB, null);

        boolean hasPhoto = MapUtils.isNotEmpty(photo);
        boolean urlForNoPhoto = isPhotoUrlForNoPhotoEnabled();

        profile.put(MatchFeedModel.PROFILE.HAS_PHOTO, hasPhoto);

        if (!hasPhoto && !urlForNoPhoto) {
            logger.debug("photo information is missing for profile={}", profile); // user might not yet have a photo

            return true;
        }
        
        UriBuilder builder = UriBuilder.fromUri("/test");

        return enrichContextWithPrimaryPhoto(profile, context);
    }
    
    

    private boolean enrichContextWithPrimaryPhoto(Map<String, Object> profile,
        MatchFeedRequestContext context) {
        try {
            int userId = (Integer) profile.get(MatchFeedModel.PROFILE.USERID);
            int height = DEFAULT_PRIMARY_PHOTO_HEIGHT;
            int width = DEFAULT_PRIMARY_PHOTO_WIDTH;

            int genderId = getGenderIdFromProfile(profile);
            String iconUrl = photoBuilder.getPrimaryURL(baseUrl, userId, PhotoSizeEnum.ICON, genderId);
            String thumbUrl = photoBuilder.getPrimaryURL(baseUrl, userId, PhotoSizeEnum.MMP2, genderId);
 
            PhotoUrlDto icon = new PhotoUrlDto();
            icon.setHeight(height);
            icon.setWidth(width);
            icon.setPhotoUrl(iconUrl);
            icon.setPhotoIndex(0);
            
            PhotoUrlDto thumb = new PhotoUrlDto();
            thumb.setHeight(height);
            thumb.setWidth(width);
            thumb.setPhotoUrl(thumbUrl);
            thumb.setPhotoIndex(1);

            profile.put(MatchFeedModel.PROFILE.PHOTOICON, icon);
            profile.put(MatchFeedModel.PROFILE.PHOTOTHUMB, thumb);

            return true;
        } catch (Exception e) {
            logger.warn("Failed to enrich photo information for userId=" +
                context.getUserId() + ", profile=" + profile, e);

            return false;
        }

     }

    private int getGenderIdFromProfile(Map<String, Object> profile) {
        int genderId = 0;

        try {
            genderId = (Integer) profile.get(MatchFeedModel.PROFILE.GENDER);
        } catch (Exception ex) {
            logger.warn("Exception while getting the gender", ex);
        }

        return genderId;
    }

    public boolean isPhotoUrlForNoPhotoEnabled() {
        return config.getPropertyBoolean(PHOTO_URL_FOR_NO_PHOTO_ENABLED, false);
    }
}
