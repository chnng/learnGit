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

import java.util.LinkedHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by 胡一鸣 on 2018/11/16.
 */
public final class StyleManager {
    private static volatile StyleManager mInstance;
    private QueryStyleBean mStyleBean;
    private LinkedHashMap<String, StyleColor> mStyleColorMap;
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

    public StyleManager() {
        initStyle();
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

    public static Observable<QueryStyleBean> updateStyle(ComponentCallbacks callbacks, int type) {
        QueryStyleBody body = new QueryStyleBody();
        body.hospital_code = AccountManager.getHospitalCode();
        body.dept_code = AccountManager.getDeptCode();
        body.user_id = type;
        return RetrofitManager.newMnServer().queryStyle(body)
                .compose(RetrofitManager.parseResponseWith(callbacks))
                .map(list -> {
                    if (CheckUtils.notEmpty(list)) {
                        QueryStyleBean bean = list.get(list.size() - 1);
                        setStyleColor(bean);
                        return bean;
                    }
                    return new QueryStyleBean();
                });
    }

    public static Observable<QueryStyleBean> insertStyle(ComponentCallbacks callbacks, String style, int type) {
        QueryStyleBean body = new QueryStyleBean();
        body.hospital_code = AccountManager.getHospitalCode();
        body.dept_code = AccountManager.getDeptCode();
        body.style_code = style;
        body.user_id = type;
        return RetrofitManager.newMnServer().queryStyleInsert(body)
                .compose(RetrofitManager.parseResponseWith(callbacks))
                .flatMap((Function<Boolean, ObservableSource<QueryStyleBean>>) aBoolean -> {
                    if (aBoolean) {
                        return updateStyle(callbacks, type);
                    } else {
                        return Observable.empty();
                    }
                });
    }

    public static class StyleColor {
        // 名称
        public String name;
        // 主题色
        public int themeColor;
        // 菜单悬浮球颜色
        public int menuColor;
        // 文本内容背景
        public int backgroundColor0;
        // 子标题背景
        public int backgroundColor1;
        // 内容背景
        public int backgroundColor2;
        // 列表条目背景
        public int backgroundColor3;
        // 边框颜色
        public int borderColor0;
        // 列表条目圆点颜色
        public int pointColor0;
        // 主标题字体颜色
        public int textColor0;
        // 子标题字体颜色
        public int textColor1;
        // 文本内容字体颜色
        public int textColor2;
        // 列表条目字体颜色
        public int textColor3;

        StyleColor(String name,
                   int themeColor,
                   int menuColor,
                   int backgroundColor0,
                   int backgroundColor1,
                   int backgroundColor2,
                   int backgroundColor3,
                   int textColor0,
                   int textColor1,
                   int textColor2,
                   int textColor3,
                   int borderColor0,
                   int pointColor0) {
            this.name = name;
            this.themeColor = themeColor;
            this.menuColor = menuColor;
            this.backgroundColor0 = backgroundColor0;
            this.backgroundColor1 = backgroundColor1;
            this.backgroundColor2 = backgroundColor2;
            this.backgroundColor3 = backgroundColor3;
            this.textColor0 = textColor0;
            this.textColor1 = textColor1;
            this.textColor2 = textColor2;
            this.textColor3 = textColor3;
            this.borderColor0 = borderColor0;
            this.pointColor0 = pointColor0;
        }
    }

    private void initStyle() {
        mStyleColorMap = new LinkedHashMap<>(5);
        // 粉
        mStyleColorMap.put("f76e98", new StyleColor("粉_f76e98", 0xFFf76e98, 0xFFda70d6,
                0xFFffc1d4, 0xFFf487a8, Color.TRANSPARENT, 0xFFffffff,
                0xFFf76e98, 0xFFffffff, 0xFFf76e98, 0xFFf76e98,
                0xFFfff9fb, 0XFFf76e98));
        // 灰
        mStyleColorMap.put("424255", new StyleColor("灰_424255", 0xFF424255, 0xFF1822ac,
                0xFF737388, 0xFF424255, Color.TRANSPARENT, 0xFFffffff,
                0xFF191b1c, 0xFFffffff, 0xFFffffff, 0XFF424255,
                0xFF2d2d3d, 0XFF424255));
        // 黑
        mStyleColorMap.put("191b1c", new StyleColor("黑_191b1c", 0xFF191b1c, 0xFFfff9fb,
                0xFF54565c, 0xFF292b2d, Color.TRANSPARENT, 0xFFffffff,
                0xFF191b1c, 0xFFffffff, 0xFFffffff, 0XFF191b1c,
                0xFF000000, 0XFF191b1c));
        // 绿
        mStyleColorMap.put("3d7d53", new StyleColor("绿_3d7d53", 0xFF3d7d53, 0xFFf0e68c,
                0xFFd2dfd6, 0xFF3d7d53, Color.TRANSPARENT, 0xFFffffff,
                0xFF3d7d53, 0xFFffffff, 0xFF3d7d53, 0xFF3d7d53,
                0xFFfff9fb, 0XFF3d7d53));
        // 蓝
        mStyleColorMap.put("1822ac", new StyleColor("蓝_1822ac", 0xFF1822ac, 0xFF778899,
                0xFFb0c1ff, 0xFF4463D8, Color.TRANSPARENT, 0xFFffffff,
                0xFF1822ac, 0xFFffffff, 0xFF1822ac, 0xFF1822ac,
                0xFFfff9fb, 0XFF1822ac));
        // 紫
        mStyleColorMap.put("4e45e3", new StyleColor("紫_4e45e3", 0xFF4e45e3, 0xFF778899,
                0xFFc5c2ff, 0xFF6c65e9, Color.TRANSPARENT, 0xFFffffff,
                0xFF4e45e3, 0xFFffffff, 0xFF4e45e3, 0xFF4e45e3,
                0xFFfff9fb, 0XFF4e45e3));
    }

    public static LinkedHashMap<String, StyleColor> getStyleColorMap() {
        return getInstance().mStyleColorMap;
    }

    public static StyleColor getStyleColor() {
        LinkedHashMap<String, StyleColor> styleColorMap = getInstance().mStyleColorMap;
        QueryStyleBean styleBean = getInstance().mStyleBean;
        StyleColor styleColor = null;
        if (styleBean != null) {
            styleColor = styleColorMap.get(styleBean.style_code);
        }
        if (styleColor == null) {
            styleColor = styleColorMap.values().iterator().next();
        }
        return styleColor;
    }
}
