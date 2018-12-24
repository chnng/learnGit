package com.aihui.lib.nurse.manager;

import android.content.ComponentCallbacks;
import android.support.annotation.NonNull;

import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.cons.App;
import com.aihui.lib.base.model.module.mn.bed.request.QueryBedCardBody;
import com.aihui.lib.base.model.module.mn.bed.response.QueryBedCardBean;
import com.aihui.lib.base.model.module.mn.main.request.QueryMnMenuBody;
import com.aihui.lib.base.model.module.mn.main.response.QueryMnMenuBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by 胡一鸣 on 2018/12/4.
 */
public final class BedCardManager {
    private static volatile BedCardManager mInstance;
    private List<QueryBedCardBean> mBedCardList;
    private List<QueryMnMenuBean> mBedMenuList;

    private static BedCardManager getInstance() {
        if (mInstance == null) {
            synchronized (BedCardManager.class) {
                if (mInstance == null) {
                    mInstance = new BedCardManager();
                }
            }
        }
        return mInstance;
    }

    public static QueryBedCardBean getBedCard(@NonNull String bedNumber) {
        List<QueryBedCardBean> bedCardList = getInstance().mBedCardList;
        if (bedCardList == null) {
            return null;
        }
        for (QueryBedCardBean bean : bedCardList) {
            if (bedNumber.equals(bean.bedNo)) {
                return bean;
            }
        }
        return null;
    }

    public static List<QueryBedCardBean> getBedCardList() {
        List<QueryBedCardBean> bedCardList = getInstance().mBedCardList;
        if (bedCardList == null) {
            return null;
        }
        return new ArrayList<>(bedCardList);
    }

    public static List<QueryMnMenuBean> getBedMenuList() {
        return getInstance().mBedMenuList;
    }

    public static Observable<List<QueryBedCardBean>> getBedCardObservable(ComponentCallbacks callbacks, List<QueryMnMenuBean> menuList) {
        QueryMnMenuBody body = new QueryMnMenuBody();
        body.app_code = App.APP_CODE;
        body.hospital_code = AccountManager.getHospitalCode();
        body.p_id = String.valueOf(menuList.get(0).id);
        body.isContainSon = "2";
        return RetrofitManager.newMnServer().queryMenu(body)
                .compose(RetrofitManager.parseResponseWith(callbacks))
                .map(list -> {
                    getInstance().mBedMenuList = list;
                    return list;
                })
                .flatMap(bean -> getBedCardObservable(callbacks));
    }

    public static Observable<List<QueryBedCardBean>> getBedCardObservable(ComponentCallbacks callbacks) {
        QueryBedCardBody body1 = new QueryBedCardBody();
        body1.token = AccountManager.getToken();
        body1.HospitalCode = AccountManager.getHospitalCode();
        body1.WardCode = AccountManager.getDeptCode();
        return RetrofitManager.newMnBedServer().queryBedCard(body1)
                .compose(RetrofitManager.parseResponseWith(callbacks))
                .map(list -> getInstance().mBedCardList = list);
    }
}
