package com.learn.git.api.retrofit;

import com.learn.git.api.okhttp.OkHttpClientHelper;
import com.learn.git.server.Server;
import com.learn.git.server.bean.base.BaseResponse;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static Retrofit mRetrofit;

    public static Retrofit getInstance() {
        if (mRetrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (mRetrofit == null) {
                    mRetrofit = new Retrofit.Builder()
                            .client(OkHttpClientHelper.get())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("https://hl.smartsky-tech.com:8094")
                            .build();
                }
            }
        }
        return mRetrofit;
    }

    public static Server getServer() {
        return getInstance().create(Server.class);
    }

    public static <T> Function<BaseResponse<T>, T> mapper() {
        return tBaseResponse -> {
//            LogUtil.d("mapper:" + tBaseResponse);
            return tBaseResponse.Result;
        };
    }

    public static <T> ObservableTransformer<T, T> transformer() {
        return tObservable -> tObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> transActivity(LifecycleProvider<ActivityEvent> provider) {
        return upstream -> upstream.compose(transformer())
                .compose(provider.bindUntilEvent(ActivityEvent.DESTROY));
    }

    public static <T> ObservableTransformer<T, T> transFragment(LifecycleProvider<FragmentEvent> provider) {
        return upstream -> upstream.compose(transformer())
                .compose(provider.bindUntilEvent(FragmentEvent.DESTROY_VIEW));
    }

    public static <T> ObservableTransformer<BaseResponse<T>, T> resultTransActivity(LifecycleProvider<ActivityEvent> provider) {
        return upstream -> upstream.map(mapper())
                .compose(transActivity(provider));
    }

    public static <T> ObservableTransformer<BaseResponse<T>, T> resultTransFragment(LifecycleProvider<FragmentEvent> provider) {
        return upstream -> upstream.map(mapper())
                .compose(transFragment(provider));
    }
}