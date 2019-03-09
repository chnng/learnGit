package com.aihui.lib.nurse.manager;

import android.content.Context;
import android.text.TextUtils;

import com.aihui.lib.base.api.retrofit.download.DownloadManager;
import com.aihui.lib.base.api.retrofit.download.OnProgressListener;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.App;
import com.aihui.lib.base.util.FileUtils;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.SharePreferenceUtils;
import com.meituan.robust.Patch;
import com.meituan.robust.PatchExecutor;
import com.meituan.robust.PatchManipulate;
import com.meituan.robust.RobustApkHashUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import okhttp3.Headers;

/**
 * Created by mivanzhang on 17/2/27.
 * <p>
 * We recommend you rewrite your own PatchManipulate class ,adding your special patch Strategy，in the demo we just load the patch directly
 *
 * <br>
 * Pay attention to the difference of patch's LocalPath and patch's TempPath
 *
 * <br>
 * We recommend LocalPath store the origin patch.jar which may be encrypted,while TempPath is the true runnable jar
 * <br>
 * <br>
 * 我们推荐继承PatchManipulate实现你们App独特的A补丁加载策略，其中setLocalPath设置补丁的原始路径，这个路径存储的补丁是加密过得，setTempPath存储解密之后的补丁，是可以执行的jar文件
 * <br>
 * setTempPath设置的补丁加载完毕即刻删除，如果不需要加密和解密补丁，两者没有啥区别
 */

public class PatchManager extends PatchManipulate {
    /***
     * connect to the network ,get the latest patches
     * l联网获取最新的补丁
     * @param context context
     *
     * @return list
     */
    @Override
    protected List<Patch> fetchPatchList(Context context) {
        //将app自己的robustApkHash上报给服务端，服务端根据robustApkHash来区分每一次apk build来给app下发补丁
        //apkhash is the unique identifier for  apk,so you cannnot patch wrong apk.
        String robustApkHash = RobustApkHashUtils.readRobustApkHash(context);
        LogUtils.w("robustApkHash :" + robustApkHash);
        //connect to network to get patch list on servers
        //在这里去联网获取补丁列表
        Patch patch = new Patch();
        patch.setName("123");
        //we recommend LocalPath store the origin patch.jar which may be encrypted,while TempPath is the true runnable jar
        //LocalPath是存储原始的补丁文件，这个文件应该是加密过的，TempPath是加密之后的，TempPath下的补丁加载完毕就删除，保证安全性
        //这里面需要设置一些补丁的信息，主要是联网的获取的补丁信息。重要的如MD5，进行原始补丁文件的简单校验，以及补丁存储的位置，这边推荐把补丁的储存位置放置到应用的私有目录下，保证安全性
//        patch.setLocalPath(Environment.getExternalStorageDirectory().getPath()+ File.separator+"robust"+File.separator + "patch");
        patch.setLocalPath(FileUtils.getIndividualCacheDirectory(context, FileUtils.DIR_DOWNLOAD).getAbsolutePath() + "/patch");

        //setPatchesInfoImplClassFullName 设置项各个App可以独立定制，需要确保的是setPatchesInfoImplClassFullName设置的包名是和xml配置项patchPackname保持一致，而且类名必须是：PatchesInfoImpl
        //请注意这里的设置
        patch.setPatchesInfoImplClassFullName("com.meituan.robust.patch.PatchesInfoImpl");
        List<Patch> patches = new ArrayList<>();
        patches.add(patch);
        return patches;
    }

    /**
     * @param context context
     * @param patch   patch
     * @return you can verify your patches here
     */
    @Override
    protected boolean verifyPatch(Context context, Patch patch) {
        //do your verification, put the real patch to patch
        //放到app的私有目录
//        patch.setTempPath(context.getCacheDir()+ File.separator+"robust"+File.separator + "patch");
        patch.setTempPath(FileUtils.getIndividualCacheDirectory(context, FileUtils.DIR_ROBUST).getAbsolutePath() + "/patch");
        //in the sample we just copy the file
        try {
//            copy(patch.getLocalPath(), patch.getTempPath());
            FileUtils.copy(new File(patch.getLocalPath()), new File(patch.getTempPath()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("copy source patch to local patch error, no patch execute in path " + patch.getTempPath());
        }

        return true;
    }

//    private void copy(String srcPath, String dstPath) throws IOException {
//        LogUtils.e("copy " + srcPath + " " + dstPath);
//        File src = new File(srcPath);
//        if (!src.exists()) {
//            throw new RuntimeException("source patch does not exist ");
//        }
//        File dst = new File(dstPath);
//        if (!dst.getParentFile().exists()) {
//            dst.getParentFile().mkdirs();
//        }
//        try (InputStream in = new FileInputStream(src)) {
//            try (OutputStream out = new FileOutputStream(dst)) {
//                // Transfer bytes from in to out
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//            }
//        }
//    }

    /**
     * @param patch patch
     * @return you may download your patches here, you can check whether patch is in the phone
     */
    @Override
    protected boolean ensurePatchExist(Patch patch) {
        return true;
    }

    public static void checkPatch(String fileName, boolean isForced) {
        Context context = BaseApplication.getContext();
        File file = new File(FileUtils.getIndividualCacheDirectory(context, FileUtils.DIR_DOWNLOAD), "patch.jar");
        Headers headers = null;
        if (file.exists()) {
            if (isForced) {
                new PatchExecutor(context, new PatchManager(), new PatchCallBack()).run();
            }
            String patchLastModified = SharePreferenceUtils.get(context, SharePreferenceUtils.SP_MN_PATCH_LAST_MODIFIED, "");
            if (!TextUtils.isEmpty(patchLastModified)) {
                headers = new Headers.Builder()
                        .add("If-Modified-Since", patchLastModified)
//                                .add("If-None-Match", "392828474ccfd41:0")
                        .build();
            }
        } else {
            SharePreferenceUtils.put(context, SharePreferenceUtils.SP_MN_PATCH_LAST_MODIFIED, null);
        }
        DownloadManager.downloadFile(App.BASE_URL + "uploadfile/app/android/patch/" + fileName + ".jar",
                headers, file,
                new OnProgressListener() {
                    @Override
                    protected void onHeaders(Headers headers) {
                        super.onHeaders(headers);
                        if (headers != null) {
                            String lastModified = headers.get("Last-Modified");
                            if (!TextUtils.isEmpty(lastModified)) {
                                SharePreferenceUtils.put(context, SharePreferenceUtils.SP_MN_PATCH_LAST_MODIFIED, lastModified);
                            }
                        }
                    }

                    @Override
                    protected void onSuccess(@NonNull File file) {
                        super.onSuccess(file);
                        new PatchExecutor(context, new PatchManager(), new PatchCallBack()).run();
                    }

                    @Override
                    protected void onFailure(@NonNull Exception e, @NonNull File file) {
                        super.onFailure(e, file);
                        SharePreferenceUtils.put(context, SharePreferenceUtils.SP_MN_PATCH_LAST_MODIFIED, null);
                        if (e instanceof FileNotFoundException) {
                            file.delete();
                        }
                    }
                });
    }
}
