package com.learn.git.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.learn.git.R;
import com.learn.git.api.glide.GlideApp;
import com.learn.git.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class GlideFragment extends BaseFragment {
    @BindView(R.id.imageView_response)
    ImageView imageView;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @Override
    public void onCreate() {

    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                GlideApp.with(this)
                        .load("https://m.360buyimg.com/babel/jfs/t24064/296/860700114/93455/937aca0c/5b46c2a6N2bfcef4b.jpg")
                        .customOption()
//                        .apply(RequestOptions.circleCropTransform())
//                        .transform(new CropCircleTransformation())
                        .into(imageView);
                break;
            case R.id.button_cancel:
                GlideApp.with(this).clear(imageView);
                break;
        }
    }
}
