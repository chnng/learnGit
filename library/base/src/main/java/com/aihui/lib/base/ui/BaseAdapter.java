package com.aihui.lib.base.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by 胡一鸣 on 2018/8/13.
 */
public abstract class BaseAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> mList;

    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (this.mList != null) {
            this.mList.addAll(list);
        } else {
            this.mList = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (mList != null && mList.size() > position) {
            T t = mList.get(position);
            if (t != null) {
                onBindViewHolder(holder, position, t);
            }
        }
    }

    public abstract void onBindViewHolder(@NonNull VH holder, int position, @NonNull T bean);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    protected void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }

//    public boolean isAllSelected() {
//        if (mList == null) {
//            return false;
//        }
//        try {
//            for (T t : mList) {
//                if (!((BaseSelectBean) t).isSelected) {
//                    return false;
//                }
//            }
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    public List<T> getSelectedList() {
//        if (mList == null) {
//            return null;
//        }
//        List<T> list = new ArrayList<>();
//        try {
//            for (T t : mList) {
//                if (((BaseSelectBean) t).isSelected) {
//                    list.add(t);
//                }
//            }
//        } catch (ClassCastException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return list;
//    }
}
