package com.aihui.lib.base.ui.view.treeview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by tlh on 2016/10/1 :)
 * https://github.com/TellH/RecyclerTreeView
 */
public class TreeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String KEY_IS_EXPAND = "IS_EXPAND";
    private final List<? extends TreeViewBinder> viewBinders;
    private List<TreeViewNode> displayNodes;
    private int padding = 30;
    private OnTreeNodeListener onTreeNodeListener;
    private boolean toCollapseChild;

    public TreeViewAdapter(List<? extends TreeViewBinder> viewBinders) {
        this(null, viewBinders);
    }

    public TreeViewAdapter(List<TreeViewNode> nodes, List<? extends TreeViewBinder> viewBinders) {
        displayNodes = new ArrayList<>();
        if (nodes != null) {
            findDisplayNodes(nodes);
        }
        this.viewBinders = viewBinders;
    }

    /**
     * 从nodes的结点中寻找展开了的非叶结点，添加到displayNodes中。
     *
     * @param nodes 基准点
     */
    private void findDisplayNodes(List<TreeViewNode> nodes) {
        for (TreeViewNode node : nodes) {
            displayNodes.add(node);
            if (!node.isLeaf() && node.isExpand())
                findDisplayNodes(node.getChildList());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayNodes.get(position).getContent().getLayoutId();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewBinders.size() == 1) {
            return viewBinders.get(0).provideViewHolder(v);
        }
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinder.getLayoutId() == viewType)
                return viewBinder.provideViewHolder(v);
        }
        return viewBinders.get(0).provideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            Bundle b = (Bundle) payloads.get(0);
            for (String key : b.keySet()) {
                switch (key) {
                    case KEY_IS_EXPAND:
                        if (onTreeNodeListener != null)
                            onTreeNodeListener.onToggle(b.getBoolean(key), holder);
                        break;
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (padding > 0) {
            holder.itemView.setPadding(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        }
        holder.itemView.setOnClickListener(v -> {
            TreeViewNode selectedNode = displayNodes.get(holder.getAdapterPosition());
            if (selectedNode.isLeaf()) {
                return;
            }
            // This TreeViewNode was locked to click.
            if (selectedNode.isLocked()) {
                return;
            }
            // Prevent multi-click during the short interval.
            try {
                long lastClickTime = (long) holder.itemView.getTag();
                if (System.currentTimeMillis() - lastClickTime < 500) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemView.setTag(System.currentTimeMillis());
            if (onTreeNodeListener != null && onTreeNodeListener.onClick(this, selectedNode, holder, position)) {
                return;
            }
            boolean isExpand = selectedNode.isExpand();
            int positionStart = displayNodes.indexOf(selectedNode) + 1;
            if (!isExpand) {
                notifyItemRangeInserted(positionStart, addChildNodes(selectedNode, positionStart));
            } else {
                notifyItemRangeRemoved(positionStart, removeChildNodes(selectedNode, true));
            }
        });
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinder.getLayoutId() == displayNodes.get(position).getContent().getLayoutId()) {
                viewBinder.bindView(holder, position, displayNodes.get(position));
            }
        }
    }

    private int addChildNodes(TreeViewNode pNode, int startIndex) {
        List<TreeViewNode> childList = pNode.getChildList();
        int addChildCount = 0;
//        for (TreeViewNode treeNode : childList) {
//            displayNodes.add(startIndex + addChildCount++, treeNode);
//            if (treeNode.isExpand()) {
//                addChildCount += addChildNodes(treeNode, startIndex + addChildCount);
//            }
//        }
        displayNodes.addAll(startIndex, childList);
        addChildCount = childList.size();
        if (!pNode.isExpand())
            pNode.toggle();
        return addChildCount;
    }

    private int removeChildNodes(TreeViewNode pNode) {
        return removeChildNodes(pNode, true);
    }

    private int removeChildNodes(TreeViewNode pNode, boolean shouldToggle) {
        if (pNode.isLeaf())
            return 0;
        List<TreeViewNode> childList = pNode.getChildList();
        int removeChildCount = childList.size();
        displayNodes.removeAll(childList);
        for (TreeViewNode child : childList) {
            if (child.isExpand()) {
                if (toCollapseChild)
                    child.toggle();
                removeChildCount += removeChildNodes(child, false);
            }
        }
//        if (shouldToggle)
//            pNode.toggle();
        pNode.collapse();
        return removeChildCount;
    }

    public int getPositionStart(TreeViewNode pNode) {
        return displayNodes.indexOf(pNode) + 1;
    }

    public int getExpandChildCount(TreeViewNode pNode) {
        int expandChildCount = 0;
        List<TreeViewNode> childList = pNode.getChildList();
        for (TreeViewNode treeViewNode : childList) {
            expandChildCount++;
            if (treeViewNode.isExpand()) {
                expandChildCount += getExpandChildCount(treeViewNode);
            }
        }
        return expandChildCount;
    }

    @Override
    public int getItemCount() {
        return displayNodes == null ? 0 : displayNodes.size();
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void ifCollapseChildWhileCollapseParent(boolean toCollapseChild) {
        this.toCollapseChild = toCollapseChild;
    }

    public void setOnTreeNodeListener(OnTreeNodeListener onTreeNodeListener) {
        this.onTreeNodeListener = onTreeNodeListener;
    }

    public interface OnTreeNodeListener {
        /**
         * called when TreeNodes were clicked.
         *
         * @return weather consume the click event.
         */
        boolean onClick(TreeViewAdapter adapter, TreeViewNode node, RecyclerView.ViewHolder holder, int position);

        /**
         * called when TreeNodes were toggle.
         *
         * @param isExpand the status of TreeNodes after being toggled.
         */
        void onToggle(boolean isExpand, RecyclerView.ViewHolder holder);
    }

    public void refresh(List<TreeViewNode> treeViewNodes) {
        displayNodes.clear();
        if (treeViewNodes != null) {
            findDisplayNodes(treeViewNodes);
        }
        notifyDataSetChanged();
    }

    public Iterator<TreeViewNode> getDisplayNodesIterator() {
        return displayNodes.iterator();
    }

    private void notifyDiff(final List<TreeViewNode> temp) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return temp.size();
            }

            @Override
            public int getNewListSize() {
                return displayNodes.size();
            }

            // judge if the same items
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return TreeViewAdapter.this.areItemsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            // if they are the same items, whether the contents has bean changed.
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return TreeViewAdapter.this.areContentsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return TreeViewAdapter.this.getChangePayload(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }
        });
        diffResult.dispatchUpdatesTo(this);
    }

    private Object getChangePayload(TreeViewNode oldNode, TreeViewNode newNode) {
        Bundle diffBundle = new Bundle();
        if (newNode.isExpand() != oldNode.isExpand()) {
            diffBundle.putBoolean(KEY_IS_EXPAND, newNode.isExpand());
        }
        if (diffBundle.size() == 0) {
            return null;
        }
        return diffBundle;
    }

    // For DiffUtil, if they are the same items, whether the contents has bean changed.
    private boolean areContentsTheSame(TreeViewNode oldNode, TreeViewNode newNode) {
        return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent())
                && oldNode.isExpand() == newNode.isExpand();
    }

    // judge if the same item for DiffUtil
    private boolean areItemsTheSame(TreeViewNode oldNode, TreeViewNode newNode) {
        return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent());
    }

    @NonNull
    private List<TreeViewNode> backupDisplayNodes() {
        List<TreeViewNode> temp = new ArrayList<>();
        for (TreeViewNode displayNode : displayNodes) {
            temp.add(displayNode.clone());
        }
        return temp;
    }

    /**
     * collapse all root nodes.
     */
    public void collapseAll() {
        // Back up the nodes are displaying.
        List<TreeViewNode> temp = backupDisplayNodes();
        //find all root nodes.
        List<TreeViewNode> roots = new ArrayList<>();
        for (TreeViewNode displayNode : displayNodes) {
            if (displayNode.isRoot())
                roots.add(displayNode);
        }
        //Close all root nodes.
        for (TreeViewNode root : roots) {
            if (root.isExpand()) {
                removeChildNodes(root);
            }
        }
        notifyDiff(temp);
    }

    public void collapseNode(TreeViewNode pNode) {
        List<TreeViewNode> temp = backupDisplayNodes();
        removeChildNodes(pNode);
        notifyDiff(temp);
    }

    public void collapseBrotherNode(TreeViewNode pNode) {
        List<TreeViewNode> temp = backupDisplayNodes();
        if (pNode.isRoot()) {
            List<TreeViewNode> roots = new ArrayList<>();
            for (TreeViewNode displayNode : displayNodes) {
                if (displayNode.isRoot()) {
                    roots.add(displayNode);
                }
            }
            //Close all root nodes.
            for (TreeViewNode root : roots) {
                if (root.isExpand() && !root.equals(pNode)) {
                    removeChildNodes(root);
                }
            }
        } else {
            TreeViewNode parent = pNode.getParent();
            if (parent == null) {
                return;
            }
            List<TreeViewNode> childList = parent.getChildList();
            for (TreeViewNode node : childList) {
                if (node.equals(pNode) || !node.isExpand()) {
                    continue;
                }
                removeChildNodes(node);
            }
        }
        notifyDiff(temp);
    }

}