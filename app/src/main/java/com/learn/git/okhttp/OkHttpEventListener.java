package com.learn.git.okhttp;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/03/08.
 */

public class OkHttpEventListener extends EventListener {
  private void log(String msg) {
    Log.d("OkHttpEventListener", msg);
  }

  @Override
  public void callStart(Call call) {
    super.callStart(call);
    log("callStart");
  }

  @Override
  public void dnsStart(Call call, String domainName) {
    super.dnsStart(call, domainName);
    log("dnsStart: " + domainName);
  }

  @Override
  public void dnsEnd(Call call, String domainName, List<InetAddress> inetAddressList) {
    super.dnsEnd(call, domainName, inetAddressList);
    log("dnsEnd:" + inetAddressList.toString());
  }

  @Override
  public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
    super.connectStart(call, inetSocketAddress, proxy);
    log("connectStart:" + inetSocketAddress.toString());
  }

  @Override
  public void secureConnectStart(Call call) {
    super.secureConnectStart(call);
    log("secureConnectStart");
  }

  @Override
  public void secureConnectEnd(Call call, @Nullable Handshake handshake) {
    super.secureConnectEnd(call, handshake);
    log("secureConnectEnd");
  }

  @Override
  public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, @Nullable
          Protocol protocol) {
    super.connectEnd(call, inetSocketAddress, proxy, protocol);
    log("connectEnd");
  }

  @Override
  public void connectFailed(Call call, InetSocketAddress inetSocketAddress, Proxy proxy,
                            @Nullable Protocol protocol, IOException ioe) {
    super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
    log("connectFailed");
  }

  @Override
  public void connectionAcquired(Call call, Connection connection) {
    super.connectionAcquired(call, connection);
    log("connectionAcquired");
  }

  @Override
  public void connectionReleased(Call call, Connection connection) {
    super.connectionReleased(call, connection);
    log("connectionReleased");
  }

  @Override
  public void requestHeadersStart(Call call) {
    super.requestHeadersStart(call);
    log("requestHeadersStart");
  }

  @Override
  public void requestHeadersEnd(Call call, Request request) {
    super.requestHeadersEnd(call, request);
    log("requestHeadersEnd");
  }

  @Override
  public void requestBodyStart(Call call) {
    super.requestBodyStart(call);
    log("requestBodyStart");
  }

  @Override
  public void requestBodyEnd(Call call, long byteCount) {
    super.requestBodyEnd(call, byteCount);
    log("requestBodyEnd");
  }

  @Override
  public void responseHeadersStart(Call call) {
    super.responseHeadersStart(call);
    log("responseHeadersStart");
  }

  @Override
  public void responseHeadersEnd(Call call, Response response) {
    super.responseHeadersEnd(call, response);
    log("responseHeadersEnd");
  }

  @Override
  public void responseBodyStart(Call call) {
    super.responseBodyStart(call);
    log("responseBodyStart");
  }

  @Override
  public void responseBodyEnd(Call call, long byteCount) {
    super.responseBodyEnd(call, byteCount);
    log("responseBodyEnd");
  }

  @Override
  public void callEnd(Call call) {
    super.callEnd(call);
    log("callEnd");
  }

  @Override
  public void callFailed(Call call, IOException ioe) {
    super.callFailed(call, ioe);
    log("callFailed");
  }
}
