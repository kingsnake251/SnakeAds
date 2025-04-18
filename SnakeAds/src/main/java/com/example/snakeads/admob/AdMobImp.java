package com.example.snakeads.admob;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.ads.MobileAds;

public class AdMobImp extends AdMobSnake{
    private Context context;
    @Override
    public void initAdmob(Context context) {
        MobileAds.initialize(context,initializationStatus -> {});
        this.context = context;
    }







    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
