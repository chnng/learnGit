package com.learn.git.ui.fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.View;

import com.learn.git.R;
import com.learn.git.ui.common.MyFragment;

import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

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

    private void request() {
    }
}
