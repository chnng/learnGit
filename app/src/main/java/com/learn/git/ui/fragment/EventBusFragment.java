package com.learn.git.ui.fragment;

import android.view.View;

import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.util.HandlerUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.learn.git.R;
import com.learn.git.cons.EventTag;
import com.learn.git.ui.common.MyFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

public class EventBusFragment extends MyFragment {

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                HandlerUtils.getUIHandler().post(() -> {
                    ToastUtils.toast("button_request");
                    tvResponse.setText("" + Math.random());
                });
                break;
            case R.id.button_cancel:
                new Thread(() -> EventBus.getDefault().post(new EventMessage<>(EventTag
                        .TEST_1, "fragment create"))).start();
                break;
        }
    }

    @Override
    public void onMessage(EventMessage event) {
        super.onMessage(event);
        switch (event.getKey()) {
            case EventTag.TEST_0:
                new Thread(() -> ToastUtils.toast(event.getValue().toString())).start();
                break;
        }
    }
}
