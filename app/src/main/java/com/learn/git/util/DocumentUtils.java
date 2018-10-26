package com.learn.git.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.aihui.lib.base.app.BaseApplication;
import com.aihui.lib.base.cons.FileType;
import com.aihui.lib.base.util.FileUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

/**
 * Created by 胡一鸣 on 2018/9/21.
 */
public final class DocumentUtils {

    public static String readDocument(@NonNull File file) {
        String fileType = FileUtils.getExtension(file.getName());
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
    public static void formatDoc(@NonNull String assetsName,
                                 @NonNull File newFile,
                                 Map<String, String> map) throws IOException {
        ByteArrayOutputStream ostream = null;
        FileOutputStream out = null;
        try {
//            FileInputStream in = new FileInputStream(demoFile);
            //获取模板文件
            InputStream open = BaseApplication.getContext().getAssets().open(assetsName);
            HWPFDocument hdt = new HWPFDocument(open);
            // Fields fields = hdt.getFields();
            // 读取word文本内容
            Range range = hdt.getRange();
            // 替换文本内容
            for (Map.Entry<String, String> entry : map.entrySet()) {
                range.replaceText(entry.getKey(), entry.getValue());
            }
            ostream = new ByteArrayOutputStream();
            out = new FileOutputStream(newFile);
            hdt.write(ostream);
            // 输出字节流
            out.write(ostream.toByteArray());
        } finally {
            if (out != null) {
                out.close();
            }
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    public static void createExcel(@NonNull File newFile,
                                   String title,
                                   String[][] content) throws IOException {
        OutputStream outputStream = new FileOutputStream(newFile);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sheet1");

        HSSFRow hssfRow = sheet.createRow(0);
        hssfRow.setHeightInPoints(30); // 设置行的高度
        Region region = new Region(0, (short) 0, 0, (short) 4);
        sheet.addMergedRegion(region);
        sheet.setColumnWidth(1, 60 * 256);

        HSSFCellStyle styleCenter = workbook.createCellStyle();
        styleCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平
        styleCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中

        if (!TextUtils.isEmpty(title)) {
            HSSFCell cell = hssfRow.createCell(0);
            cell.setCellValue(title);
            HSSFCellStyle styleTitle = workbook.createCellStyle();
            styleTitle.cloneStyleFrom(styleCenter);
            HSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 12);// 设置字体大小
            styleTitle.setFont(font);
            cell.setCellStyle(styleTitle);
        }
        HSSFCellStyle styleContent = workbook.createCellStyle();
        styleContent.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
        for (int i = 0; i < content.length; i++) {
            String[] row = content[i];
            hssfRow = sheet.createRow(i + 1);
            hssfRow.setHeightInPoints(18);
            for (int j = 0; j < row.length; j++) {
                HSSFCell itemCell = hssfRow.createCell(j);
                itemCell.setCellValue(row[j]);
                if (i == 0 || j != 1) {
                    itemCell.setCellStyle(styleCenter);
                } else {
                    itemCell.setCellStyle(styleContent);
                }
            }
        }


//        // 日期格式化
//        HSSFCellStyle cellStyle2 = workbook.createCellStyle();
//        HSSFCreationHelper creationHelper = workbook.getCreationHelper();
//        cellStyle2.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
//        sheet.setColumnWidth(2, 20 * 256); // 设置列的宽度
//
//        HSSFCell cell2 = row1.createCell(2);
//        cell2.setCellStyle(cellStyle2);
//        cell2.setCellValue(new Date());
//
//        row1.createCell(3).setCellValue(2);
//
//
//        // 保留两位小数
//        HSSFCellStyle cellStyle3 = workbook.createCellStyle();
//        cellStyle3.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
//        HSSFCell cell4 = row1.createCell(4);
//        cell4.setCellStyle(cellStyle3);
//        cell4.setCellValue(29.5);
//
//
//        // 货币格式化
//        HSSFCellStyle cellStyle4 = workbook.createCellStyle();
//        HSSFFont font = workbook.createFont();
//        font.setFontName("华文行楷");
//        font.setFontHeightInPoints((short) 15);
//        font.setColor(HSSFColor.RED.index);
//        cellStyle4.setFont(font);
//
//        HSSFCell cell5 = row1.createCell(5);
//        cell5.setCellFormula("D2*E2");  // 设置计算公式
//
//        // 获取计算公式的值
//        HSSFFormulaEvaluator e = new HSSFFormulaEvaluator(workbook);
//        cell5 = e.evaluateInCell(cell5);
//        System.out.println(cell5.getNumericCellValue());


        workbook.setActiveSheet(0);
        workbook.write(outputStream);
        outputStream.close();
    }
}
