package com.aihui.lib.base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by 路传涛 on 2017/6/15.
 */

public final class SharePreferenceUtils {
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_data";
    /**
     * apkPath 对应的键名
     */
    public static final String SP_APK_PATH = "apk_path";
    /**
     * token
     */
    public static final String SP_TOKEN = "token";
    /**
     * 接口地址
     */
    public static final String SP_BASE_URL = "base_url";
    /**
     * 天护数据分析范围
     */
    public static final String SP_TH_WARD_RANGE = "th_ward_range";
    /**
     * 天护位置信息
     */
    public static final String SP_TH_LOCATION_INFO = "th_location_info";
    /**
     * 天护推送地址
     */
    public static final String SP_TH_WEB_URL = "th_web_url";
    /**
     * 呼叫个推绑定时间
     */
    public static final String SP_CALL_GETUI_TIMESTAMP_BIND = "push_bind_alias_timestamp";
    /**
     * 呼叫个推绑定延时
     */
    public static final String SP_CALL_GETUI_TIMESTAMP_DELAY = "task_delay_timestamp";
    /**
     * 呼叫床位
     */
    public static final String SP_CALL_BED_NUMBER = "call_bed_number";
    /**
     * 推送院区识别代码
     */
    public static final String SP_CALL_HOSPITAL_IDENTIFY = "call_hospital_identify";
    /**
     * 推送病区识别代码
     */
    public static final String SP_CALL_WARD_IDENTIFY = "call_ward_identify";
    /**
     * 推送床位识别代码
     */
    public static final String SP_CALL_BED_IDENTIFY = "call_bed_identify";
    /**
     * 个推cid
     */
    public static final String SP_GETUI_CLIENT_ID = "getui_client_id";
    /**
     * 看板首页类型
     */
    public static final String SP_MN_HOMEPAGE_TYPE = "mn_homepage_type";
    /**
     * 看板设置:首页切换
     */
    public static final String SP_MN_SETTINGS_HOMEPAGE = "mn_settings_homepage";
    /**
     * 看板设置:重要提醒
     */
    public static final String SP_MN_SETTINGS_REMIND = "mn_settings_remind";
    /**
     * 看板设置:物联网设备
     */
    public static final String SP_MN_SETTINGS_DEVICE = "mn_settings_device";
    /**
     * 看板补丁最后修改时间
     */
    public static final String SP_MN_PATCH_LAST_MODIFIED = "mn_patch_last_modified";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else if (object != null) {
            editor.putString(key, object.toString());
        } else {
            editor.putString(key, null);
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    public static <T> T get(Context context, String key, @NonNull T defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        try {
            if (defaultObject instanceof String) {
                return (T) sp.getString(key, (String) defaultObject);
            } else if (defaultObject instanceof Integer) {
                return (T) (Integer) sp.getInt(key, (Integer) defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return (T) (Boolean) sp.getBoolean(key, (Boolean) defaultObject);
            } else if (defaultObject instanceof Float) {
                return (T) (Float) sp.getFloat(key, (Float) defaultObject);
            } else if (defaultObject instanceof Long) {
                return (T) (Long) sp.getLong(key, (Long) defaultObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultObject;
        }
        return defaultObject;
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }


    /**
     * 保存图片到SharedPreferences
     *
     * @param context   context
     * @param imageView imageView
     */
    public static void putImage(Context context, String key, ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        // 将Bitmap压缩成字节数组输出流
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byStream);
        // 利用Base64将我们的字节数组输出流转换成String
        byte[] byteArray = byStream.toByteArray();
        String imgString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        // 将String保存shareUtils
        SharePreferenceUtils.put(context, key, imgString);
    }

    /**
     * 从SharedPreferences读取图片
     *
     * @param context context
     */
    public static Bitmap getImage(Context context, String key) {
        String imgString = SharePreferenceUtils.get(context, key, "");
        if (!TextUtils.isEmpty(imgString)) {
            // 利用Base64将我们string转换
            byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            // 生成bitmap
            return BitmapFactory.decodeStream(byStream);
        }
        return null;
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * apply 和 commit方法的区别：
     * 1. apply没有返回值而commit返回boolean表明修改是否提交成功
     * <p></p>
     * 2. apply是将修改数据原子提交到内存, 而后异步真正提交到硬件磁盘,
     * 而commit是同步的提交到硬件磁盘，
     * 因此，在多个并发的提交commit的时候，他们会等待正在处理的commit保存到磁盘后在操作，从而降低了效率。
     * 而apply只是原子的提交到内容，后面有调用apply的函数的将会直接覆盖前面的内存数据，
     * 这样从一定程度上提高了很多效率。
     * <p></p>
     * 3. apply方法不会提示任何失败的提示。
     * 由于在一个进程中，sharedPreference是单实例，一般不会出现并发冲突，
     * 如果对提交的结果不关心的话，建议使用apply，
     * 当然需要确保提交成功且有后续操作的话，还是需要用commit的。
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }
}
