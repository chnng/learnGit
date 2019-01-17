package com.aihui.lib.base.app;

import androidx.annotation.LayoutRes;

/**
 * Created by 胡一鸣 on 2018/7/30.
 */
public interface IBaseComponent {
    /**
     * 设置布局ID
     *
     * @return contentViewId
     */
    @LayoutRes
    int getContentViewId();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化事件
     */
    void initEvent();
}
