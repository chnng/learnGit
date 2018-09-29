package com.aihui.lib.base.constant;

/**
 * 请求码
 */
public final class RequestCode {

    /**
     * 1000-1999
     */
    // 扫描二维码
    public static final int RESULT_COMMON_SCAN = 1000;
    // 跳转说明页
    public static final int RESULT_HELP = 1001;
    // 更新用户头像
    public static final int RESULT_UPDATE_USER_ICON = 1002;
    // 更新用户信息
    public static final int RESULT_UPDATE_USER_INFO = 1003;
    // 更新用户密码
    public static final int RESULT_UPDATE_USER_PWD = 1004;
    // 远程会议
    public static final int RESULT_VIDEO_MEETING = 1005;
    // 定时事件列表
    public static final int RESULT_TIME_EVENT_LIST = 1006;
    // 定时事件编辑
    public static final int RESULT_TIME_EVENT_EDIT = 1007;
    // 药典维护编辑
    public static final int RESULT_DRUG_DIC_EDIT = 1008;
    // 安装应用权限页回调
    public static final int RESULT_INSTALL_APK = 1009;
    // 选择文档文件
    public static final int RESULT_DOCUMENT_SELECT = 1010;


    /**
     * 2000-2999
     */

    public static final int PERMISSION_AMAP = 2000;
    public static final int PERMISSION_ZXING = 2001;
    public static final int PERMISSION_PUSH_AUDIO = 2002;
    public static final int PERMISSION_CALL_NURSE = 2003;
    public static final int PERMISSION_PATROL_ROOM = 2004;
    public static final int PERMISSION_MEETING = 2005;
    public static final int PERMISSION_PDF = 2006;
    public static final int PERMISSION_PUSH_VIDEO = 2007;
    public static final int PERMISSION_GETUI = 2008;
}
