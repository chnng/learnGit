package com.aihui.lib.nurse.ui.view.education;

import com.aihui.lib.base.model.module.th.msgpush.response.QueryEducationalDictionaryBean;
import com.aihui.lib.base.ui.view.treeview.LayoutItemType;
import com.aihui.lib.base.ui.view.treeview.TreeViewNode;

/**
 * Created by 胡一鸣 on 2018/7/25.
 */
public class EducationDirNode implements LayoutItemType {

    private QueryEducationalDictionaryBean mBean;
    private int mLayoutId;

    protected EducationDirNode(int layoutId, QueryEducationalDictionaryBean bean) {
        mLayoutId = layoutId;
        mBean = bean;
    }

    public QueryEducationalDictionaryBean getBean() {
        return mBean;
    }

    @Override
    public int getLayoutId() {
        return mLayoutId;
    }

    public static TreeViewNode getDirNode(int layoutId, QueryEducationalDictionaryBean bean) {
        return new TreeViewNode(new EducationDirNode(layoutId/*R.layout.item_function_msg_push_educ_dir*/, bean));
    }


    public static TreeViewNode getFileNode(int layoutId, QueryEducationalDictionaryBean bean) {
//        if (bean.DicType == HttpConstant.PUSH_TYPE_DOC) {
//            String filePath = bean.FilePath;
//            if (!TextUtils.isEmpty(filePath)) {
//                String fileType = FileUtils.getExtension(filePath);
//                if (!TextUtils.isEmpty(fileType)) {
//                    if (!fileType.equalsIgnoreCase("pdf")) {
//                        filePath = filePath.substring(0, filePath.length() - fileType.length()) + "html";
//                    }
//                } else if (filePath.contains("patientPath") || filePath.contains("&days")) {
//                    filePath += "&OperatorID=" + AccountManager.getLoginUid();
//                }
//                bean.FilePath = filePath;
//            }
//        }
        return new TreeViewNode(new EducationFileNode(layoutId/*R.layout.item_function_msg_push_educ_file*/, bean));
    }
}
