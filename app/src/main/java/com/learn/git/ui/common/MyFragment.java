package com.learn.git.ui.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aihui.lib.base.ui.BaseFragment;

/**
 * Created by 胡一鸣 on 2018/9/9.
 */
public abstract class MyFragment extends BaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getClass().getSimpleName());
        view.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
    }
}
