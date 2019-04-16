package org.linphone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 文件操作类
 * Created by Nereo on 2015/4/8.
 */
public final class FileUtils {

//    private static final String JPEG_FILE_PREFIX = "IMG_";
//    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String DIR_DOWNLOAD = "download";
    public static final String DIR_PROFILE = "profile";
    public static final String DIR_RECORD = "record";
    public static final String DIR_LOG = "log";
    public static final String DIR_ROBUST = "robust";

//    public static File createTmpFile(Context context) throws IOException{
//        File dir = null;
//        if(TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
//            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//            if (!dir.exists()) {
//                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
//                if (!dir.exists()) {
//                    dir = getCacheDirectory(context, true);
//                }
//            }
//        }else{
//            dir = getCacheDirectory(context, true);
//        }
//        return File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
//    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    private static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        try {
            createFile(appCacheDir + "/.nomedia");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 创建文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File createFile(String filePath) throws IOException {
        return createFile(new File(filePath));
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static File createFile(@NonNull File file) throws IOException {
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            boolean ret = true;
            if (!parentFile.exists()) {
                ret = parentFile.mkdirs();
            }
            if (ret) {
                ret = file.createNewFile();
            }
            if (!ret) {
                throw new IOException("createFile " + file.getAbsolutePath() + " failed!");
            }
        }
        return file;
    }

    /**
     * 1.删除单文件
     * 2.删除目录但保留根目录
     *
     * @param file
     */
    public static void deleteFile(@NonNull File file) {
        deleteFile(file, !file.isDirectory());
    }

    /**
     * 删除文件（若为目录，则递归删除子目录和文件）
     *
     * @param file
     * @param delThisPath true代表删除指定参数file，false代表保留指定参数file
     */
    public static void deleteFile(@NonNull File file, boolean delThisPath) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                // 删除子目录和文件
                for (File subFile : subFiles) {
                    deleteFile(subFile, true);
                }
            }
        }
        if (delThisPath) {
            file.delete();
        }
    }

    /**
     * 获取文件大小，单位为byte（若为目录，则包括所有子目录和文件）
     * 用于计算缓存数据
     *
     * @param file
     * @return
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null) {
                    for (File subFile : subFiles) {
                        size += getFileSize(subFile);
                    }
                }
            } else {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param file
     * @return
     */
    public static String getFileSizeFormat(File file) {
        return formatFileSize(getFileSize(file));
    }

    /**
     * 转换文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        if (size == 0) {
            return "0B";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String formatSize;
        if (size < 1024) {
            formatSize = df.format((double) size) + "B";
        } else if (size < 1048576) {
            formatSize = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            formatSize = df.format((double) size / 1048576) + "MB";
        } else {
            formatSize = df.format((double) size / 1073741824) + "GB";
        }
        return formatSize;
    }

//    /**
//     * 检查是否已挂载SD卡镜像（是否存在SD卡）
//     *
//     * @return
//     */
//    public static boolean isMountedSDCard() {
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            return true;
//        } else {
//            LogUtils.e("SD卡不存在");
//            return false;
//        }
//    }
//
//    /**
//     * 获取SD卡剩余容量（单位Byte）
//     * 下载文件时，先判断SD卡的容量是否够用
//     *
//     * @return
//     */
//    public static long gainSDFreeSize() {
//        if (isMountedSDCard()) {
//            // 取得SD卡文件路径
//            File path = Environment.getExternalStorageDirectory();
//            StatFs sf = new StatFs(path.getPath());
//            // 获取单个数据块的大小(Byte)
//            long blockSize = sf.getBlockSize();
//            // 空闲的数据块的数量
//            long freeBlocks = sf.getAvailableBlocks();
//
//            // 返回SD卡空闲大小
//            return freeBlocks * blockSize; // 单位Byte
//        } else {
//            return 0;
//        }
//    }

    /***
     * 获取文件类型
     * @param filePath 文件路径
     * @return 文件的格式
     */
    @NonNull
    public static String getExtension(String filePath) {
        return getExtension(filePath, true);
    }

    @NonNull
    public static String getName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int indexQuery = filePath.indexOf('?');
        if (indexQuery == 0) {
            return "";
        } else if (indexQuery > 0) {
            filePath = filePath.substring(0, indexQuery);
        }
        int indexSeparator = filePath.lastIndexOf('/');
        if (indexSeparator >= 0) {
            filePath = filePath.substring(indexSeparator + 1);
        }
        return filePath;
    }

    @NonNull
    public static String getSimpleName(String filePath) {
        filePath = getName(filePath);
        int indexExtension = filePath.lastIndexOf('.');
        if (indexExtension < 0) {
            return filePath;
        }
        return filePath.substring(0, indexExtension);
    }

    @NonNull
    public static String getExtension(String filePath, boolean hasPoint) {
        filePath = getName(filePath);
        int indexExtension = filePath.lastIndexOf('.');
        if (indexExtension < 0) {
            return "";
        }
        return filePath.substring(hasPoint ? indexExtension : indexExtension + 1);
    }

    public static void writeData2File(String filePath, byte[] data) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        boolean ret = true;
        if (!parentFile.exists()) {
            ret = parentFile.mkdirs();
        }
        if (!ret) {
            return;
        }
        try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
            sink.write(data);
            sink.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try (OutputStream outputStream = new FileOutputStream(file)) {
//            outputStream.write(data);
//            outputStream.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static byte[] readFile2Data(File file) {
        try (BufferedSource source = Okio.buffer(Okio.source(file))) {
            return source.readByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        try (FileInputStream inputStream = new FileInputStream(file);
//             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            byte[] buffer = new byte[4 * 1024];
//            int size;
//            while ((size = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, size);
//            }
//            outputStream.flush();
//            return outputStream.toByteArray();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public static void copy(File src, File dst) {
        try (BufferedSink sink = Okio.buffer(Okio.sink(dst));
             Source source = Okio.source(src)) {
            sink.writeAll(source);
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
