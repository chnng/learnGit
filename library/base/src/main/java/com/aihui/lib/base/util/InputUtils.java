package com.aihui.lib.base.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by 胡一鸣 on 2018/8/19.
 */
public final class InputUtils {

    public static abstract class SimpleTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public static void hideSoftInput(Activity activity) {
        if (activity == null) {
            return;
        }
        hideSoftInput(activity.getWindow().getDecorView());
    }

    public static void hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) v
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @NonNull
    public static InputFilter[] getSingleLineInputFilters() {
        return new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            if(source.equals(" ") || source.toString().contentEquals("\n")) {
                return "";
            } else {
                return null;
            }
        }};
    }
}
