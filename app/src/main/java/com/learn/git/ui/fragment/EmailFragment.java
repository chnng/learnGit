package com.learn.git.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.learn.git.R;
import com.learn.git.ui.common.MyFragment;
import com.shidian.mail.JavaMailUtils;
import com.shidian.mail.MailSender;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 胡一鸣 on 2018/8/23.
 */
public class EmailFragment extends MyFragment {
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
                sendEmail();
                break;
            case R.id.button_cancel:
                break;
        }
    }

    private void sendEmail() {
        JavaMailUtils.send("269823446@qq.com", "Hello from JavaMail", "</>",
                new MailSender.OnSendListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }
}