package com.aihui.lib.base.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.CacheTag;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class ParcelableUtils {

    private static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle(); // not sure if needed or a good idea
        return bytes;
    }

    private static <T extends Parcelable> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        return creator.createFromParcel(parcel);
    }

    private static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // this is extremely important!
        return parcel;
    }

    public static String getCacheFilePath(int uid, String tag, String className) {
        return getAccountFilePath(uid) + File.separator + tag + className;
    }

    public static String getWrapperClassName(Parcelable.Creator<?> creator) {
        String creatorClassName = creator.getClass().getName();
        return creatorClassName.substring(creatorClassName.lastIndexOf('.') + 1,
                creatorClassName.lastIndexOf('$'));
    }

    @Nullable
    private static String getAccountFilePath(int uid) {
        if (uid == -1) {
            return null;
        } else if (uid == CacheTag.USER_GLOBAL) {
            return FileUtils.getCacheDirectory(BaseApplication.getContext()).getAbsolutePath()
                    + File.separator + FileUtils.DIR_PROFILE;
        }
        return FileUtils.getCacheDirectory(BaseApplication.getContext()).getAbsolutePath()
                + File.separator + FileUtils.DIR_PROFILE + File.separator + uid;
    }

//    public static void saveParcelableToFile(String tag, Parcelable parcelable) {
//        saveParcelableToFile(tag, parcelable, getLoginUid());
//    }

    public static void saveParcelableToFile(int uid, @NonNull String tag, @NonNull Parcelable parcelable) {
        if (uid < 0) {
            return;
        }
        byte[] data = ParcelableUtils.marshall(parcelable);
        FileUtils.writeData2File(getCacheFilePath(uid, tag, parcelable.getClass().getSimpleName()), data);
    }

//    public static <T extends Parcelable> T getParcelableFromFile(String tag, Parcelable.Creator<T> creator) {
//        return getParcelableFromFile(tag, creator, getLoginUid());
//    }

    @Nullable
    public static <T extends Parcelable> T getParcelableFromFile(int uid, String tag, Parcelable.Creator<T> creator) {
        if (uid < 0) {
            return null;
        }
        File file = new File(getCacheFilePath(uid, tag, getWrapperClassName(creator)));
        if (file.exists()) {
            byte[] date = FileUtils.readFile2Data(file);
            return unmarshall(date, creator);
        } else {
            return null;
        }
    }

//    public static void deleteParcelableInFile(String tag, Class<?> klass) {
//        deleteParcelableInFile(tag, klass, getLoginUid());
//    }

    public static void deleteParcelableInFile(int uid, String tag, Parcelable.Creator<?> creator) {
        if (uid < 0) {
            return;
        }
        File file = new File(getCacheFilePath(uid, tag, getWrapperClassName(creator)));
        if (file.exists()) {
            file.delete();
        }
    }
}
