package com.aihui.lib.base.api.db;

/**
 * Created by 胡一鸣 on 2018/6/21.
 * 数据库信息
 */
public class DBField {
    static final String DB_NAME = "aihui_th.db";
    static final int DB_VERSION = 1;

    static final int USER_INFO_LOGOUT = 0;
    static final int USER_INFO_LOGIN = 1;

    public static final String TABLE_NAME_USER_INFO = "user_info";
    public static final String TABLE_USER_INFO_HOSPITAL_CODE = "hospital_code";
    public static final String TABLE_USER_INFO_ACCOUNT = "account";
    public static final String TABLE_USER_INFO_PWD = "password";
    public static final String TABLE_USER_INFO_UID = "uid";
    public static final String TABLE_USER_INFO_ACCOUNT_TYPE = "account_type";
    public static final String TABLE_USER_INFO_LOGIN_STATE = "login_state";
    public static final String TABLE_USER_INFO_TIMESTAMP = "timestamp";
    public static final String TABLE_USER_INFO_ICON = "icon";
    public static final String TABLE_USER_INFO_NAME = "name";
}
