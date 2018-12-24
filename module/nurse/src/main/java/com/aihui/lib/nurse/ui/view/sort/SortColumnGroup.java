package com.aihui.lib.nurse.ui.view.sort;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡一鸣 on 2018/8/19.
 */
public class SortColumnGroup extends LinearLayout implements View.OnClickListener {
    private List<SortColumnView> mColumnViewList;
    private OnColumnSelectListener mOnColumnSelectListener;

    public SortColumnGroup(Context context) {
        this(context, null);
    }

    public SortColumnGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortColumnGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        mColumnViewList = new ArrayList<>();
    }

    public void setOnColumnSelectListener(OnColumnSelectListener onColumnSelectListener) {
        this.mOnColumnSelectListener = onColumnSelectListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof SortColumnView) {
                child.setOnClickListener(this);
                SortColumnView columnView = (SortColumnView) child;
                columnView.setSelected(columnView.getType() == SortType.TYPE_DESC);
                mColumnViewList.add(columnView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mColumnViewList.size(); i++) {
            SortColumnView view = mColumnViewList.get(i);
            if (v.getId() == view.getId()) {
                if (mOnColumnSelectListener != null) {
                    mOnColumnSelectListener.onSelect(i, view.getType());
                }
            } else {
                view.setType(SortType.TYPE_NORMAL);
            }
        }
    }

    public void reset() {
        for (int i = 0; i < mColumnViewList.size(); i++) {
            SortColumnView view = mColumnViewList.get(i);
            view.setType(view.isSelected() ? SortType.TYPE_DESC : SortType.TYPE_NORMAL);
        }
    }

    public interface OnColumnSelectListener {
        void onSelect(int column, @SortType int type);
    }
}
