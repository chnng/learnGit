package com.aihui.lib.base.util;

import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.aihui.lib.base.app.BaseApplication;

/**
 * Created by 路传涛 on 2017/5/24.
 */

public final class ToastUtils {
    private static Toast mToast = null;

    private static String oldMsg;

    /**
     * 使吐司只更新内容和时间，而不再是重新弹出
     * @param strId 内容id
     */
    public static void toast(int strId) {
        toast(BaseApplication.getContext().getString(strId));
    }

    /**
     * 使吐司只更新内容和时间，而不再是重新弹出
     * @param text 内容
     */
    public static void toast(String text) {
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException("can not show the toast of null!");
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToast(text);
        } else {
            HandlerUtils.getUIHandler().post(() -> showToast(text));
        }
    }

    private static void showToast(String text) {
        if (ApplicationUtils.isOreo()) {
            getToast(text).show();
        } else {
            if (mToast == null) {
                mToast = getToast(text);
            }
            if (text.equals(oldMsg)) {
                mToast.show();
            } else {
                oldMsg = text;
                mToast.setText(text);
                mToast.show();
            }
        }
    }

    private static Toast getToast(String text) {
        return Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_SHORT);
    }
}
