package com.aihui.lib.base.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.aihui.lib.base.R;
import com.aihui.lib.base.app.IBaseComponent;

import butterknife.ButterKnife;

/**
 * Created by 胡一鸣 on 2018/7/30.
 */
public abstract class BaseDialog extends Dialog implements IBaseComponent {
    public BaseDialog(@NonNull Context context) {
        super(context, R.style.AppDialog_Transparent);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        initData();
    }

    @Override
    public void initEvent() {
    }
}
