package com.aihui.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.aihui.lib.base.util.DiskUtils;
import com.aihui.lib.base.util.ExtFileUtils;
import com.aihui.lib.base.util.LogUtils;

import java.io.File;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = findViewById(R.id.text_view);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button0:
//                Intent intent = new Intent();
                //hospitalCode:118 patientCode:730 bedCode:1815 deviceId:53777
//                intent.setData(Uri.parse("aihui://th_push/info?hospitalCode=118&patientCode=730&bedCode=1815&deviceId=53777"));
//                intent.setAction(Intent.ACTION_VIEW);
//                startActivity(intent);
//                startService(intent);
                break;
            case R.id.button1:
//                GetuiManager.getPushTransmissionObservable(this, "1000000010208001807", "hello")
//                        .subscribe(new BaseObserver<QueryGetuiPushBean>() {
//                            @Override
//                            public void onNext(QueryGetuiPushBean bean) {
//
//                            }
//                        });
                break;
            case R.id.button2:
                ExtFileUtils.openDocument(this, 110);
                try {
                    LogUtils.e(DiskUtils.getFileSize(new File("/storage/emulated/0/Download")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button3:
                ExtFileUtils.openImage(this, 110);
                break;
        }
    }
}
