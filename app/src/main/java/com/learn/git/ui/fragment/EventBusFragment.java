package com.learn.git.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.learn.git.R;
import com.learn.git.api.glide.GlideApp;
import com.learn.git.consant.MessageCons;
import com.learn.git.api.eventbus.MessageEvent;
import com.learn.git.ui.base.BaseFragment;
import com.learn.git.util.HandlerUtil;
import com.learn.git.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class EventBusFragment extends BaseFragment {
    @BindView(R.id.textView_response)
    TextView tvResponse;


    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @Override
    public void onCreate() {
        new Thread(() -> EventBus.getDefault().post(new MessageEvent.Builder<>()
                .what(MessageCons.TEST_1)
                .obj("fragment create")
                .build())).start();
    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                HandlerUtil.getUIHandler().post(() -> {
                    ToastUtil.toast("button_request");
                    tvResponse.setText("" + Math.random());
                });
                break;
            case R.id.button_cancel:
                break;
        }
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        switch (event.what) {
            case MessageCons.TEST_0:
                new Thread(() -> ToastUtil.toast(event.obj.toString())).start();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HandlerUtil.clear();
    }
}
