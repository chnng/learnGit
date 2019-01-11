package com.learn.git.ui.fragment;

import android.view.View;

import com.aihui.lib.ifly.IFlyManager;
import com.aihui.lib.ifly.SimpleRecognizerListener;
import com.learn.git.R;
import com.learn.git.ui.common.MyFragment;

import butterknife.OnClick;

/**
 * Created by 胡一鸣 on 2019/1/10.
 */
public class IFlyFragment extends MyFragment {

    @Override
    public void initData() {
        super.initData();
        tvResponse.setText("测试test");
    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                IFlyManager.startListening(new SimpleRecognizerListener() {
                    @Override
                    protected void onResult(String result) {
                        tvResponse.setText(result);
                    }
                });
                break;
            case R.id.button_cancel:
                IFlyManager.startSpeaking(tvResponse.getText().toString());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IFlyManager.destroy();
    }
}
