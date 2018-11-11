package com.aihui.lib.nurse.manager;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.aihui.lib.base.api.retrofit.BaseObserver;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.App;
import com.aihui.lib.base.cons.CacheTag;
import com.aihui.lib.base.cons.HttpConstant;
import com.aihui.lib.base.model.common.response.QueryHospitalBean;
import com.aihui.lib.base.model.module.nurse.request.QueryLoginBody;
import com.aihui.lib.base.model.module.nurse.response.HospitalUserBean;
import com.aihui.lib.base.model.module.th.main.request.QueryAccessBody;
import com.aihui.lib.base.model.module.th.main.request.QueryDepartmentBody;
import com.aihui.lib.base.model.module.th.main.request.QueryDoctorInfoBody;
import com.aihui.lib.base.model.module.th.main.request.QueryHospitalUserBody;
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
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

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

    private static AccountManager get() {
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
        mToken = (String) SharePreferenceUtils.get(
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
                    public void onNext(QueryAccessBean bean) {
                        mAccessId = bean.ID;
                    }
                });
    }

    public static String getToken() {
        return get().mToken;
    }

    public static int getAccessId() {
        return get().mAccessId;
    }

    public static QueryHospitalBean getHospital() {
        return get().mHospitalBean;
    }

    public static HospitalUserBean getLoginAccount() {
        return get().mHospitalUserBean;
    }

    public static String getHospitalCode() {
        HospitalUserBean loginAccount = getLoginAccount();
        return loginAccount == null ? null : loginAccount.hospital_code;
    }

    public static int getLoginUid() {
        HospitalUserBean loginAccount = getLoginAccount();
        if (loginAccount == null) {
            return -1;
        }
        return loginAccount.id;
    }

    public static String getDeptCode() {
        AccountManager manager = get();
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
        AccountManager manager = get();
        if (manager.mHospitalUserBean.account_type != HttpConstant.ACCOUNT_TYPE_DEPT) {
            return manager.mDeptUserIds;
        } else {
            return null;
        }
    }

    public static void setToken(String token) {
        get().mToken = TextUtils.isEmpty(token) ? "123456" : token;
    }

    private void setDoctorInfoBean(QueryDoctorInfoBean doctorInfoBean) {
        this.mDoctorInfoBean = doctorInfoBean;
    }

    public static void setSelectWardCode(String wardCode) {
        get().mSelectWardCode = wardCode;
    }

    /**
     * 登录
     *
     * @param activity     activity
     * @param hospitalBean hospitalBean
     * @param account      account
     * @param password     password
     */
    public static Observable<HospitalUserBean> doLogin(RxAppCompatActivity activity,
                                                       QueryHospitalBean hospitalBean,
                                                       String account,
                                                       String password) {
        QueryLoginBody body = new QueryLoginBody();
        body.account = account;
        body.pwd = password;
        body.hospital_code = body.hospital = hospitalBean.code;
        return getLoginObservable(body)
                .compose(RetrofitManager.switchSchedulerWith(activity))
                .map(bean -> {
                    get().mHospitalBean = hospitalBean;
                    return bean;
                });
    }

    /**
     * 根据缓存自动登录
     *
     * @param activity activity
     */
    public static boolean doLoginByCache(Activity activity) {
        long loginTimestamp = (long) SharePreferenceUtils.get(activity, CacheTag.LOGIN, 0L);
        LogUtils.e("doLoginByCache time:" + loginTimestamp);
        if (loginTimestamp == 0
                || System.currentTimeMillis() / 1000 - loginTimestamp > TimeUnit.HOURS.toSeconds(1)) {
            return false;
        }
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
        String token = (String) SharePreferenceUtils.get(activity, CacheTag.TOKEN, "");
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
        get().mHospitalBean = hospitalBean;
        get().mHospitalUserBean = hospitalUser;
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
                return Observable.error(new IllegalStateException("login body is null!"));
            }
        }
        return getHospitalUserObservable(body)
                .map(bean -> {
                    setLoginAccount(bean);
                    CacheUtils.saveCache(CacheTag.LOGIN, bean);
                    SharePreferenceUtils.put(BaseApplication.getContext(), CacheTag.LOGIN, System.currentTimeMillis() / 1000);
                    return bean;
                })
                .flatMap(AccountManager::getAccountInfoObservable)
                .map(bean -> {
                    BuglyUtils.setCrashReportInfo();
                    return bean;
                });
    }

    private static Observable<HospitalUserBean> getHospitalUserObservable(QueryLoginBody body) {
        if ("m_client_th".equals(App.APP_CODE)) {
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
                            throw new IllegalStateException("code:-1,login user is null!");
                        } else {
                            HospitalUserBean bean = list.get(0);
                            if (bean == null) {
                                throw new IllegalStateException("code:-2,login user is null!");
                            }
                            return bean;
                        }
                    });
        } else {
            return RetrofitManager.newMnServer().loginAccount(body)
                    .map(RetrofitManager.parseResponse())
                    .map(bean -> {
                        if (bean == null) {
                            throw new IllegalStateException("code:-3,login user is null!");
                        }
                        return bean;
                    });
        }
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
                    .map(list -> {
                        String UserIds = null;
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
                                UserIds = builder.toString();
                            }
                        }
                        return UserIds;
                    })
                    .compose(RetrofitManager.switchScheduler())
                    .subscribe(new BaseObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            get().mDeptUserIds = s;
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
                                get().setDoctorInfoBean(bean);
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
        AccountManager manager = get();
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
        AccountManager.get().clearLoginAccount();
    }

    /**
     * 清除登录信息
     */
    private void clearLoginAccount() {
        CacheUtils.clearCache();
        SharePreferenceUtils.put(BaseApplication.getContext(), CacheTag.LOGIN, 0L);
        mHospitalBean = null;
        mHospitalUserBean = null;
        mDoctorInfoBean = null;
    }
}