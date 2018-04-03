package com.learn.git;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.git.okhttp.OkHttpEventListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
  OkHttpClient okHttpClient;

  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    okHttpClient = new OkHttpClient.Builder().eventListener(new OkHttpEventListener()).build();
    mTextView = findViewById(R.id.textView);
  }

  public void onClick(View view) {
    switch (view.getId()) {
    case R.id.button0:
      final Request request =
          new Request.Builder().url("http://httpbin.org/delay/5").build();
      okHttpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, final IOException e) {
          Log.d("MainActivity",
                "onFailure:" + Thread.currentThread().getName());
          mTextView.post(new Runnable() {
            @Override
            public void run() {
              mTextView.setText("onFailure:" + e.toString() + '\n' + request.headers().toString());
            }
          });
        }

        @Override
        public void onResponse(Call call, Response response)
            throws IOException {
          Log.d("MainActivity",
                "onResponse:" + Thread.currentThread().getName());
          final String result = response.body().string();
          mTextView.post(new Runnable() {
            @Override
            public void run() {
              mTextView.setText("onResponse:" + result);
            }
          });
        }
      });
      break;
    case R.id.button1:
      okHttpClient.dispatcher().cancelAll();
      int i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
      Toast.makeText(this,
              "has permission:" + (i == PackageManager.PERMISSION_GRANTED),
              Toast.LENGTH_SHORT)
              .show();
      break;
    }
  }
}
