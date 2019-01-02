package com.aihui.lib.base.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.aihui.lib.base.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhy on 15/6/3.
 */
public class VDHLayout extends RelativeLayout {
    private ViewDragHelper mDragHelper;
    private Map<View, Rect> paramsMap = new HashMap<>();
    private boolean mIsKeepLayout;

    public VDHLayout(Context context) {
        this(context, null);
    }

    public VDHLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VDHLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.VDHLayout, defStyle, 0);
        mIsKeepLayout = t.getBoolean(R.styleable.VDHLayout_vdh_is_keep_layout, false);
        t.recycle();
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return child.getTag() != null && getContext().getString(R.string.vdh_drag_tag).equals(child.getTag().toString());
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                int limitLeft = getPaddingLeft();
                if (left < limitLeft) {
                    return limitLeft;
                }
                int limitRight = getWidth() - getPaddingRight() - child.getMeasuredWidth();
                if (left > limitRight) {
                    return limitRight;
                }
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                int limitTop = getPaddingTop();
                if (top < limitTop) {
                    return limitTop;
                }
                int limitBottom = getHeight() - getPaddingBottom() - child.getMeasuredHeight();
                if (top > limitBottom) {
                    return limitBottom;
                }
                return top;
            }

            //手指释放的时候回调
            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mIsKeepLayout) {
                    Rect rect = paramsMap.get(releasedChild);
                    if (rect == null) {
                        rect = new Rect();
                        paramsMap.put(releasedChild, rect);
                    }
                    rect.left = releasedChild.getLeft();
                    rect.top = releasedChild.getTop();
                    rect.right = releasedChild.getRight();
                    rect.bottom = releasedChild.getBottom();
                }
            }

            //在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {

            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(@NonNull View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }
        });
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return false;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mIsKeepLayout) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    Rect params = paramsMap.get(child);
                    if (params != null) {
                        child.layout(params.left, params.top, params.right, params.bottom);
                    }
                }
            }
        }
    }
}
