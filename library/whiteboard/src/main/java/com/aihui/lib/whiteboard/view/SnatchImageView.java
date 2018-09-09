package com.aihui.lib.whiteboard.view;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 胡一鸣 on 2018/7/31.
 */
public class SnatchImageView extends AppCompatImageView {
    public SnatchImageView(Context context) {
        this(context, null);
    }

    public SnatchImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnatchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                performClick();
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
