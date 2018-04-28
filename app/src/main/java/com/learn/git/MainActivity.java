package com.learn.git;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.learn.git.okhttp.OkHttpEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.OkioKt;

public class MainActivity extends AppCompatActivity {
  OkHttpClient okHttpClient;

  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    okHttpClient = new OkHttpClient.Builder()
                       .eventListener(new OkHttpEventListener())
                       .build();
    mTextView = findViewById(R.id.textView);
//    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//    sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//    TimeZone timeZone = sdf.getTimeZone();
//    String format = sdf.format(new Date());

    Matrix matrix = new Matrix();
    matrix.setScale(1.5f, 1.5f);
    matrix.preTranslate(10, 10);
    mTextView.setText(matrix.toString());
    matrix.reset();
    matrix.setScale(1.5f, 1.5f);
    matrix.postTranslate(10, 10);
    mTextView.append('\n' + matrix.toString());
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
              mTextView.setText("onFailure:" + e.toString() + '\n' +
                                request.headers().toString());
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
      sendNotification();
      break;
    case R.id.button1:
      okHttpClient.dispatcher().cancelAll();
      //      int i = ContextCompat.checkSelfPermission(this,
      //      Manifest.permission.READ_CONTACTS); Toast.makeText(this,
      //              "has permission:" + (i ==
      //              PackageManager.PERMISSION_GRANTED), Toast.LENGTH_SHORT)
      //              .show();
      int i = ContextCompat.checkSelfPermission(
          this, Manifest.permission.RECORD_AUDIO);
      mTextView.append("RECORD_AUDIO:" +
                       (i == PackageManager.PERMISSION_GRANTED));
      Log.d("111aaa",
            "RECORD_AUDIO:" + (i == PackageManager.PERMISSION_GRANTED));
      i = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
      Log.d("111aaa", "CAMERA:" + (i == PackageManager.PERMISSION_GRANTED));
      mTextView.append("CAMERA:" + (i == PackageManager.PERMISSION_GRANTED));
      break;
    }
  }

  public void sendNotification() {
    NotificationManager notificationManager =
        (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager == null) {
      return;
    }
    Intent intent = new Intent();
    intent.setClassName(this, "com.qinhe.ispeak.AppStartActivity");
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    intent.setAction(Intent.ACTION_MAIN);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    PendingIntent pendingIntent = PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, null)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                                       R.mipmap.ic_launcher))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("中文")
            .setContentText("中文")
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true);
    notificationManager.notify(4, builder.build());
  }
}
