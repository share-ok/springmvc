package com.bk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author luna
 * @date 2019-04-24
 */
public class DateUtils {


    /**
     * 把一个字符串转换为一个日期对象
     * @param dateString
     * @return
     */
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
