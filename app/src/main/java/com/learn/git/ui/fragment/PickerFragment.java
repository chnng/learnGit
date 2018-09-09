package com.learn.git.ui.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;

import com.aihui.lib.base.util.ToastUtils;
import com.learn.git.R;
import com.learn.git.ui.common.MyFragment;

import java.util.Calendar;

import butterknife.OnClick;

/**
 * Created by 胡一鸣 on 2018/8/5.
 */
public class PickerFragment extends MyFragment {
    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.button_request:
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.AppDialog, (view, hourOfDay, minute) ->
                        ToastUtils.toast("onTimeSet hourOfDay:" + hourOfDay + " minute:" + minute), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
                break;
            case R.id.button_cancel:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.AppDialog, (view, year, month, dayOfMonth) ->
                        ToastUtils.toast("onDateSet year:" + year + " month:" + month + " dayOfMonth:" + dayOfMonth), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }
    }
}
