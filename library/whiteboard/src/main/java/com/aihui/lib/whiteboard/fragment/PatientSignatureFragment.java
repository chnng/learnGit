package com.aihui.lib.whiteboard.fragment;

import com.aihui.lib.whiteboard.R;

/**
 * Created by 胡一鸣 on 2018/7/31.
 */
public class PatientSignatureFragment extends WhiteBoardFragment {

    public static PatientSignatureFragment newInstance() {
        return new PatientSignatureFragment();
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_patient_signature;
    }

    public boolean isPatientSigned() {
        return mSketchView.getRecordCount() != 0;
    }
}
