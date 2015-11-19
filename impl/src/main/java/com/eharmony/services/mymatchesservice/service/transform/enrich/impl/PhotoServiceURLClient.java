package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.photoclient.enumeration.PhotoSizeEnum;
import com.eharmony.photoclient.exceptions.PhotoSecurityException;
import com.eharmony.photoclient.security.PhotosSecurityDelegate;

/**
 * @author kmunroe
 */
public class PhotoServiceURLClient{

    private static final Logger log = LoggerFactory.getLogger(PhotoServiceURLClient.class);

    private static final String UTF8 = "UTF8";
    
    public static final int DEFAULT_EXPIRATION_IS_NOT_SET = 0;
    public static final int DEFAULT_ICON_EXPIRATION_PERIOD = 10;

    PhotosSecurityDelegate securityDelegate;

    public PhotoServiceURLClient(PhotosSecurityDelegate photosSecurityDelegate) {
        this.securityDelegate = photosSecurityDelegate;
    }

    public String getPrimaryURL(String baseURL, int userId, PhotoSizeEnum photoSize, Integer genderId) {
        UriBuilder builder = UriBuilder.fromUri(baseURL).path("/photos/v1/photo/{size}/P/{encrypted}.jpg");
        String encryptedUserId = encryptAndEncode(userId);        
        addGenderQueryParam(genderId, builder);
        return builder.buildFromEncoded(photoSize.name(),encryptedUserId).toString();
    }
    

    String encryptAndEncode(int userId) {
        String encryptedUserId = null;
        try {
            encryptedUserId = securityDelegate.encode(Integer.toString(userId));
        } catch (PhotoSecurityException e) {
            log.error("exception getting primary URL", e);
            return null;
        }
        try {
            encryptedUserId = URLEncoder.encode(encryptedUserId, UTF8);
        } catch (UnsupportedEncodingException uee) {
            log.warn("could not url encode  {" + encryptedUserId + "}", uee);
        }
        return encryptedUserId;
    }

    void addGenderQueryParam(Integer genderId, UriBuilder builder) {
        if (genderId != null && (genderId.intValue() == 1 || genderId.intValue() == 2)) {
            builder.queryParam("g", genderId.toString());
        }
    }

}
