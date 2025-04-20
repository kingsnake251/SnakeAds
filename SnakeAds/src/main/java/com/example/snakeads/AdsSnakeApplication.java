package com.example.snakeads;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.snakeads.adjust.Adjust;
import com.example.snakeads.adjust.OnAdjustAttributionChangedListener;
import com.example.snakeads.adjust.PreferenceManager;
import com.example.snakeads.admob.AdMobSnake;
import com.example.snakeads.admob.AppOpenAdManager;
import com.example.snakeads.callback.AdsCallback;
import com.example.snakeads.utils.AppUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public abstract class AdsSnakeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.BUILD_DEBUG = buildDebug();
        PreferenceManager.init(this);
        AdMobSnake.getInstance().initAdmob(this);
        if (enableAdsResume()) {
            AppOpenAdManager.getInstance().initAdResume(this);
            AppOpenAdManager.getInstance().setAdsResumeCallback(getResumeCallback());
            AppOpenAdManager.getInstance().setAppResumeAdId(getAdsResumeID());
        }

        initRemoteConfig(getDefaultsAsyncFirebase(), getMinimumFetch());

        if (enableAdjustTracking()) {
            Adjust.getInstance().init(this, getAdjustToken());
        }

    }

    private void initRemoteConfig(int defaultAsync, long minimumFetch) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(minimumFetch)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultAsync);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                }
            }
        });
    }

    public abstract Boolean enableAdsResume();

    public abstract String getAdsResumeID();

    public abstract AdsCallback getResumeCallback();

    public abstract Boolean buildDebug();

    public boolean enableAdjustTracking() {
        return false;
    }

    public void logRevenueAdjustWithCustomEvent(double revenue, String currency) {
    }

    public OnAdjustAttributionChangedListener getAdjustAttributionChangedListener() {
        return null;
    }

    public String getAdjustToken() {
        return null;
    }

    protected long getMinimumFetch() {
        return 30L;
    }

    protected int getDefaultsAsyncFirebase() {
        return R.xml.remote_config_defaults;
    }


}
