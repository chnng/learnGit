package com.aihui.lib.base.api.eventbus;

public class EventTag {
    /**
     * eventBus 通知关闭扫码页面
     */
    public static final int CLOSE_SCAN_ACTIVITY = 0x5;
    /**
     * eventBus  更新进度条
     */
    public static final int UPDATE_PROGRESS_BAR = CLOSE_SCAN_ACTIVITY + 1;
    /**
     * eventBus  关闭视频通话页面，Android 端方法供JS 端调用
     */
    public static final int CLOSE_VIDEO_CHAT_ACTIVITY = UPDATE_PROGRESS_BAR + 1;
    /**
     * eventBus  视频通话结束，JS  端方法供Android 端调用
     */
    public static final int EXIT_VIDEO_CHAT = CLOSE_VIDEO_CHAT_ACTIVITY + 1;

    /**
     * eventBus  切换到视频通话小窗口
     */
    public static final int SHRINK_VIDEO_CHAT = EXIT_VIDEO_CHAT + 1;

    /**
     * eventBus 大窗口显示远程视频
     */
    public static final int BIG_WINDOW_REMOTE_VIDEO_SHOW = SHRINK_VIDEO_CHAT + 1;

    /**
     * eventBus 小窗口视频通话结束
     */
    public static final int SMALL_VIDEO_EXIT = BIG_WINDOW_REMOTE_VIDEO_SHOW + 1;

    /**
     * eventBus 大窗口视频通话结束
     */
    public static final int BIG_VIDEO_EXIT = SMALL_VIDEO_EXIT + 1;

    /**
     * eventBus 键盘隐藏
     */
    public static final int KEYBOARD_HIDDEN = BIG_VIDEO_EXIT + 1;

    /**
     * 登录跳转注册
     */
    public static final int LOGIN_SHOW_RESISTER = KEYBOARD_HIDDEN + 1;

    /**
     * 关闭巡房
     */
    public static final int EVENT_PATROL_CLOSE = LOGIN_SHOW_RESISTER + 1;

    /**
     * 关闭远程会诊视频
     */
    public static final int EVENT_MEETING_VIDEO_CLOSE = EVENT_PATROL_CLOSE + 1;

    /**
     * 远程会诊视频已连接
     */
    public static final int EVENT_MEETING_VIDEO_CONNECTED = EVENT_MEETING_VIDEO_CLOSE + 1;

    /**
     * 下载apk进度
     */
    public static final int EVENT_UPDATE_DOWNLOAD_PROGRESS = EVENT_MEETING_VIDEO_CONNECTED + 1;

    /**
     * 下载apk结果
     */
    public static final int EVENT_UPDATE_DOWNLOAD_RESULT = EVENT_UPDATE_DOWNLOAD_PROGRESS + 1;

    /**
     * 跳转首页模块
     */
    public static final int EVENT_START_FUNCTION = EVENT_UPDATE_DOWNLOAD_RESULT + 1;

    /**
     * 看板首页定时器
     */
    public static final int EVENT_MN_UPDATE_DATA = EVENT_START_FUNCTION + 1;

    /**
     * 看板更新时间
     */
    public static final int EVENT_MN_UPDATE_DATE = EVENT_MN_UPDATE_DATA + 1;

    /**
     * 看板更新床卡
     */
    public static final int EVENT_MN_UPDATE_BED_CARD = EVENT_MN_UPDATE_DATE + 1;

    /**
     * 看板更新主题
     */
    public static final int EVENT_MN_UPDATE_STYLE = EVENT_MN_UPDATE_BED_CARD + 1;

    /**
     * 看板被动更新
     */
    public static final int EVENT_MN_UPDATE_AUTO = EVENT_MN_UPDATE_STYLE + 1;

    /**
     * 看板更新提醒
     */
    public static final int EVENT_MN_UPDATE_REMIND = EVENT_MN_UPDATE_AUTO + 1;

    /**
     * 看板视频呼叫更新
     */
    public static final int EVENT_MN_UPDATE_VIDEO_CALL = EVENT_MN_UPDATE_REMIND + 1;
    /**
     * 看板布局状态变更
     */
    public static final int EVENT_MN_UPDATE_SETTINGS = EVENT_MN_UPDATE_VIDEO_CALL + 1;
    /**
     * 看板移动状态变更
     */
    public static final int EVENT_MN_MODULE_MOVE = EVENT_MN_UPDATE_SETTINGS + 1;
    /**
     * 看板移动状态变更
     */
    public static final int EVENT_MN_MODULE_IDLE = EVENT_MN_MODULE_MOVE + 1;
    /**
     * 看板布局状态变更
     */
    public static final int EVENT_MN_MODULE_LAYOUT = EVENT_MN_MODULE_IDLE + 1;

    /**
     * 移动护理护士信息
     */
    public static final int EVENT_YDHL_NURSE_INFO = EVENT_MN_MODULE_LAYOUT + 1;
}
