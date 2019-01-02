package com.aihui.lib.nurse.ui.view.gallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.azoft.carousellayoutmanager.DefaultChildSelectionListener;

import java.util.Locale;

/**
 * Created by 胡一鸣 on 2018/9/1.
 */
public class GalleryRecyclerView extends RecyclerView {

//    private int mTouchSlop;
//    private float mLastMotionX, mLastMotionY;
//    private boolean mIsBeingDragged;
//    private boolean mIsUnableToDrag;

    public GalleryRecyclerView(Context context) {
        this(context, null);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        ViewConfiguration vc = ViewConfiguration.get(context);
//        mTouchSlop = vc.getScaledTouchSlop();

        init();
    }

    private void init() {
        RecyclerView recyclerView = this;
        CarouselLayoutManager layoutManager = new GalleryLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(2);
        recyclerView.setLayoutManager(layoutManager);

        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
//        recyclerView.setAdapter(new Adapter());
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
//        recyclerView.addOnScrollListener(new ScrollListener());
        // enable center post touching on item and item click listener
        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
//                final int position = recyclerView.getChildLayoutPosition(v);
//                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
//                ToastUtils.toast(msg);
            }
        }, recyclerView, layoutManager);

//        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {
//
//            @Override
//            public void onCenterItemChanged(final int adapterPosition) {
//                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
////                    final int value = adapter.mPosition[adapterPosition];
///*
//                    adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
//                    adapter.notifyItemChanged(adapterPosition);
//*/
//                }
//            }
//        });
    }

    public void addOnItemSelectionListener(@NonNull final CarouselLayoutManager.OnCenterItemSelectionListener onCenterItemSelectionListener) {
        ((CarouselLayoutManager) getLayoutManager()).addOnItemSelectionListener(onCenterItemSelectionListener);
    }

//    private class ScrollListener extends OnScrollListener {
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//        }
//
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            mIsBeingDragged = newState != RecyclerView.SCROLL_STATE_IDLE;
//        }
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            LogUtils.e("dispatchTouchEvent:" + MotionEvent.actionToString(ev.getAction()));
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent e) {
//        int action = e.getAction();
////        if (ApplicationUtils.isKitkat()) {
////            LogUtils.e("onInterceptTouchEvent:" + MotionEvent.actionToString(action));
////        }
//        // Nothing more to do here if we have decided whether or not we
//        // are dragging.
//        if (action != MotionEvent.ACTION_DOWN) {
//            if (mIsBeingDragged) {
//                return true;
//            }
////            if (mIsUnableToDrag) {
////                return false;
////            }
//        }
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastMotionX = e.getX();
//                mLastMotionY = e.getY();
//                if (canScrollHorizontally()) {
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                final float x = e.getX();
//                final float xDiff = Math.abs(x - mLastMotionX);
//                final float y = e.getY();
//                final float yDiff = Math.abs(y - mLastMotionY);
//                mLastMotionX = e.getX();
//                mLastMotionY = e.getY();
//                if (!mIsBeingDragged) {
//                    if (/*xDiff > mTouchSlop && */xDiff * 0.5f > yDiff && canScrollHorizontally()) {
//                        getParent().requestDisallowInterceptTouchEvent(true);
//                        mIsBeingDragged = true;
//                    } else {
//                        LogUtils.e("[xDiff:" + xDiff + "][mTouchSlop:" + mTouchSlop
//                                + "][xDiff * 0.5f:" + (xDiff * 0.5f) + "][yDiff:" + yDiff + "]");
//                        getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                }
//                break;
//        }
//        return super.onInterceptTouchEvent(e);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            LogUtils.e("onTouchEvent:" + MotionEvent.actionToString(e.getAction()));
////        }
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastMotionX = e.getX();
//                mLastMotionY = e.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (!mIsBeingDragged) {
//                    final float x = e.getX();
//                    final float xDiff = Math.abs(x - mLastMotionX);
//                    final float y = e.getY();
//                    final float yDiff = Math.abs(y - mLastMotionY);
//                    mLastMotionX = e.getX();
//                    mLastMotionY = e.getY();
//                    if (xDiff > mTouchSlop && xDiff > yDiff && canScrollHorizontally()) {
//                        getParent().requestDisallowInterceptTouchEvent(true);
//                        mIsBeingDragged = true;
//                    } else {
//                        LogUtils.e("[xDiff:" + xDiff + "][mTouchSlop:" + mTouchSlop
//                                + "][yDiff:" + yDiff + "]");
//                        getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                }
//                break;
//        }
//        return super.onTouchEvent(e);
//    }
//
//    private boolean canScrollHorizontally() {
//        boolean ret = canScrollHorizontally(-1) || canScrollHorizontally(1);
//        return true;
//    }
}
