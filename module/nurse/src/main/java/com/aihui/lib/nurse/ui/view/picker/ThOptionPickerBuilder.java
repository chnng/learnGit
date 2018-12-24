package com.aihui.lib.nurse.ui.view.picker;

import android.content.Context;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;

import java.lang.reflect.Field;

/**
 * Created by 胡一鸣 on 2018/8/19.
 */
public class ThOptionPickerBuilder extends OptionsPickerBuilder {
    public ThOptionPickerBuilder(Context context, OnOptionsSelectListener listener) {
        super(context, listener);
    }

    @Override
    public <T> ThOptionPickerView<T> build() {
        try {
            Field field = OptionsPickerBuilder.class.getDeclaredField("mPickerOptions");
            field.setAccessible(true);
            Object obj = field.get(this);
            if (obj instanceof PickerOptions) {
                return new ThOptionPickerView<>(((PickerOptions) obj));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ThOptionPickerView<>(new PickerOptions(PickerOptions.TYPE_PICKER_OPTIONS));
    }
}
