package com.eharmony.pho.hbase.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Preconditions;
/**
 * Utility class to convert the java.util.Date to apache phoenix date format to insert into hbase.
 * 
 * Note: this class is using simpledateformat, but should be 
 * switched to org.apache.commons.lang3.time.FastDateFormat if there are any performance issues
 * 
 * @author vvangapandu
 *
 */
public class PhoenixDateFormatUtil {

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    
    public static final String formatDate(Date date) {
        Preconditions.checkNotNull(date, "date must not be null");
        String formattedDate = new SimpleDateFormat(TIMESTAMP_FORMAT).format(date);
        StringBuffer dateFormatBuilder = new StringBuffer("TO_DATE('");
        dateFormatBuilder.append(formattedDate).append("', 'yyyy-MM-dd HH:mm:ss z')");
        return dateFormatBuilder.toString();
    }
    
}
