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
        mStyleColorMap = new LinkedHashMap<>(9);
        // 白
        mStyleColorMap.put("F0F4FC", new StyleColor("白_F0F4FC", 0XFFF0F4FC, 0xFFFFFFFF, Color.TRANSPARENT, 0xFFC6DDFF,
                0XFFF0F4FC, 0XFFF9CD35, 0xFF6AA7FF, 0xFF7E8A98, 0xFF52575D, 0xFF0068FF));
        // 粉
        mStyleColorMap.put("E98AA7", new StyleColor("粉0_E98AA7", 0XFFE98AA7, 0xFFFFE0E9, Color.TRANSPARENT, 0xFFFF729C,
                0XFFE98AA7, 0XFFF9CD35, 0xFFE07F9C, 0xFFFF729C, 0xFF976574, 0xFFFFFFFF));
        mStyleColorMap.put("F487A8", new StyleColor("粉1_F487A8", 0XFFF487A8, 0xFFFFFFFF, 0XFFF487A8, 0xFFFFD9E5,
                0XFFFFEEF4, 0XFFF487A8, 0xFFF487A8, 0xFFFFFFFF, 0xFFF487A8, 0xFFF487A8));
        // 蓝
        mStyleColorMap.put("00193E", new StyleColor("蓝0_00193E", 0XFF00193E, 0xFF112957, Color.TRANSPARENT, 0xFF2D55A0,
                0XFF00193E, 0XFFF9CD35, 0xFF96BAFF, 0xFF78849E, 0xFFFFFFFF, 0xFFF3F7FF));
        mStyleColorMap.put("5078F2", new StyleColor("蓝1_5078F2", 0XFF5078F2, 0xFFFFFFFF, 0XFF5078F2, 0xFFC2D1FF,
                0XFFEEF5FF, 0XFF5078F2, 0xFF5078F2, 0xFFFFFFFF, 0xFF5078F2, 0xFF5078F2));
        // 紫
        mStyleColorMap.put("1B083B", new StyleColor("紫0_1B083B", 0XFF1B083B, 0xFF251B4C, Color.TRANSPARENT, 0xFF452F6B,
                0XFF1B083B, 0XFFF9CD35, 0xFF7F5BBA, 0xFF797199, 0xFFFFFFFF, 0xFFF3F7FF));
        mStyleColorMap.put("6C65E9", new StyleColor("紫1_6C65E9", 0XFF6C65E9, 0xFFFFFFFF, 0XFF6C65E9, 0xFFCFCCFF,
                0XFFF2F1FF, 0xFF6C65E9, 0XFF6C65E9, 0xFFFFFFFF, 0xFF6C65E9, 0xFF6C65E9));
        // 灰
        mStyleColorMap.put("303042", new StyleColor("灰_303042", 0XFF303042, 0xFF424255, Color.TRANSPARENT, 0xFF38384A,
                0XFF303042, 0XFFF9CD35, 0xFFB6B6D5, 0xFF9999B3, 0xFFFFFFFF, 0xFFF3F7FF));
        // 黑
        mStyleColorMap.put("17181C", new StyleColor("黑_17181C", 0XFF17181C, 0xFF27292E, Color.TRANSPARENT, 0xFF43464D,
                0XFF17181C, 0XFFF9CD35, 0xFFB2B8CC, 0xFF92969E, 0xFFFFFFFF, 0xFFF3F7FF));
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

//    public void saveUserSkin(String code,ComponentCallbacks callbacks) {
//        QueryUserSkinBody body = new QueryUserSkinBody();
//        body.setHospital_code(AccountManager.getHospitalCode());
//        body.setDept_code(AccountManager.getDeptCode());
//        body.setEstate("0");
//        body.setId("0");
//        body.setStyle_code(code);
//        body.setUser_id("0");
//        //查询到当前主页应该显示的module
//        RetrofitManager.newMnHomepage2Server().saveUserSkin(body)
//                .compose(RetrofitManager.parseResponseWith(callbacks))
//                .subscribe(new BaseObserver<Boolean>() {
//                    @Override
//                    public void onNext(Boolean result) {
//                        if (result) {
//                            getInstance().mColor = Color.parseColor("#" + code);
//                            EventBus.getDefault().post(new EventMessage<>(0x24));
////                            getInstance().setChanged();
////                            getInstance().notifyObservers();
//                        }
//                    }
//                });
//    }

    public static class StyleColor {
        // 名称
        public String name;
        // 主题色
        public int themeColor;
        // 文本内容背景
        public int backgroundColor0;
        // 子标题背景
        public int backgroundColor1;
        // 列表条目背景
        public int backgroundColor2;
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
                   int backgroundColor0,
                   int backgroundColor1,
                   int backgroundColor2,
                   int borderColor0,
                   int pointColor0,
                   int textColor0,
                   int textColor1,
                   int textColor2,
                   int textColor3) {
            this.name = name;
            this.themeColor = themeColor;
            this.backgroundColor0 = backgroundColor0;
            this.backgroundColor1 = backgroundColor1;
            this.backgroundColor2 = backgroundColor2;
            this.borderColor0 = borderColor0;
            this.pointColor0 = pointColor0;
            this.textColor0 = textColor0;
            this.textColor1 = textColor1;
            this.textColor2 = textColor2;
            this.textColor3 = textColor3;
        }
    }

    public static LinkedHashMap<String, StyleColor> getStyleColorMap() {
        return getInstance().mStyleColorMap;
    }

    public static StyleColor getStyleColor() {
        LinkedHashMap<String, StyleColor> styleColorMap = getInstance().mStyleColorMap;
        QueryStyleBean styleBean = getInstance().mStyleBean;
        StyleColor styleColor = styleColorMap.get(styleBean == null ? "E98AA7" : styleBean.style_code);
        return styleColor == null ? styleColorMap.get("E98AA7") : styleColor;
    }
}
