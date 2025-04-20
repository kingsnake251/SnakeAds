package com.example.snakeads.admob;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.snakeads.callback.AdsCallback;
import com.example.snakeads.dialog.ResumeLoadingDialog;
import com.example.snakeads.utils.AppUtil;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class AppOpenAdManagerImp extends AppOpenAdManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "AppOpenAdManager";
    private String AD_UNIT_ID = "";
    private static volatile AppOpenAdManagerImp INSTANCE;
    private Application myApplication;
    private Activity currentActivity;
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private long loadTime = 0;

    private boolean isAppResumeEnabled = true;
    private boolean isInterstitialShowing = false;
    private AdsCallback adsCallback;

    private final List<Class> disabledAdsActList;
    Dialog dialogLoading = null;

    private AppOpenAdManagerImp() {
        disabledAdsActList = new ArrayList<>();
    }

    public static synchronized AppOpenAdManagerImp getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppOpenAdManagerImp();
        }
        return INSTANCE;
    }
    @Override
    public void initAdResume(Application application) {
        Log.e(LOG_TAG, "onStart: "+0 );
        this.myApplication = application;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    @Override
    public boolean isInterstitialShowing() {
        return isInterstitialShowing;
    }

    @Override
    public void setInterstitialShowing(boolean interstitialShowing) {
        this.isInterstitialShowing = interstitialShowing;
    }
    @Override
    public void disableAppResume() {
        isAppResumeEnabled = false;
    }

    @Override
    public void enableAppResume() {
        isAppResumeEnabled = true;
    }

    @Override
    public void setAdsResumeCallback(AdsCallback adsCallback) {
        this.adsCallback = adsCallback;
    }

    @Override
    public void setAppResumeAdId(String appResumeAdId) {
        AD_UNIT_ID = appResumeAdId;
        loadAd(myApplication.getApplicationContext());
    }

    @Override
    public void disableAdsResumeAct(Class activityClass) {
        disabledAdsActList.add(activityClass);
    }

    @Override
    public void enableAdsResumeAct(Class activityClass) {
        disabledAdsActList.remove(activityClass);
    }

    @Override
    public void setLayoutLoadingResumeAds(int layoutID) {
        AppUtil.layoutLoadingResumeAds = layoutID;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.e(LOG_TAG, "onStart: "+1 );
        if (!isAppResumeEnabled || isInterstitialShowing) {
            return;
        }
        for (Class activity : disabledAdsActList) {
            if (activity.getName().equals(currentActivity.getClass().getName())) {
                Log.d(LOG_TAG, "onStart: activity is disabled");
                return;
            }
        }
        Log.e(LOG_TAG, "onStart: "+currentActivity.getClass().getName() );
        showAdIfAvailable(currentActivity,this.adsCallback);

    }

    private void dismissDialogLoading() {
        if (dialogLoading != null) {
            try {
                dialogLoading.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void showAdIfAvailable(
            @NonNull final Activity activity,
            @NonNull AdsCallback adsCallback){
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd || AD_UNIT_ID.isEmpty()) {
            Log.d(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.");

            adsCallback.onAdClosed();
            loadAd(activity);
            return;
        }

        try {
            dismissDialogLoading();
            dialogLoading = new ResumeLoadingDialog(currentActivity);
            try {
                dialogLoading.show();
            } catch (Exception e) {
                adsCallback.onAdClosed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        // Set the reference to null so isAdAvailable() returns false.
                        Log.d(LOG_TAG, "Ad dismissed fullscreen content.");
                        appOpenAd = null;
                        isShowingAd = false;
                        adsCallback.onAdClosed();
                        dismissDialogLoading();
                        loadAd(activity);
                    }



                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        // Set the reference to null so isAdAvailable() returns false.
                        Log.d(LOG_TAG, adError.getMessage());
                        appOpenAd = null;
                        isShowingAd = false;

                        adsCallback.onAdFailedToShow(adError);
                        loadAd(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        Log.d(LOG_TAG, "Ad showed fullscreen content.");
                    }
                });
        isShowingAd = true;

        appOpenAd.show(activity);
    }

    public void loadAd(Context context) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable() || AD_UNIT_ID.isEmpty()) {
            return;
        }

        isLoadingAd = true;

        AppOpenAd.load(
                context, AD_UNIT_ID, getAdRequest(),
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        // Called when an app open ad has loaded.
                        Log.d(LOG_TAG, "Ad was loaded.");
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Called when an app open ad has failed to load.
                        Log.d(LOG_TAG, loadAdError.getMessage());
                        isLoadingAd = false;
                    }
                });
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (!activity.getClass().getName().equals(AdActivity.class.getName())) {
            currentActivity = null;
        }
    }
}
