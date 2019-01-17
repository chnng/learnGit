package com.aihui.lib.nurse.util;

import android.content.ComponentCallbacks;
import android.os.Parcelable;

import com.aihui.lib.base.api.retrofit.BaseObserver;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.cons.CacheTag;
import com.aihui.lib.base.model.common.response.QueryHospitalBean;
import com.aihui.lib.base.model.module.th.cache.BaseCacheWrapper;
import com.aihui.lib.base.util.CheckUtils;
import com.aihui.lib.base.util.ParcelableUtils;
import com.aihui.lib.nurse.manager.AccountManager;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

/**
 * Created by 胡一鸣 on 2018/8/27.
 */
public final class CacheUtils {

    public static void saveCache(@NonNull String tag, @NonNull Parcelable parcelable) {
        ParcelableUtils.saveParcelableToFile(AccountManager.getLoginUid(), tag, parcelable);
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        ParcelableUtils.deleteParcelableInFile(CacheTag.USER_GLOBAL, CacheTag.HOSPITAL, QueryHospitalBean.CREATOR);
        int loginUid = AccountManager.getLoginUid();
        for (Object[] tags : CacheTag.CACHE_TAG_BY_CREATOR) {
            ParcelableUtils.deleteParcelableInFile(loginUid, String.valueOf(tags[0]), (Parcelable.Creator<?>) tags[1]);
        }
    }

    @Nullable
    public static <L extends Parcelable, T extends BaseCacheWrapper<L>> Parcelable.Creator<T> getCreator(@NonNull String tag) {
        for (Object[] cacheTag : CacheTag.CACHE_TAG_BY_CREATOR) {
            if (cacheTag[0].equals(tag)) {
                try {
                    return (Parcelable.Creator<T>) cacheTag[1];
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public interface OnCacheListener<T> {
        void onLoad(T list);
    }

    public static <L extends Parcelable, T extends BaseCacheWrapper<L>> boolean getCache(ComponentCallbacks callbacks,
                                                                                         @NonNull String tag,
                                                                                         Parcelable.Creator<T> creator,
                                                                                         OnCacheListener<List<L>> listener) {
        if (creator == null) {
            return false;
        }
        File file = new File(ParcelableUtils.getCacheFilePath(AccountManager.getLoginUid(),
                tag,
                ParcelableUtils.getWrapperClassName(creator)));
        if (!file.exists()) {
            return false;
        }
        getCacheObserver(tag, creator)
                .compose(parseCacheListWith(callbacks))
                .subscribe(new BaseObserver<List<L>>() {
                    @Override
                    public void onNext(List<L> ls) {
                        listener.onLoad(ls);
                    }
                });
        return true;
    }

    public static <T extends Parcelable> Observable<T> getCacheObserver(@NonNull String tag,
                                                     @NonNull Parcelable.Creator<T> creator) {
        return Observable.create(emitter -> {
            T parcelable = ParcelableUtils.getParcelableFromFile(AccountManager.getLoginUid(), tag, creator);
            if (parcelable != null) {
                emitter.onNext(parcelable);
            }
            emitter.onComplete();
        });
    }

    public static <T> RetrofitManager.Transformer<T, T> parseCacheWith(ComponentCallbacks callbacks) {
        return new RetrofitManager.Transformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.filter(CheckUtils::nonNull)
                        .compose(RetrofitManager.switchSchedulerWith(callbacks));
            }

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.filter(CheckUtils::nonNull)
                        .compose(RetrofitManager.switchSchedulerWith(callbacks));
            }
        };
    }

    private static <T extends Parcelable> RetrofitManager.Transformer<BaseCacheWrapper<T>, List<T>> parseCacheListWith(ComponentCallbacks callbacks) {
        return new RetrofitManager.Transformer<BaseCacheWrapper<T>, List<T>>() {
            @Override
            public Publisher<List<T>> apply(Flowable<BaseCacheWrapper<T>> upstream) {
                return upstream.filter(CheckUtils::nonNull)
                        .filter(wrapper -> CheckUtils.notEmpty(wrapper.list))
                        .map(wrapper -> wrapper.list)
                        .compose(RetrofitManager.switchSchedulerWith(callbacks));
            }

            @Override
            public ObservableSource<List<T>> apply(Observable<BaseCacheWrapper<T>> upstream) {
                return upstream.filter(CheckUtils::nonNull)
                        .filter(wrapper -> CheckUtils.notEmpty(wrapper.list))
                        .map(wrapper -> wrapper.list)
                        .compose(RetrofitManager.switchSchedulerWith(callbacks));
            }
        };
    }
}
