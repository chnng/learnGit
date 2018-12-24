package com.aihui.lib.base.ui.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.BaseApplication;

/**
 * Created by 胡一鸣 on 2018/8/15.
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private Paint mPaint;
    private int mDividerSize = 1;
    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    private int mOrientation;
    private boolean mIsLastItemDecorate = true;

    public LinearItemDecoration() {
        this(VERTICAL);
    }

    public LinearItemDecoration(int orientation) {
        this.mOrientation = orientation;
        mPaint = new Paint();
        mPaint.setColor(BaseApplication.getContext().getResources().getColor(R.color.colorPrimary));
    }

    public void setColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public void setLastItemDecorate(boolean isLastItemDecorate) {
        mIsLastItemDecorate = isLastItemDecorate;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        switch (mOrientation) {
            case VERTICAL:
                outRect.bottom = mDividerSize;
                break;
            case HORIZONTAL:
                outRect.right = mDividerSize;
                break;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (!mIsLastItemDecorate && i == childCount - 1) {
                continue;
            }
            View view = parent.getChildAt(i);
            switch (mOrientation) {
                case VERTICAL:
                    float dividerTop = view.getBottom();
                    float dividerLeft = parent.getPaddingLeft();
                    float dividerBottom = view.getBottom() + mDividerSize;
                    float dividerRight = parent.getWidth() - parent.getPaddingRight();
                    c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, mPaint);
                    break;
                case HORIZONTAL:
                    dividerLeft = view.getRight();
                    dividerTop = parent.getPaddingTop();
                    dividerBottom = parent.getHeight() - parent.getPaddingBottom();
                    dividerRight = view.getRight() + mDividerSize;
                    c.drawRect(dividerLeft, dividerTop, dividerRight, dividerBottom, mPaint);
                    break;
            }
        }
    }
}
