package com.aihui.lib.nurse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aihui.lib.base.app.BaseApplication;

/**
 * Created by 胡一鸣 on 2018/6/21.
 * 数据库工具
 */
public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mDBHelper;

    public DBHelper(Context context) {
        super(context, DBField.DB_NAME, null, DBField.DB_VERSION);
    }

    public static SQLiteDatabase getDatabase() {
        return getDatabase(true);
    }

    public static SQLiteDatabase getDatabase(boolean isWritable) {
        if (mDBHelper == null) {
            synchronized (DBHelper.class) {
                if (mDBHelper == null) {
                    mDBHelper = new DBHelper(BaseApplication.getContext());
                }
            }
        }
        if (isWritable) {
            return mDBHelper.getWritableDatabase();
        } else {
            return mDBHelper.getReadableDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBSql.CREATE_TABLE_USER_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
