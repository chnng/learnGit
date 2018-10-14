package com.learn.git.util;

import android.support.annotation.NonNull;

import com.aihui.lib.base.constant.FileType;
import com.aihui.lib.base.util.FileUtils;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

/**
 * Created by 胡一鸣 on 2018/9/21.
 */
public final class DocumentUtils {

    public static String readDocument(@NonNull File file) {
        String fileType = FileUtils.getFileType(file.getName());
        String content = null;
        try {
            if (FileType.Extension.TXT.equalsIgnoreCase(fileType)) {
                content = readTxt(file);
            } else if (FileType.Extension.DOC.equalsIgnoreCase(fileType)) {
                content = readWord(file, false);
            } else if (FileType.Extension.DOCX.equalsIgnoreCase(fileType)) {
                content = readWord(file, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private static String readTxt(@NonNull File file) throws IOException {
        try (BufferedSource source = Okio.buffer(Okio.source(file))) {
            return source.readString(Charset.forName("UTF-8"));
        }
    }

    private static String readWord(@NonNull File file, boolean isDocx) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            if (isDocx) {
                XWPFDocument xdoc = new XWPFDocument(in);
                XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
                return extractor.getText();
            } else {
                HWPFDocument doc = new HWPFDocument(in);
                return doc.getDocumentText();
            }
        }
    }


    /**
     * https://blog.csdn.net/u011916937/article/details/50085441
     * demoFile 模板文件
     * newFile 生成文件
     * map 要填充的数据
     */
    public void writeDoc(File demoFile, File newFile, Map<String, String> map) {
        try {
            FileInputStream in = new FileInputStream(demoFile);
            HWPFDocument hdt = new HWPFDocument(in);
            // Fields fields = hdt.getFields();
            // 读取word文本内容
            Range range = hdt.getRange();
            // System.out.println(range.text());

            // 替换文本内容
            for (Map.Entry<String, String> entry : map.entrySet()) {
                range.replaceText(entry.getKey(), entry.getValue());
            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            FileOutputStream out = new FileOutputStream(newFile, true);
            hdt.write(ostream);
            // 输出字节流
            out.write(ostream.toByteArray());
            out.close();
            ostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
