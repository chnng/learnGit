package com.aihui.lib.nurse.util;

import android.content.Context;
import android.graphics.Color;

import com.aihui.lib.nurse.R;
import com.aihui.lib.nurse.ui.view.picker.ThOptionPickerBuilder;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;

/**
 * Created by 胡一鸣 on 2018/8/7.
 */
public final class PickerUtils {
    public static OptionsPickerBuilder getOptionPickerBuilder(Context context, OnOptionsSelectListener listener) {
        return new ThOptionPickerBuilder(context, listener)
                //                .setTitleText("城市选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.GRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.LTGRAY)
                .setTitleBgColor(Color.WHITE)
//                .setTitleColor(Color.LTGRAY)
                .setCancelColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .setSubmitColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .setTextColorCenter(Color.GRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .setLabels("省", "市", "区")
                .setBackgroundId(context.getResources().getColor(R.color.shadow)); //设置外部遮罩颜色
//                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
//                    @Override
//                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
//                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
////                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
//                        LogUtils.e("onOptionsSelectChanged:" + str);
//                    }
//                });
    }

    public static TimePickerBuilder getTimePickerBuilder(Context context, OnTimeSelectListener listener) {
        return new TimePickerBuilder(context, listener)
//                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
//                    @Override
//                    public void onTimeSelectChanged(Date date) {
//                        Log.i("pvTime", "onTimeSelectChanged");
//                    }
//                })
                .setType(new boolean[]{false, false, false, true, true, true});
    }
}
