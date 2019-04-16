package com.aihui.lib.getui;

import com.aihui.lib.base.cons.RequestCode;
import com.aihui.lib.base.ui.patch.PermissionActivity;

/**
 * Created by 胡一鸣 on 2018/9/17.
 */
public class GetuiPermissionActivity extends PermissionActivity {

    @Override
    protected int getRequestCode() {
        return RequestCode.PERMISSION_GETUI;
    }

    @Override
    protected String[] getRequestPermissions() {
        return GetuiUtils.getPermissionList();
    }

    @Override
    protected void onPermissions() {
        GetuiUtils.initialize(this, GetuiUtils.GETUI_SERVICE_CLASS);
        finish();
    }

    @Override
    protected String getRationaleTitle() {
        return "需要存储权限";
    }
}
