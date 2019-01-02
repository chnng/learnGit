package com.aihui.lib.nurse.util;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 胡一鸣 on 2018/11/12.
 */
public final class ViewUtils {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link android.view.View#setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static final void clearWebView(WebView m) {
        if (m == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        m.loadUrl("about:blank");
        m.stopLoading();
        if (m.getHandler() != null)
            m.getHandler().removeCallbacksAndMessages(null);
        m.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) m.getParent())) != null)
            mViewGroup.removeView(m);
        m.setWebChromeClient(null);
        m.setWebViewClient(null);
        m.setTag(null);
        m.clearHistory();
        m.destroy();
        m = null;
    }

    public static void setViewBackgroundColor(int color, View... views) {
        for (View view : views) {
            Drawable drawable = view.getBackground();
            if (drawable instanceof GradientDrawable) {
                ((GradientDrawable) drawable).setColor(color);
            } else if (drawable instanceof LayerDrawable) {
                // mn
                LayerDrawable background = (LayerDrawable) drawable;
                GradientDrawable drawable0 = (GradientDrawable) background.getDrawable(0);
                drawable0.setColor(ViewUtils.getColorWithAlpha(color, 0.5f));
                GradientDrawable drawable1 = (GradientDrawable) background.getDrawable(1);
                drawable1.setColor(color);
            } else {
                view.setBackgroundColor(color);
            }
        }
    }

    public static void setViewBackgroundStrokeColor(int color, int strokeWidth, View... views) {
        for (View view : views) {
            Drawable drawable = view.getBackground();
            if (drawable instanceof GradientDrawable) {
                ((GradientDrawable) drawable).setStroke(strokeWidth, color);
            }
        }
    }

    public static void setTextColor(int color, TextView... views) {
        for (TextView view : views) {
            if (view != null) {
                view.setTextColor(color);
            }
        }
    }

    /**
     * 对rgb色彩加入透明度
     *
     * @param color
     * @param alpha 透明度，取值范围 0.0f -- 1.0f.
     * @return a color with alpha made from base color
     */
    public static int getColorWithAlpha(int color, float alpha) {
        if (alpha == 0) {
            return 0;
        } else if (alpha == 1) {
            return color;
        }
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & color;
        return a + rgb;
    }

    /**
     * @param color 主题色
     * @return 是否亮色
     */
    public static boolean isColorBright(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        return (red + green + blue) / 3 > 128;
    }

    /**
     * 混合颜色
     *
     * @param c0    背景
     * @param c1    前景
     * @param alpha
     * @return
     */
    public static int mixColors(int c0, int c1, float alpha) {
        if (alpha <= 0) {
            return c0;
        } else if (alpha >= 1) {
            return c1;
        }
        int red0 = (c0 & 0xff0000) >> 16;
        int green0 = (c0 & 0x00ff00) >> 8;
        int blue0 = (c0 & 0x0000ff);
        int red1 = (c1 & 0xff0000) >> 16;
        int green1 = (c1 & 0x00ff00) >> 8;
        int blue1 = (c1 & 0x0000ff);
        int R = (int) (red0 * (1 - alpha) + red1 * alpha);
        int G = (int) (green0 * (1 - alpha) + green1 * alpha);
        int B = (int) (blue0 * (1 - alpha) + blue1 * alpha);
        return Color.argb(255, R, G, B);
    }
}
