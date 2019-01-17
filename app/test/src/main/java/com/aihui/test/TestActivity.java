package com.aihui.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

public class TestActivity extends Activity {

    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = findViewById(R.id.text_view);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mTvContent.setText("screen w:" + displayMetrics.widthPixels + " h:" + displayMetrics.heightPixels + " density:" + displayMetrics.scaledDensity);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mTvContent.append("\nscreen w:" + dm.widthPixels + " h:" + dm.heightPixels + " density:" + dm.scaledDensity);
        Display display = getWindowManager().getDefaultDisplay();
        mTvContent.append("\nscreen w:" + display.getWidth() + " h:" + display.getHeight());
        Point point = new Point();
        display.getRealSize(point);
        mTvContent.append("\nscreen w:" + point.x + " h:" + point.y);
//        mTvContent.append("\nscreen w:" + DensityUtils.getScreenWidth(this) + " h:" + DensityUtils.getScreenHeight(this));
//        mTvContent.append("\nscreen status bar:" + SystemUIUtils.getStatusBarHeight(this));
    }

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        display.getSize(out);
        return out;
    }

//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.button0:
////                Intent intent = new Intent();
//                //hospitalCode:118 patientCode:730 bedCode:1815 deviceId:53777
////                intent.setData(Uri.parse("aihui://th_push/info?hospitalCode=118&patientCode=730&bedCode=1815&deviceId=53777"));
////                intent.setAction(Intent.ACTION_VIEW);
////                startActivity(intent);
////                startService(intent);
//                break;
//            case R.id.button1:
//                GetuiManager.getPushTransmissionObservable(this, "1000000010208001807", "hello")
//                        .subscribe(new BaseObserver<QueryGetuiPushBean>() {
//                            @Override
//                            public void onNext(QueryGetuiPushBean bean) {
//
//                            }
//                        });
//                break;
//            case R.id.button2:
//                ExtFileUtils.openDocument(this, 110);
//                try {
//                    LogUtils.e(DiskUtils.getFileSize(new File("/storage/emulated/0/Download")));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.button3:
//                ExtFileUtils.openImage(this, 110);
//                break;
//        }
//    }
}
