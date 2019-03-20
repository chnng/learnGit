package com.learn.git.ui.fragment;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.aihui.lib.agentweb.AgentWebManager;
import com.aihui.lib.base.api.eventbus.EventBusUtils;
import com.aihui.lib.base.api.eventbus.EventTag;
import com.aihui.lib.base.ui.BaseFragment;
import com.just.library.AgentWebUtils;
import com.learn.git.R;

import butterknife.BindView;

/**
 * Created by 胡一鸣 on 2018/9/11.
 */
public class WebFragment extends BaseFragment {
    @BindView(R.id.web_view)
    RelativeLayout frameLayout;
    private AgentWebManager agentWebManager;
    //    WebView webView;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_web;
    }

    @Override
    public void initData() {
//        WebSettings settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        webView.loadUrl("https://hl.smartsky-tech.com:8095/index/index?hospitalId=1000000&app=m_client_a");
        AgentWebUtils.clearWebViewAllCache(getContext());
        agentWebManager = new AgentWebManager(getActivity(), frameLayout);
        agentWebManager.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //延迟0.5秒隐藏，遮盖webView内置的加载错误页面
//                Executors.newCachedThreadPool().execute(() -> {
//                    if (isClickCancelBtn) {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        runOnUiThread(() -> previousView.setVisibility(View.INVISIBLE));
//                        isClickCancelBtn = false;
//                    }
//                });

                // autoPlay when finished loading via javascript injection
                view.loadUrl("javascript:(" +
                        "function() {" +
                        " var videos = document.getElementsByTagName('video');" +
                        " for(var i=0;i<videos.length;i++){" +
                        "videos[i].play();" +
                        "}" +
                        "}" +
                        ")()");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
//                if (errorCode == -2) {
//                    disposeFail(R.string.network_error);
//                } else {
//                    disposeFail(R.string.sever_error);
//                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        agentWebManager.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //更新进度条
                EventBusUtils.post(EventTag.UPDATE_PROGRESS_BAR, newProgress);
            }
        });
//        agentWebManager.loadWeb("https://hl.smartsky-tech.com:8095/index/index?hospitalId=1000000&app=m_client_a", "js", new AndroidInterface());
        agentWebManager.loadWeb("https://api.perffun.com:446/tvn/U20170110041218", "js", new AndroidInterface());
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onResume() {
        super.onResume();
        agentWebManager.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        agentWebManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        agentWebManager.onDestroy();
    }

    class AndroidInterface {
        @JavascriptInterface//javaScript
        public void scanQrCodeByJS() { //原方法名 requestCodeQRCodePermissions
        }
    }
}
