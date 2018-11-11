package com.aihui.lib.nurse.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.aihui.lib.base.api.retrofit.BaseObserver;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.api.retrofit.download.DownloadManager;
import com.aihui.lib.base.api.retrofit.download.OnProgressListener;
import com.aihui.lib.base.cons.FileType;
import com.aihui.lib.base.cons.HttpConstant;
import com.aihui.lib.base.model.common.response.BaseResponseBean;
import com.aihui.lib.base.model.module.th.msgpush.request.QueryEducationDetailsInsertBody;
import com.aihui.lib.base.model.module.th.msgpush.request.QueryPushInfoInsertBody;
import com.aihui.lib.base.model.module.th.msgpush.response.QueryEducationalDictionaryBean;
import com.aihui.lib.base.ui.view.treeview.TreeNode;
import com.aihui.lib.base.util.CheckUtils;
import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.aihui.lib.nurse.manager.AccountManager;
import com.aihui.lib.nurse.ui.education.EducationDirNode;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by 胡一鸣 on 2018/7/31.
 */
public final class MsgPushUtils {

    public static void parseNode(@NonNull List<QueryEducationalDictionaryBean> list,
                                 @NonNull TreeNode node,
                                 @LayoutRes int layoutIdDir,
                                 @LayoutRes int layoutIdFile) {
        TreeNode<EducationDirNode> childNode;
        for (QueryEducationalDictionaryBean bean : list) {
            QueryEducationalDictionaryBean nodeBean = ((EducationDirNode) node.getContent()).getBean();
            int dictionaryID = nodeBean.DictionaryID;
            if (dictionaryID == bean.ParentClass) {
                if (!TextUtils.isEmpty(bean.FilePath)) {
                    bean.DicType = HttpConstant.PUSH_TYPE_DOC;
                } else if (!TextUtils.isEmpty(bean.Content)) {
                    bean.DicType = HttpConstant.PUSH_TYPE_TEXT;
                } else {
                    bean.DicType = HttpConstant.DIC_TYPE_DIR;
                }
                String dictionaryName = bean.DictionaryName;
                if (!TextUtils.isEmpty(dictionaryName)) {
                    childNode = EducationDirNode.getDirNode(layoutIdDir, bean);
                    // 患者入院须知,病区入院宣教手册,天护教程720,健康宣教16区
                    if (dictionaryName.contains(",")) {
                        // 1.病区环境①护士站②医生办公室③洗漱间④厕所⑤开水间
                        bean.DictionaryName = bean.Content;
                        String[] dicNames = dictionaryName.split(",");
                        String[] files = bean.FilePath.split(",");
                        for (int i = 0; i < dicNames.length; i++) {
                            QueryEducationalDictionaryBean clone = bean.clone();
                            clone.DictionaryName = dicNames[i];
                            clone.FilePath = files[Math.min(i, files.length - 1)];
                            childNode.addChild(EducationDirNode.getFileNode(layoutIdFile, clone));
                        }
                    } else if (bean.DicType != HttpConstant.DIC_TYPE_DIR) {
                        // 文件目录不创建文件
                        QueryEducationalDictionaryBean clone = bean.clone();
                        clone.DictionaryName = dictionaryName;
                        childNode.addChild(EducationDirNode.getFileNode(layoutIdFile, clone));
                    }
                } else if (!TextUtils.isEmpty(bean.Content)) {
                    bean.DictionaryName = bean.Content;
                    childNode = EducationDirNode.getFileNode(layoutIdFile, bean);
                } else {
                    bean.DictionaryName = bean.ClassName;
                    childNode = EducationDirNode.getDirNode(layoutIdDir, bean);
                }
                node.addChild(childNode);
                parseNode(list, childNode, layoutIdDir, layoutIdFile);
            }
        }
    }

    public static void addSelectedNodeList(@NonNull List<QueryEducationDetailsInsertBody> list,
                                           TreeNode<EducationDirNode> node,
                                           String bedNumber,
                                           boolean isSelected) {
        for (TreeNode child : node.getChildList()) {
            if (child.getContent() instanceof EducationDirNode) {
                QueryEducationalDictionaryBean bean = ((EducationDirNode) child.getContent()).getBean();
                if (bean.isSelected == isSelected) {
                    String filePath = bean.FilePath;
                    if (!TextUtils.isEmpty(filePath) && bean.DicType == HttpConstant.PUSH_TYPE_DOC) {
                        String fileType = FileUtils.getExtension(filePath);
                        if (!TextUtils.isEmpty(fileType)) {
                            if (!FileType.Extension.PDF.equalsIgnoreCase(fileType)) {
                                filePath = filePath.substring(0, filePath.length() - fileType.length()) + FileType.Extension.HTML;
                            }
                        } else if (filePath.contains("patientPath") || filePath.contains("&days")) {
                            filePath += "&OperatorID=" + AccountManager.getLoginUid();
                        }
                    }
                    QueryEducationDetailsInsertBody body = MsgPushUtils.getInsertEducationDetailsBody();
                    body.Name = bean.DictionaryName;
                    body.DictionaryID = String.valueOf(bean.DictionaryID);
                    body.Content = TextUtils.isEmpty(filePath) ? bean.Content : filePath;
                    body.BedNumber = bedNumber;
                    body.Type = bean.DicType;
                    list.add(body);
                }
            }
            addSelectedNodeList(list, child, bedNumber, isSelected);
        }
    }

    public static QueryEducationDetailsInsertBody getInsertEducationDetailsBody() {
        QueryEducationDetailsInsertBody body = new QueryEducationDetailsInsertBody();
        body.token = AccountManager.getToken();
        body.HospitalCode = AccountManager.getHospitalCode();
        body.WardCode = AccountManager.getDeptCode();
        body.OperatorID = String.valueOf(AccountManager.getLoginUid());
        return body;
    }

    @NonNull
    private static Function<String, QueryPushInfoInsertBody> educToPush(QueryEducationDetailsInsertBody body) {
        return s -> {
            QueryPushInfoInsertBody pushInfoBody = new QueryPushInfoInsertBody();
            pushInfoBody.AccessId = AccountManager.getAccessId();
            pushInfoBody.Name = body.Name;
            pushInfoBody.DictionaryID = body.DictionaryID;
            pushInfoBody.Content = body.Content;
            pushInfoBody.PushType = body.Type;
            pushInfoBody.HospitalCode = body.HospitalCode;
            pushInfoBody.DeptCode = body.WardCode;
            pushInfoBody.BedNumber = s;
            pushInfoBody.UserID = body.OperatorID;
            return pushInfoBody;
        };
    }

    public static void insertMsgPush(RxAppCompatActivity activity, @NonNull QueryEducationDetailsInsertBody body) {
        insertMsgPush(activity, body, null);
    }

    private static void insertMsgPush(RxAppCompatActivity activity, @NonNull QueryEducationDetailsInsertBody body, BaseObserver<Boolean> observer) {
        insertMsgPush(activity, body, observer, true);
    }

    public static void insertMsgPush(RxAppCompatActivity activity, @NonNull QueryEducationDetailsInsertBody body, boolean isFinish) {
        insertMsgPush(activity, body, null, isFinish);
    }

    private static void insertMsgPush(RxAppCompatActivity activity, @NonNull QueryEducationDetailsInsertBody body, BaseObserver<Boolean> observer, boolean isFinish) {
        ProgressDialog dialog = null;
        if (observer == null) {
            dialog = new ProgressDialog(activity);
            dialog.setMessage("发送中...");
            dialog.setCancelable(false);
            dialog.show();
        }
        Dialog finalDialog = dialog;
        RetrofitManager.newThServer().queryEducationDetailsInsert(body)
                .map(RetrofitManager.parseResponse())
                .map(educToPush(body))
                .flatMap((Function<QueryPushInfoInsertBody, ObservableSource<BaseResponseBean<Boolean>>>) queryPushInfoInsertBody ->
                        RetrofitManager.newThServer().queryPushInfoInsert(queryPushInfoInsertBody))
                .compose(RetrofitManager.parseResponseWith(activity))
                .subscribe(new BaseObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (observer != null) {
                            observer.onNext(aBoolean);
                        } else if (aBoolean) {
                            ToastUtils.toast("发送成功");
                            if (isFinish) {
                                activity.finish();
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        CheckUtils.dismissDialog(finalDialog);
                        if (observer != null) {
                            observer.onComplete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        CheckUtils.dismissDialog(finalDialog);
                        if (observer != null) {
                            observer.onError(e);
                        }
                    }
                });
    }

    public static void insertMsgPushList(RxAppCompatActivity activity, List<QueryEducationDetailsInsertBody> bodyList) {
        if (bodyList == null || bodyList.isEmpty()) {
            return;
        }
        AtomicInteger integer = new AtomicInteger(bodyList.size());
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("发送中...");
        dialog.show();
        for (QueryEducationDetailsInsertBody body : bodyList) {
            insertMsgPush(activity, body, new BaseObserver<Boolean>() {
                @Override
                public void onNext(Boolean aBoolean) {

                }

                @Override
                public void onError(Throwable e) {
                    finishSend();
                }

                @Override
                public void onComplete() {
                    finishSend();
                }

                private void finishSend() {
                    if (integer.decrementAndGet() == 0) {
                        ToastUtils.toast("发送成功");
                        dialog.cancel();
                        activity.finish();
                    }
                }
            });
        }
    }

    public static void doUpload(RxAppCompatActivity activity,
                                String bedNumber,
                                String mediaType,
                                @NonNull File file,
                                boolean isFinish,
                                String pushName,
                                String fileType) {
        doUpload(activity, bedNumber, mediaType, file, isFinish, pushName, fileType, null);
    }

    public static void doUpload(RxAppCompatActivity activity,
                                String bedNumber,
                                String mediaType,
                                @NonNull File file,
                                boolean isFinish,
                                String pushName,
                                String fileType,
                                ProgressDialog dialog) {
        if (dialog == null) {
            dialog = new ProgressDialog(activity);
        }
        dialog.setMessage("发送中...");
        dialog.setCancelable(false);
        dialog.show();
        ProgressDialog finalDialog = dialog;
        DownloadManager.uploadFile("source\\missionize", file, mediaType, new OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                finalDialog.setMessage("正在发送:" + progress + "%");
            }
        })
                .compose(RetrofitManager.switchSchedulerWith(activity))
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onNext(String url) {
                        if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                            QueryEducationDetailsInsertBody body = MsgPushUtils.getInsertEducationDetailsBody();
                            body.BedNumber = bedNumber;
                            body.Name = pushName;
                            body.Content = url.replace(fileType, FileType.Extension.HTML);
                            body.Type = HttpConstant.PUSH_TYPE_LINK;
                            MsgPushUtils.insertMsgPush(activity, body, isFinish);
                        } else {
                            ToastUtils.toast("推送失败，请重试!");
                        }
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        finishRecord();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRecord();
                        ToastUtils.toast("上传失败:" + e.getMessage());
                    }

                    private void finishRecord() {
                        finalDialog.dismiss();
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                });
    }
}
