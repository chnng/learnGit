package com.aihui.lib.base.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aihui.lib.base.util.HandlerUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by 胡一鸣 on 2018/7/31.
 */
public abstract class BaseLazyFragment extends BaseFragment {

    private boolean mIsCreated, mIsInit;

    protected abstract void initDataLazy();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsCreated = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initData() {
        if (getUserVisibleHint() && !mIsInit) {
            HandlerUtils.post(() -> {
                /*
                    post double check for ViewPager setCurrentItem position is not 0!
                    getUserVisibleHint() will change to false soon
                */
                if (getUserVisibleHint()) {
                    mIsInit = true;
                    initDataLazy();
                }
            });
        }
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !mIsInit && mIsCreated) {
            mIsInit = true;
            initDataLazy();
        }
    }

    protected boolean isInitiated() {
        return mIsInit;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsCreated = mIsInit = false;
    }
}
