package com.learn.git.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.util.HandlerUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.learn.git.R;
import com.learn.git.app.MessageCons;
import com.learn.git.ui.common.MyFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class EventBusFragment extends MyFragment {
    @BindView(R.id.textView_response)
    TextView tvResponse;


    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

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
                new Thread(() -> EventBus.getDefault()
                        .post(new EventMessage<>(MessageCons.TEST_1, "fragment create")))
                        .start();
                break;
        }
    }

    @Override
    public void onMessage(EventMessage event) {
        super.onMessage(event);
        switch (event.getKey()) {
            case MessageCons.TEST_0:
                new Thread(() -> ToastUtils.toast(event.getValue().toString())).start();
                break;
        }
    }
}
