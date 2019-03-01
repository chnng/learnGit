package com.aihui.lib.base.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 胡一鸣 on 2018/8/23.
 */
public final class ScreenshotUtils {

    /**
     * 截屏
     *
     * @param context
     * @return
     */
    private Bitmap screenshotScreen(Activity context) {
        View cv = context.getWindow().getDecorView();
        Bitmap bmp = Bitmap.createBitmap(cv.getWidth(), cv.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        cv.draw(canvas);
        return bmp;
    }

    /**
     * 根据指定的Activity截图（带空白的状态栏）
     *
     * @param context 要截图的Activity
     * @return Bitmap
     */
    public static Bitmap screenshotActivity(Activity context) {
        View view = context.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 根据指定的Activity截图（去除状态栏）
     *
     * @param activity 要截图的Activity
     * @return Bitmap
     */
    public static Bitmap screenshotActivityNoStatusBar(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    /**
     * 根据指定的view截图
     *
     * @param v 要截图的view
     * @return Bitmap
     */
    public static Bitmap screenshotView(View v) {
        return screenshotView(v, 0, 0);
    }

    public static Bitmap screenshotView(View v, int reqWidth, int reqHeight) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
        int measuredWidth = v.getMeasuredWidth();
        int measuredHeight = v.getMeasuredHeight();
        v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + measuredWidth, (int) v.getY() + measuredHeight);

        Matrix matrix = null;
        if (reqWidth > 0 && reqHeight > 0) {
            float scale = Math.min((float) reqWidth / measuredWidth, (float) reqHeight / measuredHeight);
            if (scale != 1) {
                matrix = new Matrix();
                matrix.postScale(scale, scale);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, measuredWidth, measuredHeight, matrix, true);
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return bitmap;
    }

    /**
     * Scrollview截屏
     *
     * @param scrollView 要截图的ScrollView
     * @return Bitmap
     */
    public static Bitmap screenshotScrollView(ScrollView scrollView) {
        int h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * RecyclerView截屏
     *
     * @param view 要截图的RecyclerView
     * @return Bitmap
     */
    public static Bitmap screenshotRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }

    /**
     * 截取webView可视区域的截图
     *
     * @param webView 前提：WebView要设置webView.setDrawingCacheEnabled(true);
     * @return
     */
    private Bitmap screenshotWebViewVisibleSize(WebView webView) {
        return webView.getDrawingCache();
    }


    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    private Bitmap screenshotWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();

        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }
}
