package com.aihui.lib.base.util;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.aihui.lib.base.cons.FileType;

/**
 * Created by 胡一鸣 on 2018/9/21.
 * https://blog.csdn.net/zhang453230017/article/details/68944304
 */
public final class ExtFileUtils {

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void openDocument(Activity activity, int requestCode) {
        openPicker(activity, requestCode, FileType.Mime.DOC, FileType.Mime.DOCX, FileType.Mime.TXT);
    }

    public static void openImage(Activity activity, int requestCode) {
        openPicker(activity, requestCode, FileType.Mime.PNG, FileType.Mime.JPEG);
    }

    private static void openPicker(Activity activity, int requestCode, String... mimeTypes) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (ApplicationUtils.isKitkat()) {
            int length = mimeTypes.length;
            intent.setType(length == 1 ? mimeTypes[0] : "*/*");
            if (length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            StringBuilder mimeTypesStr = null;
            for (String mimeType : mimeTypes) {
                if (mimeTypesStr == null) {
                    mimeTypesStr = new StringBuilder(mimeType);
                } else {
                    mimeTypesStr.append('|').append(mimeType);
                }
            }
            if (mimeTypesStr != null) {
                intent.setType(mimeTypesStr.toString());
            }
        }
        activity.startActivityForResult(Intent.createChooser(intent, "选择文件"), requestCode);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        activity.startActivityForResult(Intent.createChooser(intent, "选择文件"), requestCode);
    }

    // 得到正确路径
    public static String getResultFilePath(@NonNull Context context, Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            return null;
        }
        String path = null;
        // DocumentProvider
        if (ApplicationUtils.isKitkat() && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    path = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                path = getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{
                        split[1]
                };
                path = getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                path = uri.getLastPathSegment();
            } else {
                path = getDataColumn(context, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            path = uri.getPath();
        }
        return path;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String column = MediaStore.Images.Media.DATA;
        try (Cursor cursor = context.getContentResolver().query(
                uri, new String[]{column}, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            } else {
                return null;
            }
        }
    }
}
