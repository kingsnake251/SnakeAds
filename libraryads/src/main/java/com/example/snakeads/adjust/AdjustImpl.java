package com.example.snakeads.adjust;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.BuildConfig;
import com.adjust.sdk.LogLevel;

import com.example.snakeads.AdsSnakeApplication;
import com.google.android.gms.ads.AdValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdjustImpl extends Adjust implements Application.ActivityLifecycleCallbacks {
    private static AdjustImpl INSTANCE;
    AdsSnakeApplication adsApplication;

    public static AdjustImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AdjustImpl();
        }
        return INSTANCE;
    }

    @Override
    public void init(AdsSnakeApplication context, String appToken) {
        this.adsApplication = context;
        String environment = BuildConfig.DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(context, appToken, environment);

        config.setOnAttributionChangedListener(adjustAttribution -> {
            PreferenceManager.getInstance().putString(PreferenceManager.PREF_ADMOB_NETWORK, adjustAttribution.network.toLowerCase());
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("adjust").document().set(adjustAttribution);
            firestore.collection("networks").document(adjustAttribution.network).get().addOnSuccessListener(documentSnapshot -> {
                AdjustNetworkInfo adjustNetworkInfo = documentSnapshot.toObject(AdjustNetworkInfo.class);
                if (adjustNetworkInfo != null) {
                    adjustNetworkInfo.setCount(adjustNetworkInfo.getCount() + 1);
                    firestore.collection("networks").document(adjustAttribution.network).set(adjustNetworkInfo);
                } else {
                    AdjustNetworkInfo adjustNetwork = new AdjustNetworkInfo();
                    adjustNetwork.setNetworkName(adjustAttribution.network);
                    adjustNetwork.setCount(1);
                    firestore.collection("networks").document(adjustAttribution.network).set(adjustNetwork);
                }
            });

            if (getOnAdjustAttributionChangedListener() != null) {
                AdjustAttribution attribution = new AdjustAttribution();
                attribution.setNetwork(adjustAttribution.network);
                attribution.setCampaign(adjustAttribution.campaign);
                attribution.setAdgroup(adjustAttribution.adgroup);
                attribution.setCreative(adjustAttribution.creative);
                attribution.setClickLabel(adjustAttribution.clickLabel);
                attribution.setTrackerToken(adjustAttribution.trackerToken);
                attribution.setTrackerName(adjustAttribution.trackerName);
                attribution.setAdid(adjustAttribution.adid);
                attribution.setCostType(adjustAttribution.costType);
                attribution.setCostAmount(adjustAttribution.costAmount);
                attribution.setCostCurrency(adjustAttribution.costCurrency);
                attribution.setFbInstallReferrer(adjustAttribution.fbInstallReferrer);
                Adjust.getInstance().getOnAdjustAttributionChangedListener().onAttributionChanged(attribution);
            }

        });
        if (BuildConfig.DEBUG) {
            config.setLogLevel(LogLevel.VERBOSE);
        }
        com.adjust.sdk.Adjust.onCreate(config);
        context.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void init(Context context, String appToken) {
        String environment = BuildConfig.DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(context, appToken, environment);

        config.setOnAttributionChangedListener(adjustAttribution -> {
            PreferenceManager.getInstance().putString(PreferenceManager.PREF_ADMOB_NETWORK, adjustAttribution.network.toLowerCase());

            if (getOnAdjustAttributionChangedListener() != null) {
                AdjustAttribution attribution = new AdjustAttribution();
                attribution.setNetwork(adjustAttribution.network);
                attribution.setCampaign(adjustAttribution.campaign);
                attribution.setAdgroup(adjustAttribution.adgroup);
                attribution.setCreative(adjustAttribution.creative);
                attribution.setClickLabel(adjustAttribution.clickLabel);
                attribution.setTrackerToken(adjustAttribution.trackerToken);
                attribution.setTrackerName(adjustAttribution.trackerName);
                attribution.setAdid(adjustAttribution.adid);
                attribution.setCostType(adjustAttribution.costType);
                attribution.setCostAmount(adjustAttribution.costAmount);
                attribution.setCostCurrency(adjustAttribution.costCurrency);
                attribution.setFbInstallReferrer(adjustAttribution.fbInstallReferrer);
                Adjust.getInstance().getOnAdjustAttributionChangedListener().onAttributionChanged(attribution);
            }
        });
        if (BuildConfig.DEBUG) {
            config.setLogLevel(LogLevel.VERBOSE);
        }
        com.adjust.sdk.Adjust.onCreate(config);
        ((Application) context).registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void trackAdRevenue(AdValue adValue) {
        if (adsApplication != null && adsApplication.enableAdjustTracking()) {
            AdjustAdRevenue revenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB);
            revenue.setRevenue((double) adValue.getValueMicros() / 1000000, adValue.getCurrencyCode());
            com.adjust.sdk.Adjust.trackAdRevenue(revenue);
            adsApplication.logRevenueAdjustWithCustomEvent((double) adValue.getValueMicros() / 1000000, adValue.getCurrencyCode());
        }
    }

    @Override
    public OnAdjustAttributionChangedListener getOnAdjustAttributionChangedListener() {
        return adsApplication.getAdjustAttributionChangedListener();
    }


    @Override
    public void logRevenueWithCustomEvent(String eventName, double revenue, String currency) {
        AdjustEvent event = new AdjustEvent(eventName);
        event.setRevenue(revenue, currency);
        com.adjust.sdk.Adjust.trackEvent(event);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        com.adjust.sdk.Adjust.onResume();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        com.adjust.sdk.Adjust.onPause();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
