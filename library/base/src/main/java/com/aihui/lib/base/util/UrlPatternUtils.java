package com.aihui.lib.base.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;

/**
 * Created by huyiming on 2017/12/27.
 */

public final class UrlPatternUtils {
    /**
     * 网址正则
     */
    public static final String PATTERN_IS_WEB =
            "((http[s]{0,1}|ftp)://((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,"
                    + "2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                    + "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4}))"
                    + "(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.((\\d{1,"
                    + "2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                    + "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])"
                    + "|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4}))(:\\d+)?(/[a-zA-Z0-9\\"
                    + ".\\-~!@#$%^&*+?:_/=<>]*)?)";

    public static SpannableString matchUrlClick(TextView view, CharSequence text,
                                                final OnUrlClickListener listener) {
        return matchUrlClick(view, text, -1, true, listener);
    }

    public static SpannableString matchUrlClick(final TextView view, CharSequence text, int color,
                                                boolean isUnderLine,
                                                final OnUrlClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Pattern pattern = Pattern.compile(PATTERN_IS_WEB);
        Matcher matcher = pattern.matcher(text);
        //        Matcher matcher = Patterns.WEB_URL.matcher(text);
        SpannableString spannableString;
        if (text instanceof SpannableString) {
            spannableString = (SpannableString) text;
        } else {
            spannableString = new SpannableString(text);
        }
        while (matcher.find()) {
            final String group = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            spannableString.setSpan(new InternalClickSpan(color, isUnderLine) {
                @Override
                public void onClick(View widget) {
                    if (listener == null) {
                        Uri uri = Uri.parse(group);
                        Context context = widget.getContext();
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException ignore) {
                            //
                        }
                    } else {
                        listener.onClick(view, group);
                    }
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (view != null) {
            view.setText(spannableString);
        }
        return spannableString;
    }

    private abstract static class InternalClickSpan extends ClickableSpan {
        int color;
        boolean isUnderline;

        public InternalClickSpan(int color, boolean isUnderline) {
            this.color = color;
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color >= 0 ? color : ds.linkColor);
            ds.setUnderlineText(isUnderline);
        }
    }

    public interface OnUrlClickListener {
        void onClick(@Nullable TextView v, String url);
    }
}
