package com.example.snakeads.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsUtil {
    private static final String TAG = "FirebaseAnalyticsUtil";
    

    public static void logEventWithAds(Context context, Bundle params) {
        FirebaseAnalytics.getInstance(context).logEvent("snake_admob_paid_ad_impression", params);
    }

    static void logPaidAdImpressionValue(Context context, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent("snake_admob_paid_ad_impression_value", bundle);
    }

    public static void logClickAdsEvent(Context context, Bundle bundle) {

        FirebaseAnalytics.getInstance(context).logEvent("snake_admob_event_user_click_ads", bundle);
    }

    public static void logCurrentTotalRevenueAd(Context context, String eventName, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void logTotalRevenue001Ad(Context context, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent("snake_admob_Daily_Ads_Revenue", bundle);
    }

}
