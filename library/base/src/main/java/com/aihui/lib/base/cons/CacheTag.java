package com.aihui.lib.base.cons;

/**
 * Created by 胡一鸣 on 2018/8/6.
 */
public final class CacheTag {

    public static final int USER_GLOBAL = 0;

    /** 所在院区 */
    public static final String HOSPITAL = "hospital";
    /** token */
    public static final String TOKEN = "token";
    /** 登录账号 */
    public static final String LOGIN = "login";

    // region 天护cache tag
    /** 首页菜单 */
    public static final String MENU = "menu";
    /** 床位列表 */
    public static final String BED_NUM = "bed_num";
    /** 效果评估 */
    public static final String MISSION = "mission";
    /** 定时事件 */
    public static final String TIME = "time";
    /** 护士审核 */
    public static final String AUDIT = "audit";
    /** 护士排班 */
    public static final String SCHEDULE = "schedule";
    /** 药典维护 */
    public static final String DRUG = "drug";
    /** 暖屏音量 */
    public static final String VOICE = "voice";
    /** 暖屏使用 */
    public static final String DEVICE_USE = "device_use";

    /** 数据分析:工作量 */
    public static final String STATISTIC_WORK = "statistic_work";
    /** 数据分析:满意度表 */
    public static final String STATISTIC_TABLE = "statistic_table";
    /** 数据分析:满意度明细 */
    public static final String STATISTIC_DETAIL = "statistic_detail";
    /** 数据分析:阅读分析 */
    public static final String STATISTIC_READ = "statistic_read";
    /** 数据分析:异常评价 */
    public static final String STATISTIC_QUESTION = "statistic_question";

    public static final Object[][] CACHE_TAG_BY_CREATOR = new Object[][]{
//            {CacheTag.LOGIN, HospitalUserBean.CREATOR},
//            {CacheTag.MENU, CacheMenuWrapper.CREATOR},
//            {CacheTag.BED_NUM, CacheBedWrapper.CREATOR},
//            {CacheTag.MISSION, CacheMissionWrapper.CREATOR},
//            {CacheTag.TIME, CacheTimeEventWrapper.CREATOR},
//            {CacheTag.AUDIT, CacheUserAuditWrapper.CREATOR},
//            {CacheTag.SCHEDULE, CacheScheduleWrapper.CREATOR},
//            {CacheTag.DRUG, CacheDrugWrapper.CREATOR},
//            {CacheTag.VOICE, CacheVoiceWrapper.CREATOR},
//            {CacheTag.DEVICE_USE, CacheDeviceUserWrapper.CREATOR},
//            {CacheTag.STATISTIC_WORK, CacheStatisticWorkWrapper.CREATOR},
//            {CacheTag.STATISTIC_TABLE, CacheStatisticTableWrapper.CREATOR},
//            {CacheTag.STATISTIC_DETAIL, CacheStatisticDetailWrapper.CREATOR},
//            {CacheTag.STATISTIC_READ, CacheStatisticReadWrapper.CREATOR},
//            {CacheTag.STATISTIC_QUESTION, CacheStatisticQuestionWrapper.CREATOR},
    };
}
