package com.aihui.lib.base.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 胡一鸣 on 2018/7/3.
 */
public class InterceptView extends View {
    public InterceptView(Context context) {
        super(context);
    }

    public InterceptView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
