package com.aihui.lib.base.ui;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.IBaseComponent;

import butterknife.ButterKnife;

/**
 * Created by huyiming on 2017/10/30.
 */

public abstract class BasePopupWindow extends PopupWindow implements IBaseComponent {
    private Activity mActivity;

    public BasePopupWindow(Activity activity) {
        init(activity);
    }

    private void init(Activity activity) {
        mActivity = activity;
        View contentView = LayoutInflater.from(activity).inflate(getContentViewId(), null);
        ButterKnife.bind(this, contentView);
        setContentView(contentView);
        setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.AppPopAnimation);
//        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initData();
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
    }

    public Activity getActivity() {
        return mActivity;
    }

    public String getString(int resId) {
        return mActivity.getResources().getString(resId);
    }

    public String getString(int resId, Object... formatArgs) {
        return mActivity.getResources().getString(resId, formatArgs);
    }

    public void show() {
        showAtLocation(mActivity.getWindow().getDecorView(), 0, 0, Gravity.NO_GRAVITY);
    }
}
