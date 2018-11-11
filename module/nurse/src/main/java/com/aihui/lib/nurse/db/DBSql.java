package com.aihui.lib.nurse.db;

/**
 * Created by 胡一鸣 on 2018/6/21.
 * 数据库语句
 */
public class DBSql {
    /**
     * 创建用户表
     */
    public static final String CREATE_TABLE_USER_INFO =
            "CREATE TABLE IF NOT EXISTS " + DBField.TABLE_NAME_USER_INFO
                    + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DBField.TABLE_USER_INFO_HOSPITAL_CODE + " TEXT, "
                    + DBField.TABLE_USER_INFO_ACCOUNT + " TEXT, "
                    + DBField.TABLE_USER_INFO_PWD + " TEXT, "
                    + DBField.TABLE_USER_INFO_UID + " INTEGER, "
                    + DBField.TABLE_USER_INFO_ACCOUNT_TYPE + " INTEGER, "
                    + DBField.TABLE_USER_INFO_LOGIN_STATE + " INTEGER, "
                    + DBField.TABLE_USER_INFO_NAME + " TEXT, "
                    + DBField.TABLE_USER_INFO_ICON + " TEXT, "
                    + DBField.TABLE_USER_INFO_TIMESTAMP + " INTEGER "
//            + DBField.TABLE_USER_INFO_GENDER + " INTEGER,"
//            + DBField.TABLE_USER_INFO_EMAIL + " TEXT,"
//            + DBField.TABLE_USER_INFO_PHONE + " TEXT,"
//            + DBField.TABLE_USER_INFO_INTRODUCTION + " TEXT"
                    + ")";

    /**
     * 插入用户，默认为登陆状态
     */
    public static final String INSERT_USER_INFO =
            "INSERT INTO " + DBField.TABLE_NAME_USER_INFO + "("
                    + DBField.TABLE_USER_INFO_HOSPITAL_CODE + ","
                    + DBField.TABLE_USER_INFO_ACCOUNT + ","
                    + DBField.TABLE_USER_INFO_PWD + ","
                    + DBField.TABLE_USER_INFO_UID + ","
                    + DBField.TABLE_USER_INFO_ACCOUNT_TYPE + ","
                    + DBField.TABLE_USER_INFO_LOGIN_STATE + ","
                    + DBField.TABLE_USER_INFO_NAME + ","
                    + DBField.TABLE_USER_INFO_ICON + ","
                    + DBField.TABLE_USER_INFO_TIMESTAMP
                    + " ) VALUES (?, ?, ?, ?, ?, " + DBField.USER_INFO_LOGIN + ", ?, ?, ?)";

    /**
     * 将其他用户登陆状态置为登出
     */
    public static final String UPDATE_OTHER_USER_LOGIN_STATE =
            "UPDATE " + DBField.TABLE_NAME_USER_INFO + " SET "
                    + DBField.TABLE_USER_INFO_LOGIN_STATE + " = " + DBField.USER_INFO_LOGOUT
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " != ?";

    /**
     * 当前账户退出登录
     */
    public static final String UPDATE_USER_INFO_LOGOUT =
            "UPDATE " + DBField.TABLE_NAME_USER_INFO + " SET "
                    + DBField.TABLE_USER_INFO_LOGIN_STATE + " = " + DBField.USER_INFO_LOGOUT
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " = ?";

    /**
     * 当前账户退出登录
     */
    public static final String UPDATE_USER_INFO_PWD =
            "UPDATE " + DBField.TABLE_NAME_USER_INFO + " SET "
                    + DBField.TABLE_USER_INFO_PWD + " = ?"
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " = ?";

    /**
     * 更新用户，默认登陆状态
     */
    public static final String UPDATE_USER_INFO =
            "UPDATE " + DBField.TABLE_NAME_USER_INFO + " SET "
                    + DBField.TABLE_USER_INFO_LOGIN_STATE + " = " + DBField.USER_INFO_LOGIN + ", "
                    + DBField.TABLE_USER_INFO_HOSPITAL_CODE + " = ?, "
                    + DBField.TABLE_USER_INFO_ACCOUNT + " = ?, "
                    + DBField.TABLE_USER_INFO_PWD + " = ?, "
                    + DBField.TABLE_USER_INFO_ACCOUNT_TYPE + " = ?, "
                    + DBField.TABLE_USER_INFO_NAME + " = ?, "
                    + DBField.TABLE_USER_INFO_ICON + " = ?, "
                    + DBField.TABLE_USER_INFO_TIMESTAMP + " = ?"
//                    + DBField.TABLE_USER_INFO_GENDER + " = ?, "
//                    + DBField.TABLE_USER_INFO_EMAIL + " = ?, "
//                    + DBField.TABLE_USER_INFO_PHONE + " = ?, "
//                    + DBField.TABLE_USER_INFO_INTRODUCTION + " = ? "
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " = ?";

    /**
     * 查询用户数
     */
    public static final String COUNT_USER_INFO =
            "SELECT count(*) as count FROM " + DBField.TABLE_NAME_USER_INFO
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " = ?";

    /**
     * 查询用户
     */
    public static final String SELECT_USER_INFO =
            "SELECT * FROM " + DBField.TABLE_NAME_USER_INFO
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " = ?";

    /**
     * 查询登陆用户
     */
    public static final String SELECT_LOGIN_USER_INFO =
            "SELECT * FROM " + DBField.TABLE_NAME_USER_INFO
                    + " WHERE " + DBField.TABLE_USER_INFO_LOGIN_STATE + " = " + DBField.USER_INFO_LOGIN;

    /**
     * 查询所有用户
     */
    public static final String SELECT_ALL_USER_INFO =
            "SELECT * FROM " + DBField.TABLE_NAME_USER_INFO
                    + " ORDER BY " + DBField.TABLE_USER_INFO_TIMESTAMP + " DESC";

    /**
     * 删除用户
     */
    public static final String DELETE_USER_INFO =
            "DELETE FROM " + DBField.TABLE_NAME_USER_INFO
                    + " WHERE " + DBField.TABLE_USER_INFO_UID + " = ?";

    /**
     * 清空用户表
     */
    public static final String DELETE_ALL_USER_INFO =
            "DELETE FROM " + DBField.TABLE_NAME_USER_INFO;
}
