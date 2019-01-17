package com.learn.git.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.aihui.lib.base.util.LogUtils;
import com.learn.git.R;
import com.learn.git.ui.common.MyFragment;

import java.util.List;

import androidx.annotation.NonNull;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionFragment extends MyFragment implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private static final int RC_SMS_PERM = 122;

    private static final String[] LOCATION_AND_CONTACTS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};

    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_LOCATION_CONTACTS_PERM = 124;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_permission;
    }

    @OnClick({R.id.button_sms, R.id.button_camera, R.id.button_contact})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sms:
                smsTask();
                break;
            case R.id.button_camera:
                cameraTask();
                break;
            case R.id.button_contact:
                locationAndContactsTask();
                break;
        }
    }

    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(getContext(), Manifest.permission.CAMERA);
    }

    private boolean hasLocationAndContactsPermissions() {
        return EasyPermissions.hasPermissions(getContext(), LOCATION_AND_CONTACTS);
    }

    private boolean hasSmsPermission() {
        return EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_SMS);
    }

    private boolean hasStoragePermission() {
        return EasyPermissions.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @AfterPermissionGranted(RC_SMS_PERM)
    private void smsTask() {
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_SMS)) {
            // Have permission, do the thing!
            Toast.makeText(getActivity(), "TODO: SMS things", Toast.LENGTH_LONG).show();
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, "sms",
                    RC_SMS_PERM, Manifest.permission.READ_SMS);
        }
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (hasCameraPermission()) {
            // Have permission, do the thing!
            Toast.makeText(getContext(), "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "camera",
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void locationAndContactsTask() {
        if (hasLocationAndContactsPermissions()) {
            // Have permissions, do the thing!
            Toast.makeText(getContext(), "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                   "location",
                    RC_LOCATION_CONTACTS_PERM,
                    LOCATION_AND_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtils.e("onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtils.e("onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setTitle("permission").setRationale("没有权限").build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        LogUtils.e("onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        LogUtils.e("onRationaleDenied:" + requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            LogUtils.e("onActivityResult:" + requestCode + " camera:" + hasCameraPermission()
                    + " location:" + hasLocationAndContactsPermissions()
                    + " sms:" + hasSmsPermission());
//            String yes = getString(R.string.yes);
//            String no = getString(R.string.no);
//
//            // Do something after user returned from app settings screen, like showing a Toast.
//            Toast.makeText(
//                    this,
//                    getString(R.string.returned_from_app_settings_to_activity,
//                            hasCameraPermission() ? yes : no,
//                            hasLocationAndContactsPermissions() ? yes : no,
//                            hasSmsPermission() ? yes : no),
//                    Toast.LENGTH_LONG)
//                    .show();
        }
    }

}
