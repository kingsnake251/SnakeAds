package com.example.snakeads;

import android.app.Application;

import com.example.snakeads.admob.AppOpenAdManager;
import com.example.snakeads.callback.AdsCallback;
import com.example.snakeads.utils.AppUtil;

public abstract class AdsSnakeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.BUILD_DEBUG = buildDebug();
        if (enableAdsResume()){
            AppOpenAdManager.getInstance().initAdResume(this);
            AppOpenAdManager.getInstance().setAdsResumeCallback(getResumeCallback());
            AppOpenAdManager.getInstance().setAppResumeAdId(getAdsResumeID());
        }

    }

    public abstract Boolean enableAdsResume();
    public abstract String  getAdsResumeID();
    public abstract AdsCallback  getResumeCallback();

    public abstract Boolean buildDebug();

}
