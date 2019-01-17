package com.aihui.lib.base.ui.view.itemdecoration;

import android.graphics.Rect;
import android.view.View;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.BaseApplication;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 胡一鸣 on 2018/8/15.
 */
public class BottomItemDecoration extends RecyclerView.ItemDecoration {

    public static BottomItemDecoration DECORATION = new BottomItemDecoration();

    private int bottom;

    private BottomItemDecoration() {
        this(BaseApplication.getContext().getResources().getDimensionPixelSize(R.dimen.dp80));
    }

    public BottomItemDecoration(int bottom) {
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (childAdapterPosition == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = bottom;
        }
    }
}
