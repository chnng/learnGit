package com.learn.git.ui.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.learn.git.R;
import com.learn.git.api.glide.GlideApp;
import com.learn.git.ui.base.BaseFragment;
import com.learn.git.util.ToastUtil;

import java.util.Calendar;

import butterknife.OnClick;

/**
 * Created by 胡一鸣 on 2018/8/5.
 */
public class PickerFragment extends BaseFragment {
    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @Override
    public void onCreate() {

    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.button_request:
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.AppDialog, (view, hourOfDay, minute) ->
                        ToastUtil.toast("onTimeSet hourOfDay:" + hourOfDay + " minute:" + minute), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
                break;
            case R.id.button_cancel:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.AppDialog, (view, year, month, dayOfMonth) ->
                        ToastUtil.toast("onDateSet year:" + year + " month:" + month + " dayOfMonth:" + dayOfMonth), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }
    }
}
