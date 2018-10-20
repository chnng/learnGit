package com.learn.git.ui.fragment;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.learn.git.R;
import com.learn.git.api.okhttp.OkHttpClientHelper;
import com.learn.git.ui.common.MyFragment;

import java.io.IOException;

import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpFragment extends MyFragment {

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                final Request request = new Request.Builder().url("http://httpbin.org/delay/5")
                        .build();
                OkHttpClientHelper.get().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                        Log.d("MainActivity", "onFailure:" + Thread.currentThread().getName());
                        tvResponse.post(() -> tvResponse.setText("onFailure:" + e.toString() +
                                '\n' + request.headers().toString()));
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws
                            IOException {
                        Log.d("MainActivity", "onResponse:" + Thread.currentThread().getName());
                        final String result = response.body().string();
                        tvResponse.post(() -> tvResponse.setText("onResponse:" + result));
                    }
                });
                break;
            case R.id.button_cancel:
                OkHttpClientHelper.get().dispatcher().cancelAll();
                break;
        }
    }
}
