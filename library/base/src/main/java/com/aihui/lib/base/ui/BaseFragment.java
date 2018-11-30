package com.aihui.lib.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.app.IBaseComponent;
import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by 胡一鸣 on 2018/6/22.
 */
public abstract class BaseFragment extends RxFragment
        implements IBaseComponent, EasyPermissions.PermissionCallbacks {

    private Context mContext;
    private Unbinder mViewBinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View view = null;
        int contentViewId = getContentViewId();
        if (contentViewId != 0) {
            view = inflater.inflate(contentViewId, container, false);
            mViewBinder = ButterKnife.bind(this, view);
        }
        initData();
        initEvent();
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mViewBinder != null) {
            mViewBinder.unbind();
        }
    }

    /**
     * 注册EventBus的Activity 必须实现 EventBus其中的一个方法
     * 这里实现一个EventBus中的方法，不需要的子类可以不必再实现
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMessage event) {
    }

    @NonNull
    @Override
    public Context getContext() {
        return mContext == null ? BaseApplication.getContext() : mContext;
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
