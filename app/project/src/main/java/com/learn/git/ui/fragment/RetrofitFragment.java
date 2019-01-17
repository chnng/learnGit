package com.learn.git.ui.fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.aihui.lib.base.util.LogUtils;
import com.learn.git.R;
import com.learn.git.api.retrofit.RetrofitManager;
import com.learn.git.bean.base.BaseResponse;
import com.learn.git.bean.request.QueryMissionListBody;
import com.learn.git.bean.request.QueryMissionListItemBody;
import com.learn.git.bean.response.QueryMissionListBean;
import com.learn.git.bean.response.QueryMissionListItemBean;
import com.learn.git.ui.common.MyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class RetrofitFragment extends MyFragment {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void initData() {
        super.initData();
        tvResponse.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnClick({R.id.button_request, R.id.button_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_request:
                request();
//                Observable<BaseResponse<String>>[] observables = new Observable[5];
//                List<Observable<BaseResponse<String>>> observableList = new ArrayList<>();
//                for (int i = 0; i < observables.length; i++) {
//                    observables[i] = RetrofitManager.getHttpServer().getToken(new TokenRequest());
//                    observableList.add(observables[i]);
//                }
//                DisposableObserver<String> disposableObserver = Observable.mergeDelayError(observableList)
//                        .compose(RetrofitManager.resultTransformer())
//                        .subscribeWith(new DisposableObserver<String>() {
//                            @Override
//                            public void onNext(String s) {
//                                LogUtil.d(s);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//                                LogUtil.d(null);
//                            }
//                        });

//                Observable<BaseResponse<String>> token = RetrofitManager.getHttpServer()
//                        .getToken(new TokenRequest());
//                DisposableObserver<String> disposableObserver = token
//                        .compose(RetrofitManager.resultTransformer())
//                        .subscribeWith(new DisposableObserver<String>() {
//                            @Override
//                            public void onNext(String s) {
//                                LogUtil.d(s);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
//                mCompositeDisposable.add(disposableObserver);
                break;
            case R.id.button_cancel:
                mCompositeDisposable.dispose();
                break;
        }
    }

    List<QueryMissionListBean> missionList;
    private long duration;
    private void request() {
        duration = System.currentTimeMillis();
        mCompositeDisposable.add(RetrofitManager.getHttpServer().queryMissionList(new QueryMissionListBody())
                .compose(RetrofitManager.resultTransFragment(this))
                .flatMap((Function<List<QueryMissionListBean>, ObservableSource<List<QueryMissionListItemBean>>>) queryMissionListBeans -> {
//                    LogUtil.d("queryMissionListBeans" + queryMissionListBeans.toString());
                    missionList = queryMissionListBeans;
                    List<Observable<BaseResponse<List<QueryMissionListItemBean>>>> list = new ArrayList<>();
                    for (QueryMissionListBean queryMissionListBean : queryMissionListBeans) {
                        QueryMissionListItemBody itemBody = new QueryMissionListItemBody();
                        itemBody.BedNumber = queryMissionListBean.identify_code;
                        list.add(RetrofitManager.getHttpServer().queryMissionListItem(itemBody));
                    }
                    return Observable.concatDelayError(list).compose(RetrofitManager.resultTransFragment(this));
                })
                .subscribeWith(new DisposableObserver<List<QueryMissionListItemBean>>() {
                    @Override
                    public void onNext(List<QueryMissionListItemBean> queryMissionListItemBeans) {
//                        LogUtil.d(queryMissionListItemBeans.size() + "");
                        for (QueryMissionListBean listBean : missionList) {
                            if (listBean.itemList == null) {
                                listBean.itemList = queryMissionListItemBeans;
                                break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        LogUtil.d(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.e("" + (System.currentTimeMillis() - duration));
                        tvResponse.setText("duration:" + (System.currentTimeMillis() - duration) + "\n" + missionList);
                    }
                }));
    }
}
