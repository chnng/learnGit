package com.aihui.lib.nurse.ui.view.gallery;

import android.util.DisplayMetrics;
import android.view.View;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 胡一鸣 on 2018/9/1.
 */
public class GalleryLayoutManager extends CarouselLayoutManager {
    /**
     * Many calculations are made depending on orientation. To keep it clean, this interface
     * helps {@link LinearLayoutManager} make those decisions.
     */
//    OrientationHelper mOrientationHelper;
    public GalleryLayoutManager(int orientation) {
        this(orientation, false);
    }

    public GalleryLayoutManager(int orientation, boolean circleLayout) {
        super(orientation, circleLayout);
//        mOrientationHelper =
//                OrientationHelper.createOrientationHelper(this, orientation);
    }

//    @Override
//    public int computeHorizontalScrollOffset(RecyclerView.State state) {
//        return computeScrollOffset(state);
//    }
//
//    private int computeScrollOffset(RecyclerView.State state) {
//        if (getChildCount() == 0) {
//            return 0;
//        }
//        ensureLayoutState();
//        return ScrollbarHelper.computeScrollOffset(state, mOrientationHelper,
//                findFirstVisibleChildClosestToStart(!mSmoothScrollbarEnabled, true),
//                findFirstVisibleChildClosestToEnd(!mSmoothScrollbarEnabled, true),
//                this, mSmoothScrollbarEnabled, mShouldReverseLayout);
//    }


    @Override
    public void smoothScrollToPosition(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            // 返回：滑过1px时经历的时间(ms)。
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 150f / displayMetrics.densityDpi;
            }

            @Override
            public int calculateDyToMakeVisible(final View view, final int snapPreference) {
                if (!canScrollVertically()) {
                    return 0;
                }
                return getOffsetForCurrentView(view);
            }

            @Override
            public int calculateDxToMakeVisible(final View view, final int snapPreference) {
                if (!canScrollHorizontally()) {
                    return 0;
                }
                return getOffsetForCurrentView(view);
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
