package com.learn.git.api.glide;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

@GlideExtension
public class MyAppExtension {
    private MyAppExtension() { } // utility class

    @GlideOption
    public static void customOption(RequestOptions options) {
        RequestOptions requestOptions = options.diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .skipMemoryCache(true);
    }
}
