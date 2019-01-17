package com.aihui.lib.base.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;

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
    // 2018-07-1316:21:12
    public final static SimpleDateFormat sdf7 = getSimpleDateFormat("yyyy-MM-ddHH:mm:ss");
    // 2018/07/13
    public final static SimpleDateFormat sdf8 = getSimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    // 1316:21:12
    public final static SimpleDateFormat sdf9 = getSimpleDateFormat("ddHH:mm:ss");
    // 16:21:12
    public final static SimpleDateFormat sdf10 = getSimpleDateFormat("HH:mm:ss");
    // 20180713
    public final static SimpleDateFormat sdf11 = getSimpleDateFormat("yyyyMMdd");
    // 2018.07.13
    public final static SimpleDateFormat sdf12 = getSimpleDateFormat("yyyy.MM.dd");
    // 20180713162112
    public final static SimpleDateFormat sdf13 = getSimpleDateFormat("yyyyMMddHHmmss");
    // 0713
    public final static SimpleDateFormat sdf14 = getSimpleDateFormat("MMdd");
    // 2018年07月13日 16:21 星期五
    public final static SimpleDateFormat sdf15 = getSimpleDateFormat("yyyy年MM月dd日 HH:mm EEEE");
    // 21:12
    public final static SimpleDateFormat sdf16 = getSimpleDateFormat("mm:ss");
    //201812
    public final static SimpleDateFormat sdf17 = getSimpleDateFormat("yyyyMM");
    // 2018-07-13 16:21
    public final static SimpleDateFormat sdf18 = getSimpleDateFormat("yyyy-MM-dd HH:mm");
    // 2018年07月13日
    public final static SimpleDateFormat sdf19 = getSimpleDateFormat("yyyy年MM月dd日");
	// 07月13日
    public final static SimpleDateFormat sdf20 = getSimpleDateFormat("MM月dd日");

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

    public static long getYdcfTime(String timeString) {
        if (TextUtils.isEmpty(timeString)) return 0;
        try {
            return sdf13.parse(timeString).getTime();
        } catch (ParseException e) {
            try {
                return sdf11.parse(timeString).getTime();
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

    public static String formatDateByReplace(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return timeStr;
        }
        return timeStr.replace('T', ' ');
    }

    public static String formatYdcfDate(String timeStr) {
        long time = getTime(timeStr);
        return time != 0 ? sdf14.format(time) : "0";
    }

    public static String getYdcfDate(String timeStr) {
        long time = getTime(timeStr);
        return time != 0 ? sdf11.format(time) : "0";
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

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2)
    {
        return (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
    }
}
