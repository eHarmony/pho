package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.util.Map;

import javax.annotation.Resource;

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
    public static final int DEFAULT_PRIMARY_PHOTO_HEIGHT_THUMBNAIL = 167;
    public static final int DEFAULT_PRIMARY_PHOTO_WIDTH_THUMBNAIL = 210;
    public static final int DEFAULT_PRIMARY_PHOTO_HEIGHT_ICON = 31;
    public static final int DEFAULT_PRIMARY_PHOTO_WIDTH_ICON = 40;

    @Resource
    PhotoServiceURLClient photoBuilder;
    
    @Resource
    private Configuration config;
    
    @Value(value="${photo.metadata.service.base.url}")
    private String baseUrl;

    @Value(value="${photo.url.for.no.photo.enabled}")
    private boolean urlForNoPhoto;
    
    @Override
    protected boolean processMatchSection(Map<String, Object> profile,
        MatchFeedRequestContext context) {
        // Throw away photo from feed
        @SuppressWarnings("unchecked")
        Map<String, Object> photo = (Map<String, Object>) profile.remove(MatchFeedModel.PROFILE.PHOTO);
        
        if(!context.getMatchFeedQueryContext().isAllowedSeePhotos()){
        	logger.debug("allowedSeePhotos=false, returning without enriching.");
        	return true;
        }

        // set properties, even if null
        profile.put(MatchFeedModel.PROFILE.PHOTOICON, null);
        profile.put(MatchFeedModel.PROFILE.PHOTOTHUMB, null);

        boolean hasPhoto = MapUtils.isNotEmpty(photo);

        profile.put(MatchFeedModel.PROFILE.HAS_PHOTO, hasPhoto);

        if (!hasPhoto && !urlForNoPhoto) {
            logger.debug("photo information is missing for profile={}", profile); // user might not yet have a photo

            return true;
        }
        
        return enrichContextWithPrimaryPhoto(profile, context);
    }
    
    

    private boolean enrichContextWithPrimaryPhoto(Map<String, Object> profile,
        MatchFeedRequestContext context) {
        try {
            Long userId = (Long) profile.get(MatchFeedModel.PROFILE.USERID);
 
            int genderId = getGenderIdFromProfile(profile);
            String iconUrl = photoBuilder.getPrimaryURL(baseUrl, userId.intValue(), PhotoSizeEnum.ICON, genderId);
            String thumbUrl = photoBuilder.getPrimaryURL(baseUrl, userId.intValue(), PhotoSizeEnum.MMP2, genderId);
 
            PhotoUrlDto icon = new PhotoUrlDto();
            icon.setHeight(DEFAULT_PRIMARY_PHOTO_HEIGHT_ICON);
            icon.setWidth(DEFAULT_PRIMARY_PHOTO_WIDTH_ICON);
            icon.setPhotoUrl(iconUrl);
            icon.setPhotoIndex(0);
            
            PhotoUrlDto thumb = new PhotoUrlDto();
            thumb.setHeight(DEFAULT_PRIMARY_PHOTO_HEIGHT_THUMBNAIL);
            thumb.setWidth(DEFAULT_PRIMARY_PHOTO_WIDTH_THUMBNAIL);
            thumb.setPhotoUrl(thumbUrl);
            thumb.setPhotoIndex(0);

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

}
