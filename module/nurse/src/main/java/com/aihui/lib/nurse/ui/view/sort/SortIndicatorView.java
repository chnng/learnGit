package com.aihui.lib.nurse.ui.view.sort;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aihui.lib.nurse.R;

/**
 * Created by 胡一鸣 on 2018/8/18.
 */
public class SortIndicatorView extends LinearLayout {

    private ImageView mIvUp, mIvDown;

    @SortType
    private int mType;

    public SortIndicatorView(Context context) {
        this(context, null);
    }

    public SortIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SortIndicatorView);
        @SortType int type = a.getInt(R.styleable.SortIndicatorView_sort_type, SortType.TYPE_NORMAL);
        a.recycle();
        init();
        setType(type);
    }

    private void init() {
        setOrientation(VERTICAL);
        View.inflate(getContext(), R.layout.view_sort_indicator, this);
        mIvUp = findViewById(R.id.iv_sort_indicator_up);
        mIvDown = findViewById(R.id.iv_sort_indicator_down);
    }

    @SortType
    public int getType() {
        return mType;
    }

    public void setType(@SortType int type) {
        mType = type;
        switch (type) {
            case SortType.TYPE_NORMAL:
                mIvUp.setImageResource(R.mipmap.select_up);
                mIvDown.setImageResource(R.mipmap.select_up);
                break;
            case SortType.TYPE_ASC:
                mIvUp.setImageResource(R.mipmap.select_down);
                mIvDown.setImageResource(R.mipmap.select_up);
                break;
            case SortType.TYPE_DESC:
                mIvUp.setImageResource(R.mipmap.select_up);
                mIvDown.setImageResource(R.mipmap.select_down);
                break;
        }
    }

    @SortType
    public int nextType() {
        switch (mType) {
            case SortType.TYPE_NORMAL:
            case SortType.TYPE_ASC:
                return SortType.TYPE_DESC;
            case SortType.TYPE_DESC:
                return SortType.TYPE_ASC;
            default:
                return SortType.TYPE_NORMAL;
        }
    }
}
