package com.chnng.java.okhttp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by Administrator on 2018/03/09.
 */

public class OkioTest {
  private static final ByteString PNG_HEADER = ByteString.decodeHex("89504e470d0a1a0a");

  public void decodePng(InputStream in) throws IOException {
    Source source = Okio.source(in);
    try (BufferedSource pngSource = Okio.buffer(source)) {
      ByteString header = pngSource.readByteString(PNG_HEADER.size());
      if (!header.equals(PNG_HEADER)) {
        throw new IOException("Not a PNG.");
      }

      while (true) {
        Buffer chunk = new Buffer();

        // Each chunk is a length, type, data, and CRC offset.
        int length = pngSource.readInt();
        String type = pngSource.readUtf8(4);
        pngSource.readFully(chunk, length);
        int crc = pngSource.readInt();

        decodeChunk(type, chunk);
        if (type.equals("IEND")) break;
      }
    }
  }

  private void decodeChunk(String type, Buffer chunk) {
    if (type.equals("IHDR")) {
      int width = chunk.readInt();
      int height = chunk.readInt();
      System.out.printf("%08x: %s %d x %d%n", chunk.size(), type, width, height);
    } else {
      System.out.printf("%08x: %s%n", chunk.size(), type);
    }
  }

  public void readLines0(File file) throws IOException {
    try (Source fileSource = Okio.source(file);
         BufferedSource bufferedSource = Okio.buffer(fileSource)) {

      while (true) {
        String line = bufferedSource.readUtf8Line();
        if (line == null) break;

        if (line.contains("square")) {
          System.out.println(line);
        }
      }

    }
  }

  public void readLines1(File file) throws IOException {
    try (BufferedSource source = Okio.buffer(Okio.source(file))) {
      for (String line; (line = source.readUtf8Line()) != null; ) {
        if (line.contains("square")) {
          System.out.println(line);
        }
      }
    }
  }

  public void readLines2(File file) throws IOException {
    try (BufferedSource source = Okio.buffer(Okio.source(file))) {
      while (!source.exhausted()) {
        String line = source.readUtf8LineStrict(1024L);
        if (line.contains("square")) {
          System.out.println(line);
        }
      }
    }
  }

  public void writeEnv0(File file) throws IOException {
    try (Sink fileSink = Okio.sink(file);
         BufferedSink bufferedSink = Okio.buffer(fileSink)) {

      for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
        bufferedSink.writeUtf8(entry.getKey());
        bufferedSink.writeUtf8("=");
        bufferedSink.writeUtf8(entry.getValue());
        bufferedSink.writeUtf8("\n");
      }

    }
  }

  public void writeEnv1(File file) throws IOException {
    try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
      for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
        sink.writeUtf8(entry.getKey())
                .writeUtf8("=")
                .writeUtf8(entry.getValue())
                .writeUtf8("\n");
      }
    }
  }
}
