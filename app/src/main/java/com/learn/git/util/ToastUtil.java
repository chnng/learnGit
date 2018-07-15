package com.learn.git.util;

import android.os.Looper;
import android.widget.Toast;

import com.learn.git.ui.base.BaseApplication;

/**
 * Created by 路传涛 on 2017/5/24.
 */

public class ToastUtil {
    private static volatile Toast mToast = null;

    private static volatile String oldMsg;

    private static long oneTime;


    /**
     * 使吐司只更新内容和时间，而不再是重新弹出
     * @param strId 内容id
     */
    public static void toast(int strId) {
        toast(BaseApplication.getContext().getString(strId));
    }

    public static void toast(String text) {
        if (text.equals(oldMsg)) {
            if (System.currentTimeMillis() - oneTime > Toast.LENGTH_SHORT) {
                setToastText(text);
            }
        } else {
            oldMsg = text;
            setToastText(text);
        }
        oneTime = System.currentTimeMillis();
    }

    private static void setToastText(String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToast(text);
        } else {
            HandlerUtil.getUIHandler().post(() -> showToast(text));
        }
    }

    /**
     * 使吐司只更新内容和时间，而不再是重新弹出
     * @param text 内容
     */
    private static void showToast(String text) {
        LogUtil.e(text);
        if (mToast == null) {
            mToast = Toast.makeText(BaseApplication.getContext(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
