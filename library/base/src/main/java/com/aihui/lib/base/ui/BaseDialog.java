package com.aihui.lib.base.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.Window;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.IBaseComponent;
import com.aihui.lib.base.util.SystemUIUtils;

import androidx.annotation.NonNull;
import butterknife.ButterKnife;

/**
 * Created by 胡一鸣 on 2018/7/30.
 */
public abstract class BaseDialog extends Dialog implements IBaseComponent {

    private Context mContext;

    public BaseDialog(@NonNull Context context) {
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public BaseDialog(@NonNull Context context, int width, int height) {
        super(context, R.style.AppDialog_Transparent);
        mContext = context;
        setContentView(getContentViewId());
        Window window = getWindow();
        if (window != null) {
            window.setLayout(width, height);
//            window.getDecorView().setPadding(0, SystemUIUtils.getStatusBarHeight(context), 0, 0);
        }
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    @Override
    public void initEvent() {
    }

    public Context getBaseContext() {
        return mContext;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (SystemUIUtils.navigationBarHide) {
//            SystemUIUtils.hideNavigationBar(this);
//        }
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && SystemUIUtils.navigationBarHide) {
            SystemUIUtils.hideNavigationBar(this);
        }
    }
}
