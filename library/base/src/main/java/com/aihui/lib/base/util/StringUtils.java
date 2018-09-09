package com.aihui.lib.base.util;

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
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
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(src.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashText = bigInt.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
