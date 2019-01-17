package com.learn.git;

import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.ifly.IFlyManager;

/**
 * Created by 胡一鸣 on 2018/9/9.
 */
public class MyApplication extends BaseApplication {
    @Override
    protected void initData() {
        IFlyManager.init(this, true);
    }
}
