package com.aihui.lib.base.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间处理工具
 * Created by Nereo on 2015/4/8.
 */
public final class TimeUtils {

    // 2018-07-13T16:21:12.079
    public final static SimpleDateFormat sdf0 = getSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    // 2018-07-13T16:21:12
    public final static SimpleDateFormat sdf1 = getSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    // 2018-07-13 16:21:12
    public final static SimpleDateFormat sdf2 = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 16:21
    public final static SimpleDateFormat sdf3 = getSimpleDateFormat("HH:mm");
    // 21:12.79
    public final static SimpleDateFormat sdf4 = getSimpleDateFormat("mm:ss:SS");
    // 16:21:12
    public final static SimpleDateFormat sdf5 = getSimpleDateFormat("HH:mm:ss");
    // 2018-07-13
    public final static SimpleDateFormat sdf6 = getSimpleDateFormat("yyyy-MM-dd");
    // 2018-07-1312:00:00
    public final static SimpleDateFormat sdf7 = getSimpleDateFormat("yyyy-MM-ddHH:mm:ss");
    // 2018/07/13
    public final static SimpleDateFormat sdf8 = getSimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // 1316:21:12
    public final static SimpleDateFormat sdf9 = getSimpleDateFormat("ddHH:mm:ss");
    // 16:21:12
    public final static SimpleDateFormat sdf10 = getSimpleDateFormat("HH:mm:ss");
    // 20180713
    public final static SimpleDateFormat sdf11 = getSimpleDateFormat("yyyyMMdd");

    @NonNull
    private static SimpleDateFormat getSimpleDateFormat(String s) {
        return new SimpleDateFormat(s, Locale.CHINA);
    }

    static {
        sdf10.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static long getTime(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return 0;
        }
        try {
            return sdf0.parse(timeStr).getTime();
        } catch (ParseException e) {
            try {
                return sdf1.parse(timeStr).getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }

    public static String formatDate(String timeStr) {
        long time = getTime(timeStr);
        return time != 0 ? sdf2.format(time) : "0";
    }

    public static String getNowTimeString() {
        return sdf8.format(System.currentTimeMillis());
    }

    public static Calendar getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static String formatMonthCalendar() {
        return formatMonthCalendar(null);
    }

    public static String formatMonthCalendar(Calendar calendar) {
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        return sdf6.format(calendar.getTime());
    }

    public static String getDayOfWeek(int day, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if (offset < 0) {
            calendar.add(Calendar.DATE, -7);
        } else if (offset > 0) {
            calendar.add(Calendar.DATE, 7);
        }
        calendar.set(Calendar.DAY_OF_WEEK, day);
        return sdf6.format(calendar.getTime());
    }
}
