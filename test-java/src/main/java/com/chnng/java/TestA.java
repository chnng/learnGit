package com.chnng.java;

import org.omg.CORBA.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class TestA {
  static {
    System.out.println("A in static");
  }

  public TestA() {
    System.out.println("A constructor");
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url("http://xianyu-video.alicdn" +
            ".com/1527082135228.813232").addHeader("User-Agent", "Lavf/57.25.100").addHeader
            ("Accept", "*/*").addHeader("Connection", "close").addHeader("Host", "xianyu-video" +
            ".alicdn.com").addHeader("Icy-MetaData", "1").build();
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        System.out.println("onFailure:" + e.getMessage());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        System.out.println("onResponse:" + response.code());
        //        InputStream is = null;
        //        byte[] buf = new byte[2048];
        //        int len = 0;
        //        FileOutputStream fos = null;
        //        // 储存下载文件的目录
        //        String savePath = isExistDir("video");
        //        try {
        //          is = response.body().byteStream();
        //          long total = response.body().contentLength();
        //          File file = new File(savePath, "a.mp4");
        //          fos = new FileOutputStream(file);
        //          long sum = 0;
        //          while ((len = is.read(buf)) != -1) {
        //            fos.write(buf, 0, len);
        //            sum += len;
        //            int progress = (int) (sum * 1.0f / total * 100);
        //            // // 下载中
        //            // listener.onDownloading(progress);
        //            System.out.println("progress:" + progress);
        //          }
        //          fos.flush();
        //          //          // 下载完成
        //          //          listener.onDownloadSuccess();
        //          System.out.println("onDownloadSuccess");
        //        } catch (Exception e) {
        //          //          listener.onDownloadFailed();
        //          System.out.println("onDownloadFailed");
        //        } finally {
        //          try {
        //            if (is != null) is.close();
        //          } catch (IOException e) {
        //          }
        //          try {
        //            if (fos != null) fos.close();
        //          } catch (IOException e) {
        //          }
        //        }

        byte[] buf = new byte[8 * 1024];
        int len, progress;
        long sum = 0;
        String savePath = isExistDir("video");
        File file = new File(savePath, "redminote.mp4");
        ResponseBody body = response.body();
        long total = body.contentLength();
        System.out.println("contentLength:" + total);
        try (BufferedSource source = Okio.buffer(Okio.source(body.byteStream()));
             BufferedSink sink = Okio.buffer(Okio.sink(file))) {
          while ((len = source.read(buf)) != -1) {
            sum += len;
            progress = (int) (sum * 1.0f / total * 100);
            sink.write(buf, 0, len);
            System.out.println("progress:" + progress);
          }
        }
      }
    });
  }

  /**
   * @param saveDir
   * @return
   * @throws IOException 判断下载目录是否存在
   */
  private String isExistDir(String saveDir) throws IOException {
    // 下载位置
    File downloadFile = new File(saveDir);
    if (!downloadFile.mkdirs()) {
      downloadFile.createNewFile();
    }
    String savePath = downloadFile.getAbsolutePath();
    return savePath;
  }

  /**
   * @param url
   * @return 从下载连接中解析出文件名
   */
  private String getNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }
}
