package com.eharmony.services.mymatchesservice.service.transform.enrich.impl;

import static com.eharmony.photoclient.file.FilePartitionStrategy.DIR_SEP;
import static com.eharmony.photoclient.request.UserPhotoRequest.PARAM_RELATIVE_PHOTO_PATH;
import static com.eharmony.photoclient.request.UserPhotoRequest.PARAM_REQUEST;
import static com.eharmony.photoclient.request.UserPhotoRequest.PARAM_USERID;
import static com.eharmony.photoclient.request.UserPhotoRequest.PARAM_USER_STATUS;
import static com.eharmony.photoclient.request.UserPhotoRequest.REQUEST_VIEW;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eharmony.photoclient.enumeration.PhotoSizeEnum;
import com.eharmony.photoclient.exceptions.PhotoSecurityException;
import com.eharmony.photoclient.security.PhotosSecurityDelegate;
import com.eharmony.photos.PhotosService;

/**
 * @author cflockhart
 *         Date: 11/15/12
 *         Time: 11:42 AM
 */
public class PhotoServiceURLClient{

    private static final Logger log = LoggerFactory.getLogger(PhotoServiceURLClient.class);

    private static final char URL_SEP = '/';
    private static final String UTF8 = "UTF8";
    
    private static final String QUERY_PARAM_SEPARATOR = "&";

    public static final int DEFAULT_EXPIRATION_IS_NOT_SET = 0;
    public static final int DEFAULT_ICON_EXPIRATION_PERIOD = 10;

    PhotosSecurityDelegate securityDelegate;

    public PhotoServiceURLClient(PhotosSecurityDelegate photosSecurityDelegate) {
        this.securityDelegate = photosSecurityDelegate;
    }


    public String getViewURL(URL servletUrl, int userId, String relativeUrl) {
        if (StringUtils.isEmpty(relativeUrl)) {

            throw new IllegalArgumentException("relativeUrl parameter cannot be null");

        }

        int expireInField = Calendar.HOUR;
        int expireInValue = DEFAULT_EXPIRATION_IS_NOT_SET;

        boolean isIconRequest =
                relativeUrl.contains(securityDelegate.getPhotoPropertyConfig().getSubDirectory(PhotoSizeEnum.ICON)
                        .getName());

        if (isIconRequest) {
            expireInField = Calendar.YEAR;
            expireInValue = DEFAULT_ICON_EXPIRATION_PERIOD;
        }

        return getViewURL(servletUrl, userId, relativeUrl, expireInField, expireInValue);

    }
    
    public String getViewURL(URL servletUrl, int userId, String relativeUrl, boolean primary, int genderId) {
        if (StringUtils.isEmpty(relativeUrl)) {

            throw new IllegalArgumentException("relativeUrl parameter cannot be null");

        }

        int expireInField = Calendar.HOUR;
        int expireInValue = DEFAULT_EXPIRATION_IS_NOT_SET;

        boolean isIconRequest =
                relativeUrl.contains(securityDelegate.getPhotoPropertyConfig().getSubDirectory(PhotoSizeEnum.ICON)
                        .getName());

        if (isIconRequest) {
            expireInField = Calendar.YEAR;
            expireInValue = DEFAULT_ICON_EXPIRATION_PERIOD;
        }

        return getViewURL(servletUrl, userId, relativeUrl, expireInField, expireInValue, primary, genderId);

    }
    

    public String getViewURL(URL servletUrl,
                             int userId,
                             String relativeUrl,
                             int expiresInField,
                             int expiresInValue) {
        StringBuilder photoViewURL = new StringBuilder();

        String urlEncoded = getRelativeEncryptedURL(userId, relativeUrl, expiresInField, expiresInValue);
        // construct the full URL
        photoViewURL.append(servletUrl)
                    .append("?")
                    .append(PhotosSecurityDelegate.ENCRYPTED_TOKEN_REQUEST_PARAMETER)
                    .append("=")
                    .append(urlEncoded);

        log.debug("got photo view URL {} for relativeURL {}", photoViewURL.toString(), relativeUrl);

        return photoViewURL.toString();

    }
    
	public String getViewURL(URL servletUrl, int userId, String relativeUrl,
			int expiresInField, int expiresInValue, boolean primary, int genderId) {
		
		StringBuilder photoViewURL = new StringBuilder();

		String urlEncoded = getRelativeEncryptedURL(userId, relativeUrl,
				expiresInField, expiresInValue);
		// construct the full URL
		photoViewURL.append(servletUrl).append("?");

		photoViewURL
			.append(PhotosSecurityDelegate.ENCRYPTED_PRIMARY_TOKEN_REQUEST_PARAMETER)
			.append("=").append(primary)
			.append(QUERY_PARAM_SEPARATOR);
		photoViewURL
			.append(PhotosSecurityDelegate.ENCRYPTED_GENDER_ID_REQUEST_PARAMETER)
			.append("=").append(String.valueOf(genderId))
			.append(QUERY_PARAM_SEPARATOR);
		
		photoViewURL
			.append(PhotosSecurityDelegate.ENCRYPTED_TOKEN_REQUEST_PARAMETER)
			.append("=").append(urlEncoded);

		log.debug("got photo view URL {} for relativeURL {}",
				photoViewURL.toString(), relativeUrl);

		return photoViewURL.toString();

	}

    private String getRelativeEncryptedURL(int userId,
                                          String relativeUrl,
                                          int expiresInField,
                                          int expiresInValue) {

        if (StringUtils.isEmpty(relativeUrl)) {

            throw new IllegalArgumentException("relativeUrl parameter cannot be null");

        }

        relativeUrl = convertToInternetForm(relativeUrl);

        log.debug("getting converted photo view URL [{}] for user [{}]", relativeUrl, userId);

        Properties args = new Properties();
        args.put(PARAM_REQUEST, REQUEST_VIEW);
        args.put(PARAM_RELATIVE_PHOTO_PATH, relativeUrl);
        args.put(PARAM_USERID, String.valueOf(userId));
        args.put(PARAM_USER_STATUS, "0");

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(expiresInField, expiresInValue);
        args.put(PhotosSecurityDelegate.TIMESTAMP_REQUEST_PARAMETER,
                String.valueOf(expirationDate.getTime().getTime()));

        String urlEncoded = null;
        try {

            if (log.isDebugEnabled()) {
                log.debug("User's [{}] photo [{}] request time [{}], current time [{}]",
                        new Object[]{args.get(PARAM_USERID),
                                args.get(PARAM_RELATIVE_PHOTO_PATH),
                                DateFormatUtils.format(Long.valueOf((String) args.get(PhotosSecurityDelegate.TIMESTAMP_REQUEST_PARAMETER)), "yyyy-MM-dd HH:mm"),
                                DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm")});
            }

            // encrypt all request parameters
            String encryptedVal = securityDelegate.encodeProperties(args);

            log.debug("URLEncoding encrypted request parameters [{}]", encryptedVal);

            try {

                urlEncoded = URLEncoder.encode(encryptedVal, UTF8);

            } catch (UnsupportedEncodingException uee) {

                urlEncoded = encryptedVal;
                log.warn("could not url encode  {" + encryptedVal + "}", uee);

            }

        } catch (PhotoSecurityException pse) {

            log.error("exception getting photo view URL", pse);

        }

        return urlEncoded;
    }

    /**
     * Converts the given URLs 'DIR_SEP' characters  to proper URL separators:
     * '/'
     */
    private String convertToInternetForm(String url) {

        return url.replace(DIR_SEP.charAt(0), URL_SEP);

    }

    public String getPrimaryURL(String baseURL, int userId, PhotoSizeEnum photoSize, Integer genderId) {
        UriBuilder builder = UriBuilder.fromUri(baseURL).path("/photos/v1/photo/{size}/P/{encrypted}.jpg");
        String encryptedUserId = encryptAndEncode(userId);        
        addGenderQueryParam(genderId, builder);
        return builder.buildFromEncoded(photoSize.name(),encryptedUserId).toString();
    }
    
    public String getIndexURL(String baseURL, int userId, int index, PhotoSizeEnum photoSize, Integer genderId) {
        UriBuilder builder = UriBuilder.fromUri(baseURL).path("/photos/v1/photo/{size}/I{index}/{encrypted}.jpg");
        String encryptedUserId = encryptAndEncode(userId);        
        
        addGenderQueryParam(genderId, builder);
        return builder.buildFromEncoded(photoSize.name(),Integer.toString(index),encryptedUserId).toString();
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
