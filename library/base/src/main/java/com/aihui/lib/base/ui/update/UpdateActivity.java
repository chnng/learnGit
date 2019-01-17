package com.aihui.lib.base.ui.update;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aihui.lib.base.R;
import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.api.eventbus.EventTag;
import com.aihui.lib.base.cons.RequestCode;
import com.aihui.lib.base.model.common.response.CheckUpdateBean;
import com.aihui.lib.base.ui.BaseActivity;
import com.aihui.lib.base.util.ApplicationUtils;
import com.aihui.lib.base.util.CheckUtils;
import com.aihui.lib.base.util.StringUtils;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by 胡一鸣 on 2018/8/16.
 * 1.处理android8.0安装apk权限问题
 * 2.处理dialog宿主activity的finish问题
 */
public class UpdateActivity extends BaseActivity {

    private UpdateService.UpdateBinder mBinder;
    private ServiceConnection mServiceConnection;
    private MaterialDialog mUpdateDialog;
    private MaterialDialog mProgressDialog;

    private String mApkPath;

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public void initData() {
        CheckUpdateBean updateBean = getIntent().getParcelableExtra("checkUpdate");
        if (updateBean != null) {
            showUpdateDialog(updateBean);
        } else {
            mApkPath = getIntent().getStringExtra("apkPath");
            installApk(true);
        }
    }

    @Override
    public void initEvent() {
    }

    private void installApk(boolean isRequestPermission) {
        if (TextUtils.isEmpty(mApkPath)) {
            finish();
        } else {
            if (ApplicationUtils.isOreo() && !getPackageManager().canRequestPackageInstalls()) {
                if (isRequestPermission) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, RequestCode.RESULT_INSTALL_APK);
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("安装授权失败是否重新尝试")
                            .setNegativeButton("否", null)
                            .setPositiveButton("是", (dialog, which) -> installApk(true))
                            .show();
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                FileProvider7.setIntentDataAndType(this, intent,
                        "application/vnd.android.package-archive", new File(mApkPath), true);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCode.RESULT_INSTALL_APK:
                installApk(false);
                break;
        }
    }

    @Override
    public void onMessage(EventMessage event) {
        super.onMessage(event);
        switch (event.getKey()) {
            case EventTag.EVENT_UPDATE_DOWNLOAD_PROGRESS:
                if (mProgressDialog != null) {
                    mProgressDialog.setProgress((Integer) event.getValue());
                }
                break;
            case EventTag.EVENT_UPDATE_DOWNLOAD_RESULT:
                if (mProgressDialog != null) {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.cancel();
                    }
                }
                Object value = event.getValue();
                if (value != null) {
                    mApkPath = String.valueOf(value);
                    installApk(true);
                }
                break;
        }
    }

    /**
     * 更新提示框
     *
     * @param bean bean
     */
    private void showUpdateDialog(CheckUpdateBean bean) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_update_dialog, null);
        TextView tvUpdateDes = dialogView.findViewById(R.id.tv_update_des);
        String fileUrl = bean.down_url;
        String versionName = bean.v_name;
        String content = bean.remark;
        content = StringUtils.unescapeHtml(content);
        //格式化content, | 需要转义
        String[] contents = content.split("\\|");
        StringBuilder des = new StringBuilder();
        for (int i = 0; i < contents.length; i++) {
            if (i == contents.length - 1)
                des.append(i + 1).append(". ").append(contents[i]);
            else
                des.append(i + 1).append(". ").append(contents[i]).append("\n");
        }
        tvUpdateDes.setText(Html.fromHtml(des.toString()));
        mUpdateDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.update_dialog_title, versionName))
                .customView(dialogView, false)
                .positiveText(getString(R.string.update_now))
                .negativeText(getString(R.string.update_later))
                .onAny((dialog, which) -> {
                    switch (which) {
                        case POSITIVE:
                            showUpdateProgressDialog(fileUrl);
                            break;
                        case NEGATIVE:
                            stopService(new Intent(this, UpdateService.class));
                            finish();
                            break;
                    }
                })
                .cancelListener(dialog -> finish())
                .keyListener((dialog, keyCode, event) -> {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            finish();
                            break;
                    }
                    return false;
                })
                .show();
    }

    /**
     * 更新进度提示框
     *
     * @param url url
     */
    private void showUpdateProgressDialog(String url) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = ((UpdateService.UpdateBinder) service);
                mBinder.download(url);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, UpdateService.class), mServiceConnection, BIND_AUTO_CREATE);
        mProgressDialog = new MaterialDialog.Builder(this)
                .content(getString(R.string.updating_des))
                .progress(false, 100, false)
                .positiveText(getString(R.string.update_background))
                .onAny((dialog, which) -> {
                    //后台更新，隐藏进度对话框，通知栏显示更新进度
                    showUpdateNotification();
                })
                .cancelable(false)
                .show();
    }

    private void showUpdateNotification() {
        if (mBinder != null) {
            mBinder.showNotification();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        CheckUtils.dismissDialog(mUpdateDialog);
        CheckUtils.dismissDialog(mProgressDialog);
    }
}
