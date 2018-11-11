package com.aihui.lib.nurse.db.userinfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.aihui.lib.nurse.db.DBField;
import com.aihui.lib.nurse.db.DBHelper;
import com.aihui.lib.nurse.db.DBSql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡一鸣 on 2018/6/21.
 * DBUserInfoUtils
 */
public class DBUserInfoUtils {

    /**
     * 插入一个用户信息
     * @param userInfo 用户信息
     */
    public synchronized static void insertUserInfo(@NonNull DBUserInfoBean userInfo) {
        SQLiteDatabase database = DBHelper.getDatabase();
        String[] uid = {String.valueOf(userInfo.uid)};
        database.execSQL(DBSql.UPDATE_OTHER_USER_LOGIN_STATE, uid);
        Cursor cursor = database.rawQuery(DBSql.COUNT_USER_INFO, uid);
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            // 存在该用户，更新
            updateUserInfo(userInfo);
        } else {
            // 不存在改用户，插入
            database.execSQL(DBSql.INSERT_USER_INFO, new Object[]{
                    userInfo.hospitalCode,
                    userInfo.account,
                    userInfo.pwd,
                    userInfo.uid,
                    userInfo.accountType,
                    userInfo.name,
                    userInfo.icon,
                    System.currentTimeMillis() / 1000});
        }
        cursor.close();
        database.close();
    }

    /**
     * 更新用户登录状态
     * @param uid uid
     */
    public synchronized static void updateUserInfoLogout(int uid) {
        SQLiteDatabase database = DBHelper.getDatabase();
        database.execSQL(DBSql.UPDATE_USER_INFO_LOGOUT, new Object[]{uid});
        database.execSQL(DBSql.UPDATE_USER_INFO_PWD, new Object[]{null, uid});
        database.close();
    }

    /**
     * 更新用户密码
     * @param uid
     */
    public synchronized static void updateUserInfoPwd(int uid, String pwd) {
        SQLiteDatabase database = DBHelper.getDatabase();
        database.execSQL(DBSql.UPDATE_USER_INFO_PWD, new Object[]{pwd, uid});
        database.close();
    }

    /**
     * 更细用户信息
     * @param userInfo 用户信息
     */
    public synchronized static void updateUserInfo(@NonNull DBUserInfoBean userInfo) {
        SQLiteDatabase database = DBHelper.getDatabase();
        database.execSQL(DBSql.UPDATE_USER_INFO, new Object[]{
                userInfo.hospitalCode,
                userInfo.account,
                userInfo.pwd,
                userInfo.accountType,
                userInfo.name,
                userInfo.icon,
                System.currentTimeMillis() / 1000,
                userInfo.uid});
        database.close();
    }

    /**
     * 查询一个用户
     * @param uid 用户id
     * @return userInfo
     */
    public synchronized static DBUserInfoBean queryUserInfo(int uid) {
        SQLiteDatabase database = DBHelper.getDatabase(false);
        Cursor cursor = database.rawQuery(DBSql.SELECT_USER_INFO,
                new String[]{String.valueOf(uid)});
        try {
            if (cursor.moveToFirst()) {
                // 存在该用户，返回
                return getUserInfo(cursor);
            }
        } finally {
            cursor.close();
            database.close();
        }
        return null;
    }

    public synchronized static DBUserInfoBean queryLoginUserInfo() {
        SQLiteDatabase database = DBHelper.getDatabase(false);
        Cursor cursor = database.rawQuery(DBSql.SELECT_LOGIN_USER_INFO, null);
        try {
            if (cursor.moveToFirst()) {
                // 存在该用户，返回
                return getUserInfo(cursor);
            }
        } finally {
            cursor.close();
            database.close();
        }
        return null;
    }

    /**
     * 查询全部用户
     * @return 表内所有信息
     */
    public synchronized static List<DBUserInfoBean> queryAllUserInfo() {
        List<DBUserInfoBean> userInfoList = null;
        SQLiteDatabase database = DBHelper.getDatabase(false);
        Cursor cursor = database.rawQuery(DBSql.SELECT_ALL_USER_INFO, null);
        while (cursor.moveToNext()) {
            if (userInfoList == null) {
                userInfoList = new ArrayList<>();
            }
            DBUserInfoBean userInfo = getUserInfo(cursor);
            userInfoList.add(userInfo);
        }
        cursor.close();
        database.close();
        return userInfoList;
    }

    @NonNull
    private synchronized static DBUserInfoBean getUserInfo(Cursor cursor) {
        DBUserInfoBean userInfo = new DBUserInfoBean();
        userInfo.hospitalCode = cursor.getString(cursor.getColumnIndex(DBField.TABLE_USER_INFO_HOSPITAL_CODE));
        userInfo.account = cursor.getString(cursor.getColumnIndex(DBField.TABLE_USER_INFO_ACCOUNT));
        userInfo.pwd = cursor.getString(cursor.getColumnIndex(DBField.TABLE_USER_INFO_PWD));
        userInfo.uid = cursor.getInt(cursor.getColumnIndex(DBField.TABLE_USER_INFO_UID));
        userInfo.accountType = cursor.getInt(cursor.getColumnIndex(DBField.TABLE_USER_INFO_ACCOUNT_TYPE));
        userInfo.name = cursor.getString(cursor.getColumnIndex(DBField.TABLE_USER_INFO_NAME));
        userInfo.icon = cursor.getString(cursor.getColumnIndex(DBField.TABLE_USER_INFO_ICON));
        return userInfo;
    }

    /**
     * 删除单个用户
     * @param uid 用户id
     */
    public synchronized static void deleteUserInfo(int uid) {
        SQLiteDatabase database = DBHelper.getDatabase();
        database.execSQL(DBSql.DELETE_USER_INFO, new Object[]{uid});
        database.close();
    }

    public synchronized static void deleteALLUserInfo() {
        SQLiteDatabase database = DBHelper.getDatabase();
        database.execSQL(DBSql.DELETE_ALL_USER_INFO);
        database.close();
    }
}
