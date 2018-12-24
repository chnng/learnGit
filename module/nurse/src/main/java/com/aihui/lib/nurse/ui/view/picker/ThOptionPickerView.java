package com.aihui.lib.nurse.ui.view.picker;

import android.app.Activity;
import android.content.Context;

import com.aihui.lib.base.util.SystemUIUtils;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.view.OptionsPickerView;

/**
 * Created by 胡一鸣 on 2018/8/19.
 */
public class ThOptionPickerView<T> extends OptionsPickerView<T> {

    private Activity mActivity;

    public ThOptionPickerView(PickerOptions pickerOptions) {
        super(pickerOptions);
        Context context = getDialogContainerLayout().getContext();
        if (SystemUIUtils.navigationBarHide && context instanceof Activity) {
            mActivity = (Activity) context;
            setOnDismissListener(o -> SystemUIUtils.showNavigationBar((mActivity.getWindow())));
        }
    }

    @Override
    public void show() {
        super.show();
        if (SystemUIUtils.navigationBarHide && mActivity != null) {
            SystemUIUtils.hideNavigationBar(mActivity);
        }
    }
}
