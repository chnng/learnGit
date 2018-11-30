package com.aihui.lib.nurse.manager;

import android.content.ComponentCallbacks;
import android.graphics.Color;

import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.CacheTag;
import com.aihui.lib.base.model.module.mn.main.request.QueryStyleBody;
import com.aihui.lib.base.model.module.mn.main.response.QueryStyleBean;
import com.aihui.lib.base.util.CheckUtils;
import com.aihui.lib.base.util.ParcelableUtils;
import com.aihui.lib.nurse.R;
import com.aihui.lib.nurse.util.CacheUtils;
import com.aihui.lib.nurse.util.ViewUtils;

import io.reactivex.Observable;

/**
 * Created by 胡一鸣 on 2018/11/16.
 */
public final class StyleManager {
    private static volatile StyleManager mInstance;
    private QueryStyleBean mStyleBean;
    private int mColor, mSubColor, mInvertSubColor;
    private boolean mIsStyleBright;

    private static StyleManager getInstance() {
        if (mInstance == null) {
            synchronized (StyleManager.class) {
                if (mInstance == null) {
                    mInstance = new StyleManager();
                }
            }
        }
        return mInstance;
    }

    private static void setStyleBean() {
        StyleManager manager = getInstance();
        if (manager.mStyleBean == null) {
            manager.mStyleBean = ParcelableUtils.getParcelableFromFile(AccountManager.getLoginUid(),
                    CacheTag.MN_STYLE, QueryStyleBean.CREATOR);
        }
        if (manager.mStyleBean != null) {
            setStyleColor(manager.mStyleBean);
        }
    }

    private static void setStyleColor(QueryStyleBean bean) {
        StyleManager manager = getInstance();
        manager.mStyleBean = bean;
        manager.mColor = Color.parseColor("#" + bean.style_code);
        manager.mIsStyleBright = ViewUtils.isColorBright(manager.mColor);
        if (manager.mIsStyleBright) {
            manager.mSubColor = BaseApplication.getContext().getResources().getColor(R.color.text_white);
            manager.mInvertSubColor = BaseApplication.getContext().getResources().getColor(R.color.text_black);
        } else {
            manager.mSubColor = BaseApplication.getContext().getResources().getColor(R.color.text_black);
            manager.mInvertSubColor = BaseApplication.getContext().getResources().getColor(R.color.text_white);
        }
        manager.mInvertSubColor = getSubColor(manager.mColor, true);
        CacheUtils.saveCache(CacheTag.MN_STYLE, bean);
    }

    public static int getColor() {
        return getColor(1);
    }

    public static int getColor(float alpha) {
        StyleManager styleManager = getInstance();
        int color = Color.TRANSPARENT;
        if (styleManager.mColor != Color.TRANSPARENT) {
            color = styleManager.mColor;
        } else {
            setStyleBean();
            if (styleManager.mColor != Color.TRANSPARENT) {
                color = styleManager.mColor;
            }
        }
        if (color == Color.TRANSPARENT) {
            color = BaseApplication.getContext().getResources().getColor(R.color.colorPrimary);
        }
        return ViewUtils.getColorWithAlpha(color, alpha);
    }

    public static int getSubColor(boolean isInverted) {
        return getSubColor(isInverted, 1);
    }

    public static int getSubColor(boolean isInverted, float alpha) {
        StyleManager styleManager = getInstance();
        int color;
        if (isInverted) {
            if (styleManager.mInvertSubColor == Color.TRANSPARENT) {
                styleManager.mInvertSubColor = getSubColor(getColor(), true);
            }
            color = styleManager.mInvertSubColor;
        } else {
            if (styleManager.mSubColor == Color.TRANSPARENT) {
                styleManager.mSubColor = getSubColor(getColor(), false);
            }
            color = styleManager.mSubColor;
        }
        return ViewUtils.getColorWithAlpha(color, alpha);
    }

    /**
     * @param color      主题色
     * @param isInverted 是否反色
     * @return 底色
     */
    private static int getSubColor(int color, boolean isInverted) {
        int colorId;
        if (ViewUtils.isColorBright(color)) {
            // 亮色：底色白，反色黑
            colorId = isInverted ? R.color.text_black : R.color.text_white;
        } else {
            // 暗色：底色黑，反色白
            colorId = isInverted ? R.color.text_white : R.color.text_black;
        }
        return BaseApplication.getContext().getResources().getColor(colorId);
    }

    public static boolean isStyleBright() {
        return getInstance().mIsStyleBright;
    }

    public static Observable<QueryStyleBean> updateStyle(ComponentCallbacks callbacks) {
        QueryStyleBody body = new QueryStyleBody();
        body.hospital_code = AccountManager.getHospitalCode();
        body.dept_code = AccountManager.getDeptCode();
        body.user_id = AccountManager.getLoginAccount().account_id;
        return RetrofitManager.newMnServer().queryStyle(body)
                .compose(RetrofitManager.parseResponseWith(callbacks))
                .map(list -> {
                    if (CheckUtils.notEmpty(list)) {
                        QueryStyleBean bean = list.get(list.size() - 1);
                        setStyleColor(bean);
                        return bean;
                    }
                    return null;
                });
    }
}
