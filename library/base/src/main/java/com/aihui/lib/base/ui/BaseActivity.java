package com.aihui.lib.base.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.api.eventbus.EventTag;
import com.aihui.lib.base.app.IBaseComponent;
import com.aihui.lib.base.ui.patch.AndroidBug5497Workaround;
import com.aihui.lib.base.util.ApplicationUtils;
import com.aihui.lib.base.util.SystemUIUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by 路传涛 on 2017/5/24.
 */

public abstract class BaseActivity extends RxAppCompatActivity
        implements IBaseComponent, EasyPermissions.PermissionCallbacks {

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int contentViewId = getContentViewId();
        if (!setUniqueContent(contentViewId)) {
            setContentView(contentViewId);
        }
        //setStatusBarTint();
        registerApi();
        initData();
        initEvent();
        if (SystemUIUtils.navigationBarHide) {
            AndroidBug5497Workaround.assistActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (!shouldHideNavigationBar()) {
                    SystemUIUtils.showNavigationBar(getWindow());
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                hideNavigationBar();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    /**
     * 绑定api
     */
    private void registerApi() {
        EventBus.getDefault().register(this);
        mUnbinder = ButterKnife.bind(this);
    }

    /**
     * 解绑api
     */
    private void unregisterApi() {
        EventBus.getDefault().unregister(this);
        mUnbinder.unbind();
    }

    /**
     * 注册EventBus的Activity 必须实现 EventBus其中的一个方法
     * 这里实现一个EventBus中的方法，不需要的子类可以不必再实现
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage event) {
        switch (event.getKey()) {
            case EventTag.KEYBOARD_HIDDEN:
                hideNavigationBar();
                break;
            default:
                break;
        }
    }

//    /**
//     * 设置状态栏的颜色
//     */
//    private void setStatusBarTint() {
//        if (ApplicationUtils.isKitkat()) {
//            setTranslucentStatus(true);
//        }
//
//        SystemBarTintManager tintManager = new SystemBarTintManager(this);
//        tintManager.setStatusBarTintEnabled(true);
//        tintManager.setStatusBarTintResource(R.color.colorPrimary);
//    }
//
//    /**
//     * 设置状态栏颜色透明
//     *
//     * @param on
//     */
//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public void hideNavigationBar() {
        if (shouldHideNavigationBar()) {
            // 全屏主题隐藏导航栏
            SystemUIUtils.hideNavigationBar(this);
        }
    }

    private boolean shouldHideNavigationBar() {
        return SystemUIUtils.navigationBarHide
                && (((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0)
                || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private long exitTime;

    /**
     * 连续按两次返回键退出程序
     */
    protected void exitBy2Click() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.toast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    protected boolean setUniqueContent(@LayoutRes int contentViewId) {
        return contentViewId == 0;
    }

    protected void setStatusBarColor(@ColorRes int colorId) {
        if (ApplicationUtils.isLollipop()) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorId));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
