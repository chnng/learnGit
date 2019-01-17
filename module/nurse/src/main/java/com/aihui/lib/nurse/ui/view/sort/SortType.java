package com.aihui.lib.nurse.ui.view.sort;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by 胡一鸣 on 2018/9/1.
 */
@IntDef({SortType.TYPE_NORMAL, SortType.TYPE_ASC, SortType.TYPE_DESC})
@Retention(RetentionPolicy.SOURCE)
public @interface SortType {
    int TYPE_NORMAL = 0;
    int TYPE_ASC = 1;
    int TYPE_DESC = 2;
}
