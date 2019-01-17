package com.aihui.lib.base.ui.patch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.OverScroller;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Field;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

/**
 * Created by 胡一鸣 on 2018/10/11.
 * https://blog.csdn.net/u012556114/article/details/81475309
 */
public class FixBounceV26Behavior extends AppBarLayout.Behavior {
    private OverScroller mScroller;

    public FixBounceV26Behavior() {
        super();
    }

    public FixBounceV26Behavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        getParentScroller(context);
    }

    /**
     * 反射获得滑动属性。
     *
     * @param context
     */
    private void getParentScroller(Context context) {
        if (mScroller != null) return;
        mScroller = new OverScroller(context);
        Field fieldScroller = null;
        Class<?> reflex_class = getClass().getSuperclass().getSuperclass();//父类AppBarLayout.Behavior父类的父类HeaderBehavior
        try {
            fieldScroller = reflex_class.getDeclaredField("scroller");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            try {
                fieldScroller = reflex_class.getDeclaredField("mScroller");
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
        }
        if (fieldScroller != null) {
            try {
                fieldScroller.setAccessible(true);
                fieldScroller.set(this, mScroller);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //fling上滑appbar然后迅速fling下滑recycler时, HeaderBehavior的mScroller并未停止, 会导致上下来回晃动
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        if (mScroller != null) { //当recyclerView 做好滑动准备的时候 直接干掉Appbar的滑动
            if (mScroller.computeScrollOffset()) {
                mScroller.abortAnimation();
            }
        }
        if (type == ViewCompat.TYPE_NON_TOUCH && getTopAndBottomOffset() == 0) { //recyclerview的惯性比较大 ,会顶在头部一会儿, 到头直接干掉它的滑动
            ViewCompat.stopNestedScroll(target, type);
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }
}
