package com.aihui.lib.nurse.manager;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aihui.lib.base.api.retrofit.AhException;
import com.aihui.lib.base.api.retrofit.BaseObserver;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.App;
import com.aihui.lib.base.cons.CacheTag;
import com.aihui.lib.base.cons.ErrorCode;
import com.aihui.lib.base.cons.HttpConstant;
import com.aihui.lib.base.model.common.request.QueryOneHospitalBody;
import com.aihui.lib.base.model.common.response.QueryHospitalBean;
import com.aihui.lib.base.model.module.nurse.request.QueryLoginBody;
import com.aihui.lib.base.model.module.nurse.response.HospitalUserBean;
import com.aihui.lib.base.model.module.th.main.request.HospitalIdentifyBody;
import com.aihui.lib.base.model.module.th.main.request.QueryAccessBody;
import com.aihui.lib.base.model.module.th.main.request.QueryDepartmentBody;
import com.aihui.lib.base.model.module.th.main.request.QueryDoctorInfoBody;
import com.aihui.lib.base.model.module.th.main.request.QueryHospitalUserBody;
import com.aihui.lib.base.model.module.th.main.request.QueryTokenBody;
import com.aihui.lib.base.model.module.th.main.response.QueryAccessBean;
import com.aihui.lib.base.model.module.th.main.response.QueryDepartmentBean;
import com.aihui.lib.base.model.module.th.main.response.QueryDoctorInfoBean;
import com.aihui.lib.base.util.CheckUtils;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.ParcelableUtils;
import com.aihui.lib.base.util.SharePreferenceUtils;
import com.aihui.lib.nurse.db.userinfo.DBUserInfoBean;
import com.aihui.lib.nurse.db.userinfo.DBUserInfoUtils;
import com.aihui.lib.nurse.util.BuglyUtils;
import com.aihui.lib.nurse.util.CacheUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 胡一鸣 on 2018/7/13.
 */
public final class AccountManager {

    private static volatile AccountManager mInstance;
    private QueryHospitalBean mHospitalBean;
    private HospitalUserBean mHospitalUserBean;
    private QueryDoctorInfoBean mDoctorInfoBean;
    private String mToken;
    private int mAccessId;

    // 护理部账号切换的病区
    private String mSelectWardCode;

    // 病区账号屏蔽护理部账号推送内容
    private String mDeptUserIds;

    private static AccountManager getInstance() {
        if (mInstance == null) {
            synchronized (AccountManager.class) {
                if (mInstance == null) {
                    mInstance = new AccountManager();
                }
            }
        }
        return mInstance;
    }

    public AccountManager() {
        // avoid crash too many layers
        mHospitalBean = ParcelableUtils.getParcelableFromFile(
                CacheTag.USER_GLOBAL, CacheTag.HOSPITAL, QueryHospitalBean.CREATOR);
        LogUtils.e("AccountManager hospital:" + mHospitalBean);
        mToken = SharePreferenceUtils.get(
                BaseApplication.getContext(), CacheTag.TOKEN, "");
        LogUtils.e("AccountManager token:" + mToken);
        DBUserInfoBean loginUserInfo = DBUserInfoUtils.queryLoginUserInfo();
        LogUtils.e("AccountManager loginUserInfo:" + loginUserInfo);
        if (loginUserInfo != null) {
            mHospitalUserBean = ParcelableUtils.getParcelableFromFile(loginUserInfo.uid,
                    CacheTag.LOGIN, HospitalUserBean.CREATOR);
        }
        // avoid crash too many layers end
        RetrofitManager.newThServer().queryQueryAccess(new QueryAccessBody())
                .map(RetrofitManager.parseResponse())
                .filter(CheckUtils::notEmpty)
                .map(RetrofitManager::sortByCreateTime)
                .map(list -> list.get(0))
                .compose(RetrofitManager.switchScheduler())
                .subscribe(new BaseObserver<QueryAccessBean>() {
                    @Override
                    public void onNext(@NonNull QueryAccessBean bean) {
                        mAccessId = bean.ID;
                    }
                });
    }

    public static String getToken() {
        return getInstance().mToken;
    }

    public static int getAccessId() {
        return getInstance().mAccessId;
    }

    public static QueryHospitalBean getHospital() {
        return getInstance().mHospitalBean;
    }

    public static HospitalUserBean getLoginAccount() {
        return getInstance().mHospitalUserBean;
    }

    public static String getHospitalCode() {
        QueryHospitalBean hospital = getHospital();
        return hospital == null ? "1000000" : hospital.code;
    }

    public static int getLoginUid() {
        HospitalUserBean loginAccount = getLoginAccount();
        if (loginAccount == null) {
            return -1;
        }
        return loginAccount.id;
    }

    public static String getDeptCode() {
        AccountManager manager = getInstance();
        HospitalUserBean hospitalUser = manager.mHospitalUserBean;
        if (hospitalUser != null) {
            switch (hospitalUser.account_type) {
                case HttpConstant.ACCOUNT_TYPE_WARD:
                    return hospitalUser.account;
                case HttpConstant.ACCOUNT_TYPE_DEPT:
                    if (!TextUtils.isEmpty(manager.mSelectWardCode)) {
                        return manager.mSelectWardCode;
                    } else if (!TextUtils.isEmpty(hospitalUser.ward_code)) {
                        return hospitalUser.ward_code;
                    }
                    break;
            }
        }
        if (manager.mDoctorInfoBean != null) {
            return manager.mDoctorInfoBean.DoctorDept;
        }
        return null;
    }

    public static String getDeptUserIds() {
        AccountManager manager = getInstance();
        if (manager.mHospitalUserBean.account_type != HttpConstant.ACCOUNT_TYPE_DEPT) {
            return manager.mDeptUserIds;
        } else {
            return null;
        }
    }

    public static void setToken(String token) {
        getInstance().mToken = TextUtils.isEmpty(token) ? "123456" : token;
    }

    public static void setHospital(QueryHospitalBean hospital) {
        getInstance().mHospitalBean = hospital;
    }

    private void setDoctorInfoBean(QueryDoctorInfoBean doctorInfoBean) {
        this.mDoctorInfoBean = doctorInfoBean;
    }

    public static void setSelectWardCode(String wardCode) {
        getInstance().mSelectWardCode = wardCode;
    }

    public static Observable<QueryHospitalBean> getHospitalCodeObservable(ComponentCallbacks callbacks,
                                                                          String latitude,
                                                                          String longitude) {
        AtomicReference<QueryHospitalBean> hospitalBeanReference = new AtomicReference<>();
        return RetrofitManager.newHttpBaseServer()
                // 0.根据经纬度获取医院id
                .queryOneHospital(new QueryOneHospitalBody(latitude, longitude, App.LOCATION_DISTANCE))
                .subscribeOn(Schedulers.io())
                .map(RetrofitManager.parseResponse())
                .map(bean -> {
                    if (null == bean) {
                        throw new AhException(ErrorCode.TH_LOGIN_LOCATION, "latitude:" + latitude + ",longitude:" + longitude);
                    }
                    hospitalBeanReference.set(bean);
                    return bean;
                })
                // 1.根据医院id获取医院Identify信息
                .flatMap(bean -> {
                    HospitalIdentifyBody body = new HospitalIdentifyBody();
                    body.hospital_code = bean.code;
                    return RetrofitManager.newThServer().queryHospitalIdentify(body);
                })
                .map(RetrofitManager.parseResponse())
                .map(RetrofitManager::sortByCreateTime)
                .map(list -> {
                    if (list == null || list.isEmpty()) {
                        throw new AhException(ErrorCode.TH_LOGIN_IDENTIFY, "hospital identify is null!");
                    } else {
                        String code = list.get(0).hospital_code;
                        QueryHospitalBean bean = hospitalBeanReference.get();
                        bean.code = code;
                        return code;
                    }
                })
                .flatMap(code -> RetrofitManager.newThServer().queryToken(new QueryTokenBody(code)))
                .map(RetrofitManager.parseResponse())
                // 2.根据医院code获取token
                .map(token -> {
                    SharePreferenceUtils.put(BaseApplication.getContext(), SharePreferenceUtils.SP_TOKEN, token);
                    setToken(token);
                    return token;
                })
                .flatMap(token -> Observable.just(hospitalBeanReference.get()))
                .compose(RetrofitManager.bindLifecycle(callbacks));
    }

    /**
     * 登录
     *
     * @param callbacks callbacks
     * @param account   account
     * @param password  password
     */
    public static Observable<HospitalUserBean> doLogin(ComponentCallbacks callbacks,
                                                       String account,
                                                       String password) {
        if (account == null && password == null) {
            // 4.判断本地登录账号
            DBUserInfoBean userInfo = DBUserInfoUtils.queryLoginUserInfo();
            if (userInfo == null) {
                return Observable.error(new AhException(ErrorCode.TH_LOGIN_USER_NOT_LOGIN, "db user is null!", Log.INFO));
            } else {
                account = userInfo.account;
                password = userInfo.pwd;
            }
        }
        QueryHospitalBean hospitalBean = getHospital();
        QueryLoginBody body = new QueryLoginBody();
        body.account = account;
        body.pwd = password;
        body.hospital_code = body.hospital = hospitalBean.code;
        return getLoginObservable(body)
                .compose(RetrofitManager.switchSchedulerWith(callbacks));
    }

    /**
     * 根据缓存自动登录
     */
    public static boolean doLoginByCache() {
        Context context = BaseApplication.getContext();
        DBUserInfoBean loginUserInfo = DBUserInfoUtils.queryLoginUserInfo();
        if (loginUserInfo == null) {
            return false;
        }
        QueryHospitalBean hospitalBean = ParcelableUtils.getParcelableFromFile(
                CacheTag.USER_GLOBAL, CacheTag.HOSPITAL, QueryHospitalBean.CREATOR);
        LogUtils.e("doLoginByCache hospital:" + hospitalBean);
        if (hospitalBean == null) {
            return false;
        }
        String token = SharePreferenceUtils.get(context, SharePreferenceUtils.SP_TOKEN, "");
        LogUtils.e("doLoginByCache token:" + token);
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        HospitalUserBean hospitalUser = ParcelableUtils.getParcelableFromFile(loginUserInfo.uid,
                CacheTag.LOGIN, HospitalUserBean.CREATOR);
        LogUtils.e("doLoginByCache user:" + hospitalUser);
        if (hospitalUser == null) {
            return false;
        }
        setHospitalInfo(hospitalUser, hospitalBean, token);
        return true;
    }

    private static void setHospitalInfo(HospitalUserBean hospitalUser, QueryHospitalBean hospitalBean, String token) {
        getInstance().mHospitalBean = hospitalBean;
        getInstance().mHospitalUserBean = hospitalUser;
        setToken(token);
    }

    /**
     * 登录Observable
     *
     * @param body body
     * @return Observable
     */
    public static Observable<HospitalUserBean> getLoginObservable(QueryLoginBody body) {
        if (body == null) {
            HospitalUserBean loginAccount = getLoginAccount();
            if (loginAccount != null) {
                body = new QueryLoginBody();
                body.account = loginAccount.account;
                body.pwd = loginAccount.pwd;
                body.hospital_code = body.hospital = loginAccount.hospital_code;
            } else {
                return Observable.error(new AhException(ErrorCode.TH_LOGIN_USER_NOT_ENABLE, "login body is null!", Log.INFO));
            }
        }
        String pwd = body.pwd;
        return getHospitalUserObservable(body)
                .map(bean -> {
                    // th:md5 mn:origin
                    bean.pwd = pwd;
                    setLoginAccount(bean);
                    CacheUtils.saveCache(CacheTag.LOGIN, bean);
                    return bean;
                })
                .flatMap(AccountManager::getAccountInfoObservable)
                .map(bean -> {
                    BuglyUtils.setCrashReportInfo();
                    return bean;
                });
    }

    private static Observable<HospitalUserBean> getHospitalUserObservable(QueryLoginBody body) {
        if (isMnAppCode()) {
            return RetrofitManager.newMnServer().loginAccount(body)
                    .map(RetrofitManager.parseResponse())
                    .map(bean -> {
                        if (bean == null) {
                            throw new AhException(ErrorCode.TH_LOGIN_USER_NOT_EXIST, "login user is null!");
                        }
                        if (bean.account_type != HttpConstant.ACCOUNT_TYPE_WARD) {
                            throw new AhException(ErrorCode.TH_LOGIN_USER_NOT_WARD, "account_type is:" + bean.account_type);
                        }
                        return bean;
                    });
        } else {
            return RetrofitManager.newThServer().loginAccount(body)
                    .map(RetrofitManager.parseResponse())
                    .map(userList -> {
                        Iterator<HospitalUserBean> iterator = userList.iterator();
                        while (iterator.hasNext()) {
                            HospitalUserBean next = iterator.next();
                            if (next.audit_status != HttpConstant.AUDIT_STATE_PASSED
                                    || next.estate != HttpConstant.ESTATE_NORMAL) {
                                iterator.remove();
                            }
                        }
                        return userList;
                    })
                    .map(RetrofitManager::sortByCreateTime)
                    .map(list -> {
                        if (list == null || list.isEmpty()) {
                            throw new AhException(ErrorCode.TH_LOGIN_USER_NOT_EXIST, "login user is empty!");
                        } else {
                            HospitalUserBean bean = list.get(0);
                            if (bean == null) {
                                throw new AhException(ErrorCode.TH_LOGIN_USER_NOT_EXIST, "login user is null!");
                            }
                            return bean;
                        }
                    });
        }
    }

    private static boolean isMnAppCode() {
        return "m_client_n".equals(App.APP_CODE);
    }

    @NonNull
    private static Observable<HospitalUserBean> getAccountInfoObservable(HospitalUserBean hospitalUser) {
        if (hospitalUser.account_type != HttpConstant.ACCOUNT_TYPE_DEPT) {
            QueryHospitalUserBody body = new QueryHospitalUserBody();
            body.hospital_code = AccountManager.getHospitalCode();
            body.audit_status = String.valueOf(HttpConstant.AUDIT_STATE_PASSED);
            body.account_type = String.valueOf(HttpConstant.ACCOUNT_TYPE_DEPT);
            RetrofitManager.newThServer().queryHospitalUser(body)
                    .map(RetrofitManager.parseResponse())
                    .subscribe(new BaseObserver<List<HospitalUserBean>>() {
                        @Override
                        public void onNext(@NonNull List<HospitalUserBean> list) {
                            String userIds = null;
                            if (CheckUtils.notEmpty(list)) {
                                StringBuilder builder = null;
                                for (HospitalUserBean bean : list) {
                                    if (builder == null) {
                                        String id = String.valueOf(bean.id);
                                        builder = new StringBuilder(list.size() * id.length()).append(id);
                                    } else {
                                        builder.append(',').append(bean.id);
                                    }
                                }
                                if (builder != null) {
                                    userIds = builder.toString();
                                }
                            }
                            getInstance().mDeptUserIds = userIds;
                        }
                    });
        }
        switch (hospitalUser.account_type) {
            case HttpConstant.ACCOUNT_TYPE_WARD:
                QueryDepartmentBody deptBody = new QueryDepartmentBody();
                deptBody.token = getToken();
                deptBody.HospitalCode = hospitalUser.hospital_code;
                deptBody.WardCode = hospitalUser.account;
                return RetrofitManager.newThServer().queryDepartment(deptBody)
                        .map(RetrofitManager.parseResponse())
                        .compose(RetrofitManager.switchScheduler())
                        .flatMap((Function<List<QueryDepartmentBean>, ObservableSource<HospitalUserBean>>) list -> {
                            if (CheckUtils.notEmpty(list)) {
                                QueryDepartmentBean bean = list.get(list.size() - 1);
                                if (bean != null) {
                                    hospitalUser.name = bean.DeptShortName;
                                }
                            }
                            return Observable.just(hospitalUser);
                        });
            case HttpConstant.ACCOUNT_TYPE_NURSE:
                QueryDoctorInfoBody docBody = new QueryDoctorInfoBody();
                docBody.token = getToken();
                docBody.HospitalCode = hospitalUser.hospital_code;
                docBody.DoctorCode = hospitalUser.account;
                return RetrofitManager.newThServer().queryDoctorInfo(docBody)
                        .map(RetrofitManager.parseResponse())
                        .compose(RetrofitManager.switchScheduler())
                        .flatMap((Function<List<QueryDoctorInfoBean>, ObservableSource<HospitalUserBean>>) list -> {
                            if (CheckUtils.notEmpty(list)) {
                                QueryDoctorInfoBean bean = list.get(list.size() - 1);
                                hospitalUser.name = bean.DoctorName;
                                getInstance().setDoctorInfoBean(bean);
                            }
                            return Observable.just(hospitalUser);
                        });
            default:
                return Observable.just(hospitalUser);
        }
    }

    /**
     * 设置账号信息
     *
     * @param hospitalUser hospitalUser
     */
    public static void setLoginAccount(HospitalUserBean hospitalUser) {
        AccountManager manager = getInstance();
        manager.mHospitalUserBean = hospitalUser;
        if (hospitalUser != null) {
            DBUserInfoBean userInfoBean = new DBUserInfoBean();
            userInfoBean.hospitalCode = hospitalUser.hospital_code;
            userInfoBean.account = hospitalUser.account;
            userInfoBean.pwd = hospitalUser.pwd;
            userInfoBean.uid = hospitalUser.id;
            userInfoBean.accountType = hospitalUser.account_type;
            userInfoBean.name = hospitalUser.name;
            if (TextUtils.isEmpty(hospitalUser.sex)) {
                hospitalUser.sex = "1";
            }
            DBUserInfoUtils.insertUserInfo(userInfoBean);
        }
    }

    /**
     * 判断是否展示菜单
     *
     * @param role 当前菜单role值
     */
    public static boolean isPermissionDenied(int role) {
        int type = getLoginAccount().account_type;
        switch (type) {
            case HttpConstant.ACCOUNT_TYPE_NURSE:
                return role != HttpConstant.ACCOUNT_TYPE_NURSE;
            case HttpConstant.ACCOUNT_TYPE_WARD:
                return role != HttpConstant.ACCOUNT_TYPE_NURSE && role != HttpConstant.ACCOUNT_TYPE_WARD;
            case HttpConstant.ACCOUNT_TYPE_DEPT:
                return role != HttpConstant.ACCOUNT_TYPE_NURSE && role != HttpConstant.ACCOUNT_TYPE_WARD && role != HttpConstant.ACCOUNT_TYPE_DEPT;
            default:
                return true;
        }
    }

    /**
     * @return 判断是否是护理部账号, 并且清除选中科室
     */
    public static boolean isDeptAccountAndClearSelectWardCode() {
        HospitalUserBean loginAccount = AccountManager.getLoginAccount();
        if (loginAccount == null) {
            return false;
        }
        boolean isDept = loginAccount.account_type == HttpConstant.ACCOUNT_TYPE_DEPT;
        if (isDept) {
            AccountManager.setSelectWardCode(null);
        }
        return isDept;
    }

    /**
     * 登出
     */
    public static void doLogout() {
        DBUserInfoUtils.updateUserInfoLogout(getLoginUid());
        AccountManager.getInstance().clearLoginAccount();
    }

    /**
     * 清除登录信息
     */
    private void clearLoginAccount() {
        CacheUtils.clearCache();
        Context context = BaseApplication.getContext();
        SharePreferenceUtils.remove(context, SharePreferenceUtils.SP_TH_LOCATION_INFO);
//        mHospitalBean = null;
        mHospitalUserBean = null;
        mDoctorInfoBean = null;
    }
}
