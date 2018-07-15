package com.learn.git.ui.fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.learn.git.R;
import com.learn.git.api.retrofit.RetrofitHelper;
import com.learn.git.server.bean.base.BaseResponse;
import com.learn.git.server.bean.request.QueryMissionListBody;
import com.learn.git.server.bean.request.QueryMissionListItemBody;
import com.learn.git.server.bean.response.QueryMissionListBean;
import com.learn.git.server.bean.response.QueryMissionListItemBean;
import com.learn.git.ui.base.BaseFragment;
import com.learn.git.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class RetrofitFragment extends BaseFragment {
    @BindView(R.id.textView_response)
    TextView tvResponse;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public int getContentViewId() {
        return R.layout.fragment_test;
    }

    @Override
    public void onCreate() {
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
//                    observables[i] = RetrofitHelper.getServer().getToken(new TokenRequest());
//                    observableList.add(observables[i]);
//                }
//                DisposableObserver<String> disposableObserver = Observable.mergeDelayError(observableList)
//                        .compose(RetrofitHelper.resultTransformer())
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

//                Observable<BaseResponse<String>> token = RetrofitHelper.getServer()
//                        .getToken(new TokenRequest());
//                DisposableObserver<String> disposableObserver = token
//                        .compose(RetrofitHelper.resultTransformer())
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
        mCompositeDisposable.add(RetrofitHelper.getServer().queryMissionList(new QueryMissionListBody())
                .compose(RetrofitHelper.resultTransFragment(this))
                .flatMap((Function<List<QueryMissionListBean>, ObservableSource<List<QueryMissionListItemBean>>>) queryMissionListBeans -> {
//                    LogUtil.d("queryMissionListBeans" + queryMissionListBeans.toString());
                    missionList = queryMissionListBeans;
                    List<Observable<BaseResponse<List<QueryMissionListItemBean>>>> list = new ArrayList<>();
                    for (QueryMissionListBean queryMissionListBean : queryMissionListBeans) {
                        QueryMissionListItemBody itemBody = new QueryMissionListItemBody();
                        itemBody.BedNumber = queryMissionListBean.identify_code;
                        list.add(RetrofitHelper.getServer().queryMissionListItem(itemBody));
                    }
                    return Observable.concatDelayError(list).compose(RetrofitHelper.resultTransFragment(this));
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
                        LogUtil.e("" + (System.currentTimeMillis() - duration));
                        tvResponse.setText("duration:" + (System.currentTimeMillis() - duration) + "\n" + missionList);
                    }
                }));
    }
}
