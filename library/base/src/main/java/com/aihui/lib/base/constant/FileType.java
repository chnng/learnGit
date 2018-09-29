package com.aihui.lib.base.constant;

/**
 * Created by 胡一鸣 on 2018/9/21.
 * http://www.w3school.com.cn/media/media_mimeref.asp
 * http://tool.oschina.net/commons
 * https://www.freeformatter.com/mime-types-list.html
 */
public final class FileType {

    public static final class Extension {
        // media
        public static final String AVI = ".avi";
        public static final String FLV = ".flv";
        public static final String MP3 = ".mp3";
        public static final String MP4 = ".mp4";
        public static final String MOV = ".mov";
        // image
        public static final String BMP = ".bmp";
        public static final String PNG = ".png";
        public static final String JPG = ".jpg";
        public static final String JPEG	= ".jpeg";
        // document
        public static final String TXT = ".txt";
        public static final String HTML = ".html";
        public static final String PDF = ".pdf";
        public static final String JSON = ".json";
        public static final String XML = ".xml";
        public static final String ZIP	= ".zip";
        // office
        public static final String DOC = ".doc";
        public static final String DOCX = ".docx";
        public static final String XLS = ".xls";
        public static final String XLSX = ".xlsx";
        public static final String PPT = ".ppt";
        public static final String PPTX = ".pptx";
    }

    public static final class Mime {
        // media
        public static final String AVI = "video/x-msvideo";
        public static final String FLV = "video/x-flv";
        public static final String MP3 = "audio/mpeg";
        public static final String MP4 = "video/mp4";
        public static final String MOV = "video/quicktime";
        // image
        public static final String BMP = "image/bmp";
        public static final String PNG = "image/png";
        public static final String JPEG	= "image/jpeg";
        // document
        public static final String TXT = "text/plain";
        public static final String HTML = "text/html";
        public static final String PDF = "application/pdf";
        public static final String JSON = "application/json";
        public static final String XML = "application/xml";
        public static final String ZIP	= "application/zip";
        // office
        public static final String DOC = "application/msword";
        public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        public static final String XLS = "application/vnd.ms-excel";
        public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        public static final String PPT = "application/vnd.ms-powerpoint";
        public static final String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }
}
