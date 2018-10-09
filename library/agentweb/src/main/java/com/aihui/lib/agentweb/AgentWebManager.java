package com.aihui.lib.agentweb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.just.library.AgentWeb;

/**
 * Created by 路传涛 on 2017/7/18.
 * 参数设置
 */

public class AgentWebManager {

    private Activity mContext;
    private AgentWeb mAgentWeb;
    private AgentWeb.PreAgentWeb preAgentWeb;
    private RelativeLayout mRelativeLayout;
    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    private boolean mIsWebTransparent;

    public AgentWebManager(Activity mContext, RelativeLayout mRelativeLayout) {
        this.mContext = mContext;
        this.mRelativeLayout = mRelativeLayout;
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        this.mWebChromeClient = webChromeClient;
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        this.mWebViewClient = webViewClient;
    }

    public AgentWeb loadWeb(String webUrl, String mark, Object object) {
        if (null == preAgentWeb) {
            preAgentWeb = AgentWeb.with(mContext)
                    .setAgentWebParent(mRelativeLayout, new RelativeLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .defaultProgressBarColor()
                    .setWebChromeClient(mWebChromeClient)
                    .setWebViewClient(mWebViewClient)
                    .setSecutityType(AgentWeb.SecurityType.strict)
                    .createAgentWeb()
                    .ready();
        }
        mAgentWeb = preAgentWeb.go(webUrl);
        mAgentWeb.getJsInterfaceHolder().addJavaObject(mark, object);
        WebSettings settings = mAgentWeb.getAgentWebSettings().getWebSettings();
        settings.setUseWideViewPort(false);
        settings.setLoadWithOverviewMode(false);
        if (mIsWebTransparent) {
            WebView webView = mAgentWeb.getWebCreator().get();
            webView.setBackgroundColor(Color.TRANSPARENT);
            ((View) webView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        }
        return mAgentWeb;
    }

    public void reloadWeb() {
        if (mAgentWeb != null) {
            mAgentWeb.getLoader().reload();
        }
    }

    public boolean handleKeyEvent(int keyCode, KeyEvent event) {
        return mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event);
    }

    public void callJsMethod(String methodName, String... params) {
        if (null != mAgentWeb) {
            mAgentWeb.getJsEntraceAccess().quickCallJs(methodName, params);
        }
    }

    public void onPause() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    public void onResume() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    public void onDestroy() {
        if (mAgentWeb != null) {
            mAgentWeb.clearWebCache();
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAgentWeb != null) {
            mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        }
    }

    public void setTransparentBackground() {
        mIsWebTransparent = true;
    }
}
