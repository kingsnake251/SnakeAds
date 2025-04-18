package com.example.snakeads.callback;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;

public class AdsCallback {
    public void onAdClosed(){

    }
    public void onAdLoaded() {
    }
    public void onAdFailedToLoad(@Nullable LoadAdError i) {
    }

    public void onAdFailedToShow(@Nullable AdError adError) {
    }

    public void onAdClicked() {
    }

    public void onAdImpression() {
    }

    public void onAdClosedByUser(){
    }
}
