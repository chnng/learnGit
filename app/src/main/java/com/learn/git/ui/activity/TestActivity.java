package com.learn.git.ui.activity;

import android.app.ProgressDialog;
import android.os.Handler;

import com.aihui.lib.base.ui.BaseActivity;


/**
 * Created by 胡一鸣 on 2018/8/19.
 */
public class TestActivity extends BaseActivity {

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public void initData() {
        testProgressDialog();
    }

    @Override
    public void initEvent() {

    }

    private void testProgressDialog() {
        Handler handler = new Handler();
        new Thread(() -> {
            ProgressDialog dialog = new ProgressDialog(this);
            handler.post(() -> {
                dialog.show();
            });
            handler.postDelayed(() -> {
                dialog.dismiss();
            }, 4000);
        }).start();
        handler.postDelayed(() -> finish(), 8000);
    }
}
