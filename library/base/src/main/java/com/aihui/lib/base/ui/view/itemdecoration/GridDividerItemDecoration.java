package com.aihui.lib.base.ui.view.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.BaseApplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adds interior dividers to a RecyclerView with a GridLayoutManager.
 */
public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;
    private int mDividerSize = 1;

    public GridDividerItemDecoration() {
        this(R.color.colorPrimary, 1);
    }

    public GridDividerItemDecoration(int colorId, int dividerSize) {
        mPaint = new Paint();
        mPaint.setColor(BaseApplication.getContext().getResources().getColor(colorId));
        this.mDividerSize = dividerSize;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(0, 0, mDividerSize, mDividerSize);
//        int spanCount = getSpanCount(parent);
//        int childAdapterPosition = parent.getChildAdapterPosition(view);
//        if (isLastColumn(childAdapterPosition, spanCount)) {
//            // 如果是最后一列，则不需要绘制右边
//            outRect.set(0, 0, 0, mDividerSize);
//        } else {
//            if (isFirstRaw(childAdapterPosition, spanCount)) {
//                //如果是第一行 需要绘制上面
//                outRect.set(0, mDividerSize, mDividerSize, mDividerSize);
//            } else {
//                outRect.set(0, 0, mDividerSize, mDividerSize);
//            }
//        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDividerSize;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerSize;
            c.drawRect(left, top, right, bottom, mPaint);
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerSize;
            c.drawRect(left, top, right, bottom, mPaint);
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
        }
    }

    public static int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = GridLayoutManager.DEFAULT_SPAN_COUNT;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public static boolean isLastColumn(int pos, int spanCount) {
        // 如果是最后一列，则不需要绘制右边
        return (pos + 1) % spanCount == 0;
    }

    public static boolean isFirstRaw(int pos, int spanCount) {
        // 如果是第一行 需要绘制上面
        return pos <= spanCount;
    }

    public static boolean isLastRaw(int pos, int spanCount, int size) {
        // 如果是最后一列，则不需要绘制右边
        return pos + spanCount > size;
    }
}