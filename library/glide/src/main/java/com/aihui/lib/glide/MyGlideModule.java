package com.aihui.lib.glide;

import android.content.Context;

import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import androidx.annotation.NonNull;

/**
 * Created by 胡一鸣 on 2018/7/18.
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RetrofitManager.getOkHttpClient()));
    }
}
