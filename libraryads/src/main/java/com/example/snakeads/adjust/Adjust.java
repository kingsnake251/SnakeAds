package com.example.snakeads.adjust;

import android.content.Context;


import com.example.snakeads.AdsSnakeApplication;
import com.google.android.gms.ads.AdValue;

public abstract class Adjust {
    public static Adjust getInstance() {
        return AdjustImpl.getInstance();
    }

    public abstract void init(AdsSnakeApplication context, String appToken);

    public abstract void init(Context context, String appToken);

    public abstract void trackAdRevenue(AdValue adValue);

    public abstract OnAdjustAttributionChangedListener getOnAdjustAttributionChangedListener();


    public abstract void logRevenueWithCustomEvent(String eventName, double revenue, String currency);
}
