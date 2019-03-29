package com.aihui.lib.base.ui.view.treeview;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by tlh on 2016/10/1 :)
 */
public class TreeViewNode implements Cloneable {
    private LayoutItemType content;
    private TreeViewNode parent;
    private List<TreeViewNode> childList;
    private boolean isExpand;
    private boolean isLocked;
    //the tree high
    private int height = UNDEFINE;

    private static final int UNDEFINE = -1;

    public TreeViewNode(@NonNull LayoutItemType content) {
        this.content = content;
        this.childList = new ArrayList<>();
    }

    public int getHeight() {
        if (isRoot()) {
            height = 0;
        } else if (height == UNDEFINE) {
            height = parent.getHeight() + 1;
        }
        return height;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return childList == null || childList.isEmpty();
    }

    public void setContent(LayoutItemType content) {
        this.content = content;
    }

    public LayoutItemType getContent() {
        return content;
    }

    public List<TreeViewNode> getChildList() {
        return childList;
    }

    public void setChildList(List<TreeViewNode> childList) {
        this.childList.clear();
        for (TreeViewNode TreeViewNode : childList) {
            addChild(TreeViewNode);
        }
    }

    public TreeViewNode addChild(TreeViewNode node) {
        if (childList == null) {
            childList = new ArrayList<>();
        }
        childList.add(node);
        node.parent = this;
        return this;
    }

    public boolean toggle() {
        isExpand = !isExpand;
        return isExpand;
    }

    public void collapse() {
        if (isExpand) {
            isExpand = false;
        }
    }

    public void collapseAll() {
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeViewNode child : this.childList) {
            child.collapseAll();
        }
    }

    public void expand() {
        if (!isExpand) {
            isExpand = true;
        }
    }

    public void expandAll() {
        expand();
        if (childList == null || childList.isEmpty()) {
            return;
        }
        for (TreeViewNode child : this.childList) {
            child.expandAll();
        }
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setParent(TreeViewNode parent) {
        this.parent = parent;
    }

    public TreeViewNode getParent() {
        return parent;
    }

    public TreeViewNode lock() {
        isLocked = true;
        return this;
    }

    public TreeViewNode unlock() {
        isLocked = false;
        return this;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public TreeViewNode clone() {
        try {
            return (TreeViewNode) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            TreeViewNode clone = new TreeViewNode(this.content);
            clone.isExpand = this.isExpand;
            return clone;
        }
    }

    @Override
    public String toString() {
        return "TreeViewNode{" +
                "content=" + this.content +
                ", parent=" + (parent == null ? "null" : parent.getContent().toString()) +
                ", childList=" + childList +
                ", isExpand=" + isExpand +
                '}';
    }
}
