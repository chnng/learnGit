package com.aihui.lib.nurse.ui.view.sort;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aihui.lib.base.util.DensityUtils;
import com.aihui.lib.nurse.R;

/**
 * Created by 胡一鸣 on 2018/8/18.
 */
public class SortColumnView extends FrameLayout {

    SortIndicatorView mIndicator;

    public SortColumnView(Context context) {
        this(context, null);
    }

    public SortColumnView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SortColumnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SortColumnView);
        String column = a.getString(R.styleable.SortColumnView_sort_column);
        @SortType int type = a.getInt(R.styleable.SortColumnView_sort_indicator_type, SortType.TYPE_NORMAL);
        int color = a.getColor(R.styleable.SortColumnView_sort_color, context.getResources().getColor(R.color.gray0));
        float size = context.getResources().getDimension(R.dimen.textSizeMiddle);
        size = a.getDimension(R.styleable.SortColumnView_sort_size, size);
        boolean bold = a.getBoolean(R.styleable.SortColumnView_sort_bold, true);
        a.recycle();
        View.inflate(context, R.layout.view_sort_column, this);
        TextView tvColumn = findViewById(R.id.tv_sort_column);
        mIndicator = findViewById(R.id.view_sort_indicator);
        mIndicator.setType(type);
        tvColumn.setText(column);
        tvColumn.setTextColor(color);
        tvColumn.setTextSize(DensityUtils.px2dip(context, size));
        tvColumn.setTypeface(bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    @SortType
    public int getType() {
        return mIndicator.getType();
    }

    public void setType(@SortType int type) {
        mIndicator.setType(type);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(v -> {
            mIndicator.setType(mIndicator.nextType());
            if (l != null) {
                l.onClick(v);
            }
        });
    }
}
