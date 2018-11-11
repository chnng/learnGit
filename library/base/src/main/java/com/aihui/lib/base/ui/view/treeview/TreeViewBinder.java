package com.aihui.lib.base.ui.view.treeview;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class TreeViewBinder<VH extends RecyclerView.ViewHolder> implements LayoutItemType {
    public abstract VH provideViewHolder(View itemView);

    public abstract void bindView(VH holder, int position, TreeNode node);
}