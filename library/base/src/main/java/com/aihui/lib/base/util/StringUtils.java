package com.aihui.lib.base.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.util.Locale;

/**
 * Created by aihui087 on 2017/9/8.
 */

public final class StringUtils {
    public static String escapedHtmlString(String str) {
        if (!TextUtils.isEmpty(str)) {
            str = str.replace("&quot;", "\"")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&nbsp;", " ");
        }
        return str;
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    public static String md5(String src) {
//        try {
//            MessageDigest m = MessageDigest.getInstance("MD5");
//            m.update(src.getBytes());
//            byte[] digest = m.digest();
//            BigInteger bigInt = new BigInteger(1, digest);
//            String hashText = bigInt.toString(16);
//            while (hashText.length() < 32) {
//                hashText = "0" + hashText;
//            }
//            return hashText;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
        return updatesDigest("MD5", src);
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     * @param src
     * @return
     */
    public static String sha256(String src) {
        return updatesDigest("SHA256", src);
    }

    /**
     * @param str 加密后的报文
     * @return
     */
    private static String updatesDigest(String algorithm, String str){
        MessageDigest m;
        String encodeStr = "";
        try {
            m = MessageDigest.getInstance(algorithm);
            m.update(str.getBytes("UTF-8"));
            switch (algorithm) {
                case "MD5":
                    BigInteger bigInt = new BigInteger(1, m.digest());
                    StringBuilder builder = new StringBuilder(bigInt.toString(16));
                    while (builder.length() < 32) {
                        builder.insert(0, "0");
                    }
                    encodeStr = builder.toString();
                    break;
                case "SHA256":
                    encodeStr = byte2Hex(m.digest());
                    break;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }

    private static final Collator COLLATOR = Collator.getInstance(Locale.CHINA);

    public static int compareString(String str1, String str2) {
        if (TextUtils.isEmpty(str1) || TextUtils.isEmpty(str2)) {
            return 0;
        }
//        int compare = str1.compareToIgnoreCase(str2);
//        if (compare == 0) {
//            compare = COLLATOR.compare(str1, str2);
//        }
        return COLLATOR.compare(str1, str2);
    }
}
