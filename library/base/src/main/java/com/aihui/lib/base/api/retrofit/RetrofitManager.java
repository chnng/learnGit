package com.aihui.lib.base.api.retrofit;

import android.content.ComponentCallbacks;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.aihui.lib.base.api.retrofit.server.HttpBaseServer;
import com.aihui.lib.base.app.IBaseCreateTime;
import com.aihui.lib.base.app.IBaseSort;
import com.aihui.lib.base.model.common.response.BaseResponseBean;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.TimeUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.reactivestreams.Publisher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 路传涛 on 2017/6/1.
 */

public class RetrofitManager {
    private static volatile RetrofitManager mInstance;
    private Gson mGson;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private static RetrofitManager getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitManager.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitManager();
                }
            }
        }
        return mInstance;
    }

    private RetrofitManager() {
        mGson = new Gson();
        mOkHttpClient = RetrofitUrlManager.getInstance().with(ProgressManager.getInstance().with(new OkHttpClient.Builder()))
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor(LogUtils::http)
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://www.baidu.com/")
                .build();
        RetrofitUrlManager.getInstance().putDomain("rcUrl", "https://www.baidu.com/");
    }

    public static synchronized void setBaseUrl(String url) {
        getInstance().mRetrofit = new Retrofit.Builder()
                .client(getOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

    public static Gson getGson() {
        return getInstance().mGson;
    }

    public static OkHttpClient getOkHttpClient() {
        return getInstance().mOkHttpClient;
    }

    private static Retrofit getRetrofit() {
        return getInstance().mRetrofit;
    }

    public static HttpBaseServer newHttpBaseServer() {
        return getRetrofit().create(HttpBaseServer.class);
    }



    @NonNull
    public static <T> Function<BaseResponseBean<T>, T> parseResponse() {
        return bean -> {
            if (bean.MessageType != 0) {
                ToastUtils.toast(bean.MessageType + (bean.ErrorMessage != null ? bean.ErrorMessage.toString() : ""));
            }
            return bean.Result;
        };
    }

    public static <T extends IBaseSort> List<T> sort(List<T> list) {
        return sort(list, true);
    }

    public static <T extends IBaseSort> List<T> sort(List<T> list, boolean acs) {
        // 升序
        Collections.sort(list, (o1, o2) -> {
            if (o1 != null && o2 != null) {
                int compare = Long.compare(o1.getSort(), o2.getSort());
                return acs ? compare : -compare;
            } else {
                return 0;
            }
        });
        return list;
    }

    public static <T extends IBaseCreateTime> List<T> sortByCreateTime(List<T> list) {
        // 降序
        Collections.sort(list, (o1, o2) -> {
            if (o1 != null && o2 != null) {
                String time1 = o1.getCreateTime();
                String time2 = o2.getCreateTime();
                if (!TextUtils.isEmpty(time1) && !TextUtils.isEmpty(time2)) {
                    return Long.compare(TimeUtils.getTime(time2), TimeUtils.getTime(time1));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        });
        return list;
    }

    public abstract static class Transformer<Upstream, Downstream> implements
            ObservableTransformer<Upstream, Downstream>,
            FlowableTransformer<Upstream, Downstream> {
    }

    @Nullable
    private static <T> LifecycleTransformer<T> lifecycleTransformer(ComponentCallbacks callbacks) {
        LifecycleTransformer<T> transformer = null;
        if (callbacks instanceof RxActivity || callbacks instanceof RxAppCompatActivity) {
            transformer = ((LifecycleProvider<ActivityEvent>) callbacks).bindUntilEvent(ActivityEvent.DESTROY);
        } else if (callbacks instanceof Fragment || callbacks instanceof android.app.Fragment) {
            transformer = ((LifecycleProvider<FragmentEvent>) callbacks).bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        }
        return transformer;
    }

    public static <T> Transformer<T, T> switchScheduler() {
        return new Transformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Transformer<T, T> bindLifecycle(ComponentCallbacks callbacks) {
        LifecycleTransformer<T> transformer = lifecycleTransformer(callbacks);
        if (transformer == null) {
            return new Transformer<T, T>() {
                @Override
                public Publisher<T> apply(Flowable<T> upstream) {
                    return upstream;
                }

                @Override
                public ObservableSource<T> apply(Observable<T> upstream) {
                    return upstream;
                }
            };
        } else {
            return new Transformer<T, T>() {
                @Override
                public Publisher<T> apply(Flowable<T> upstream) {
                    return upstream.compose(transformer);
                }

                @Override
                public ObservableSource<T> apply(Observable<T> upstream) {
                    return upstream.compose(transformer);
                }
            };
        }
    }

    public static <T> Transformer<T, T> switchSchedulerWith(ComponentCallbacks callbacks) {
        return new Transformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.compose(switchScheduler()).compose(bindLifecycle(callbacks));
            }

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.compose(switchScheduler()).compose(bindLifecycle(callbacks));
            }
        };
    }

    public static <T> Transformer<BaseResponseBean<T>, T> parseResponseWith(ComponentCallbacks callbacks) {
        return new Transformer<BaseResponseBean<T>, T>() {
            @Override
            public Publisher<T> apply(Flowable<BaseResponseBean<T>> upstream) {
                return upstream.map(parseResponse())
                        .compose(switchSchedulerWith(callbacks));
            }

            @Override
            public ObservableSource<T> apply(Observable<BaseResponseBean<T>> upstream) {
                return upstream.map(parseResponse())
                        .compose(switchSchedulerWith(callbacks));
            }
        };
    }
}
