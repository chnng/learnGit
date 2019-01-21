package com.aihui.lib.whiteboard.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aihui.lib.base.api.retrofit.BaseObserver;
import com.aihui.lib.base.api.retrofit.RetrofitManager;
import com.aihui.lib.base.model.module.mn.main.request.CustomProductBody;
import com.aihui.lib.base.util.BitmapUtils;
import com.aihui.lib.base.util.HandlerUtils;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.NetworkUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.aihui.lib.whiteboard.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by 胡一鸣 on 2018/6/25.
 * 自定义项目Fragment
 */
public class CustomProductFragment extends WhiteBoardFragment implements WhiteBoardFragment.OnSketchClickListener {
    private Context mContext;
    private String mColor;

    public static CustomProductFragment newInstance() {
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(BUNDLE_HOSPITAL_BEAN, mHospitalBean);
        //        fragment.setArguments(bundle);
        return new CustomProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        setOnSketchClickListener(this);
//        mCustomProductBody = getArguments().getParcelable(BUNDLE_HOSPITAL_BEAN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSave() {
        if (NetworkUtils.isNetworkConnected(mContext)) {
            new AlertDialog.Builder(mContext).setMessage("确认上传")
                    .setPositiveButton("确认", (dialog, which) -> update(null))
                    .setNegativeButton("取消", null).create().show();
        } else {
            ToastUtils.toast(R.string.network_error);
        }
    }

    public void update(CustomProductBody body) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("正在上传...");

        Bitmap resultBitmap = getResultBitmap();
        String sketchBase64 = BitmapUtils.bitmapToBase64(resultBitmap);
        dialog.show();
        if (body != null) {
            body.Remark = sketchBase64;
        } else {
            body = new CustomProductBody();
            body.HospitalCode = "1000000";
            body.DeptCode = "01020800";
            body.TypeID = 3;
            body.Remark = sketchBase64;
            body.Sort = 0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy年-MM月-dd日HH:mm:SS", Locale.CHINA);
            format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            body.ReminderProject = format.format(new Date());
        }
        ArrayList<CustomProductBody> list = new ArrayList<>();
        list.add(body);
        RetrofitManager.newHttpBaseServer().updateCustomProduct(list)
                .compose(RetrofitManager.parseResponseWith(this))
                .subscribe(new BaseObserver<Boolean>() {
                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ToastUtils.toast("上传失败\n" + e.getMessage());
                        dialog.dismiss();
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {

                    }
                });
    }

    public void setTitleColor(String color) {
        LogUtils.e("setTitleColor:" + color);
        if (!TextUtils.isEmpty(color) && !color.equals(mColor)) {
            mColor = color;
            HandlerUtils.post(() -> {
                String[] colors = color.split(",");
                int r = Integer.parseInt(colors[0]);
                int g = Integer.parseInt(colors[1]);
                int b = Integer.parseInt(colors[2]);
                setThemeColor(Color.rgb(r, g, b));
            });
        }
    }
}
