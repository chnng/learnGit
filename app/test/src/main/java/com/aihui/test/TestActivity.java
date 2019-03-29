package com.aihui.test;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.TextView;

public class TestActivity extends Activity {

    private TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = findViewById(R.id.text_view);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        mTvContent.append("\nConfiguration");
        mTvContent.append("\nscreen widthDp:" + configuration.screenWidthDp + " heightDp:" + configuration.screenHeightDp
                + " smallestWidthDp:" + configuration.smallestScreenWidthDp + " densityDpi:" + configuration.densityDpi);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        mTvContent.append("\nDisplayMetrics");
        mTvContent.append("\nscreen w:" + displayMetrics.widthPixels + " h:" + displayMetrics.heightPixels + " density:" + displayMetrics.scaledDensity);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        mTvContent.append("\nDefaultDisplay");
        mTvContent.append("\nscreen w:" + display.getWidth() + " h:" + display.getHeight());
        display.getMetrics(dm);
        mTvContent.append("\nMetrics");
        mTvContent.append("\nscreen w:" + dm.widthPixels + " h:" + dm.heightPixels + " density:" + dm.scaledDensity);
        display.getRealMetrics(dm);
        mTvContent.append("\nRealMetrics");
        mTvContent.append("\nscreen w:" + dm.widthPixels + " h:" + dm.heightPixels + " density:" + dm.scaledDensity);
        Point point = new Point();
        display.getSize(point);
        mTvContent.append("\nSize");
        mTvContent.append("\nscreen w:" + point.x + " h:" + point.y);
        display.getRealSize(point);
        mTvContent.append("\nRealSize");
        mTvContent.append("\nscreen w:" + point.x + " h:" + point.y);
    }
}
