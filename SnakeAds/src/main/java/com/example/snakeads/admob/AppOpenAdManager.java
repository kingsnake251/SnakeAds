package com.example.snakeads.admob;

import android.app.Application;

import com.example.snakeads.callback.AdsCallback;

public abstract class AppOpenAdManager {
    public static AppOpenAdManager getInstance() {
        return AppOpenAdManagerImp.getInstance();
    }

    public abstract void initAdResume(Application application);

    public abstract boolean isInterstitialShowing();

    public abstract void setInterstitialShowing(boolean interstitialShowing);

    public abstract void disableAppResume();

    public abstract void enableAppResume();

    public abstract void setAdsResumeCallback(AdsCallback adsCallback);

    public abstract void setAppResumeAdId(String appResumeAdId);

    public abstract void disableAdsResumeAct(Class activityClass);

    public abstract void enableAdsResumeAct(Class activityClass);

    public abstract void setLayoutLoadingResumeAds(int layoutID);
}
