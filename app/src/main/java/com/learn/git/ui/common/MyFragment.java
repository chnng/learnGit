package com.learn.git.ui.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aihui.lib.base.ui.BaseFragment;
import com.learn.git.R;

import butterknife.BindView;

/**
 * Created by 胡一鸣 on 2018/9/9.
 */
public abstract class MyFragment extends BaseFragment {
    @BindView(R.id.textView_response)
    protected TextView tvResponse;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getClass().getSimpleName());
        view.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
    }
}
