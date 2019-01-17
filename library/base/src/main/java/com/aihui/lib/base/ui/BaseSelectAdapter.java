package com.aihui.lib.base.ui;

import com.aihui.lib.base.model.common.response.BaseSelectBean;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 胡一鸣 on 2018/9/13.
 */
public abstract class BaseSelectAdapter<T extends BaseSelectBean, VH extends RecyclerView.ViewHolder> extends BaseAdapter<T, VH> {

    public BaseSelectAdapter() {
    }

    public BaseSelectAdapter(List<T> list) {
        super(list);
    }

    public boolean isAllSelected() {
        if (mList == null) {
            return false;
        }
        for (T t : mList) {
            if (!t.isSelected) {
                return false;
            }
        }
        return true;
    }

    public List<T> getSelectedList() {
        if (mList == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (T t : mList) {
            if (t.isSelected) {
                list.add(t);
            }
        }
        return list;
    }

    public void setAllSelected(boolean isSelected) {
        if (mList == null) {
            return;
        }
        for (T t : mList) {
            t.isSelected = isSelected;
        }
        notifyDataSetChanged();
    }
}
