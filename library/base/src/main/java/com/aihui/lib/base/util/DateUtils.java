package com.aihui.lib.base.util;

import java.util.Date;

/**
 * Created by aihui087 on 2017/8/2.
 */

public final class DateUtils {


    /**
     * 20点 - 7点为夜间模式，其它时间为日间模式
     *
     * @return true 日间模式， false 夜间模式
     */
    public static boolean isDay() {
        String time = TimeUtils.sdf3.format(new Date());
        int hour = Integer.parseInt(time.split(":")[0]);
        return hour < 20 && hour > 7;
    }
}
