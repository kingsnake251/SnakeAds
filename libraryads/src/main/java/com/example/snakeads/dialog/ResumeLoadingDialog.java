package com.example.snakeads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.snakeads.R;
import com.example.snakeads.utils.AppUtil;


public class ResumeLoadingDialog extends Dialog {

    public ResumeLoadingDialog(Context context) {
        super(context, R.style.AppTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppUtil.layoutLoadingResumeAds == 0) {
            setContentView(R.layout.dialog_resume_loading);
        } else {
            setContentView(AppUtil.layoutLoadingResumeAds);
        }

    }
}
