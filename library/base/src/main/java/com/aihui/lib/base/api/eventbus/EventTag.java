package com.aihui.lib.base.api.eventbus;

public class EventTag {

//    /**
//     * eventBus发送消息时使用，代表一个对象，无实际作用
//     */
//    public static final int EVENT_BUS_FLAG = 0x0;
    /**
     * eventBus 通知关闭扫码页面
     */
    public static final int CLOSE_SCAN_ACTIVITY = 0x5;
    /**
     * eventBus  更新进度条
     */
    public static final int UPDATE_PROGRESS_BAR = 0x6;
    /**
     * eventBus  关闭视频通话页面，Android 端方法供JS 端调用
     */
    public static final int CLOSE_VIDEO_CHAT_ACTIVITY = 0x7;
    /**
     * eventBus  视频通话结束，JS  端方法供Android 端调用
     */
    public static final int EXIT_VIDEO_CHAT = 0x8;

    /**
     * eventBus  切换到视频通话小窗口
     */
    public static final int SHRINK_VIDEO_CHAT = 0x9;

    /**
     * eventBus 大窗口显示远程视频
     */
    public static final int BIG_WINDOW_REMOTE_VIDEO_SHOW = 0x10;

    /**
     * eventBus 小窗口视频通话结束
     */
    public static final int SMALL_VIDEO_EXIT = 0x11;

    /**
     * eventBus 大窗口视频通话结束
     */
    public static final int BIG_VIDEO_EXIT = 0x12;

    /**
     * eventBus 键盘隐藏
     */
    public static final int KEYBOARD_HIDDEN = 0x13;

    /**
     * 登录跳转注册
     */
    public static final int LOGIN_SHOW_RESISTER = 0x14;

    /**
     * 关闭巡房
     */
    public static final int EVENT_CLOSE_PATROL_ROOM = 0x15;

    /**
     * 关闭远程会诊视频
     */
    public static final int EVENT_MEETING_VIDEO_CLOSE = 0x16;

    /**
     * 远程会诊视频已连接
     */
    public static final int EVENT_MEETING_VIDEO_CONNECTED = 0x17;

    /**
     * 下载apk进度
     */
    public static final int EVENT_UPDATE_DOWNLOAD_PROGRESS = 0x18;

    /**
     * 下载apk结果
     */
    public static final int EVENT_UPDATE_DOWNLOAD_RESULT = 0x19;
}
