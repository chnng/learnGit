package com.aihui.lib.base.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by 胡一鸣 on 2018/8/13.
 */
public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    List<T> mList;
    private OnItemClickListener<T> mOnItemClickListener;

    public BaseAdapter() {
    }

    public BaseAdapter(List<T> list) {
        this.mList = list;
    }

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
            } else {
                onBindViewHolderNullable(holder, position);
            }
        } else {
            onBindViewHolderNullable(holder, position);
        }
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, int position, @NonNull T bean);

    protected void onBindViewHolderNullable(@NonNull VH holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    protected void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    protected OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    protected void setOnItemClickListener(@NonNull VH holder, T t) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.getAdapterPosition(), t);
                }
            });
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, @NonNull T bean);
    }
}
