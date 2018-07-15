package com.learn.git.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.learn.git.api.eventbus.MessageEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends RxAppCompatActivity implements IContentView{

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this);
        onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {/* Do something */}
}
