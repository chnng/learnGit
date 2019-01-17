package com.aihui.lib.base.ui.patch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.aihui.lib.base.ui.BaseActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.helper.BaseSupportPermissionsHelper;

/**
 * Created by 胡一鸣 on 2018/9/17.
 */
public class PermissionActivity extends BaseActivity {

    private int mRequestCode;
    private String[] mRequestPermissions;
    private String mRationaleTitle;
    private String mRationaleContent;

    public static boolean startPermissionActivity(Context context,
                                               int requestCode,
                                               String rationaleTitle,
                                               String rationaleContent,
                                               String... requestPermissions) {
        if (!EasyPermissions.hasPermissions(context, requestPermissions)) {
            Intent intent = new Intent(context, PermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("requestCode", requestCode);
            intent.putExtra("rationaleTitle", rationaleTitle);
            intent.putExtra("rationaleContent", rationaleContent);
            intent.putExtra("requestPermissions", requestPermissions);
            context.startActivity(intent);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getContentViewId() {
        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra("requestCode", 0);
        if (mRequestCode == 0) {
            mRequestCode = getRequestCode();
        }
        mRequestPermissions = intent.getStringArrayExtra("requestPermissions");
        if (mRequestPermissions == null) {
            mRequestPermissions = getRequestPermissions();
        }
        mRationaleTitle = intent.getStringExtra("rationaleTitle");
        if (mRationaleTitle == null) {
            mRationaleTitle = getRationaleTitle();
        }
        mRationaleContent = intent.getStringExtra("rationaleContent");
        if (mRationaleContent == null) {
            mRationaleContent = getRationaleContent();
        }
        return 0;
    }

    @Override
    public void initData() {
        requestPermissions();
    }

    @Override
    public void initEvent() {

    }

    protected int getRequestCode() {
        return 0;
    }

    protected String[] getRequestPermissions() {
        return null;
    }

    protected String getRationaleTitle() {
        return "需要一些权限";
    }

    protected String getRationaleContent() {
        return "前往设置页设置";
    }

    protected void onPermissions() {
    }

    private void requestPermissions() {
        if (mRequestPermissions == null || EasyPermissions.hasPermissions(this, mRequestPermissions)) {
            setResult(Activity.RESULT_OK);
            onPermissions();
        } else {
            EasyPermissions.requestPermissions(this, mRationaleTitle, mRequestCode, mRequestPermissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == mRequestCode) {
            requestPermissions();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode != mRequestCode) {
            return;
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(mRationaleTitle)
                    .setRationale(mRationaleContent)
                    .build()
                    .show();
        } else {
            /**
             * {@link BaseSupportPermissionsHelper#showRequestPermissionRationale}
             * Found existing fragment, not showing rationale.
             */
            ActivityCompat.requestPermissions(this, perms.toArray(mRequestPermissions), requestCode);
//          requestPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                requestPermissions();
                break;
        }
    }
}
