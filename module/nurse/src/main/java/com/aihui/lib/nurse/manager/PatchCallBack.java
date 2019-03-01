package com.aihui.lib.nurse.manager;

import com.aihui.lib.base.util.LogUtils;
import com.meituan.robust.Patch;
import com.meituan.robust.RobustCallBack;

import java.util.List;

/**
 * Created by hedingxu on 17/11/26.
 */
public class PatchCallBack implements RobustCallBack {

    @Override
    public void onPatchListFetched(boolean result, boolean isNet, List<Patch> patches) {
        LogUtils.i("onPatchListFetched result: " + result);
        LogUtils.i("onPatchListFetched isNet: " + isNet);
        for (Patch patch : patches) {
            LogUtils.i("onPatchListFetched patch: " + patch.getName());
        }
    }

    @Override
    public void onPatchFetched(boolean result, boolean isNet, Patch patch) {
        LogUtils.i("onPatchFetched result: " + result);
        LogUtils.i("onPatchFetched isNet: " + isNet);
        LogUtils.i("onPatchFetched patch: " + patch.getName());
    }

    @Override
    public void onPatchApplied(boolean result, Patch patch) {
        LogUtils.i("onPatchApplied result: " + result);
        LogUtils.i("onPatchApplied patch: " + patch.getName());

    }

    @Override
    public void logNotify(String log, String where) {
        LogUtils.i("logNotify log: " + log);
        LogUtils.i("logNotify where: " + where);
    }

    @Override
    public void exceptionNotify(Throwable throwable, String where) {
        LogUtils.e("exceptionNotify where: " + where + " " + throwable.getMessage());
    }
}