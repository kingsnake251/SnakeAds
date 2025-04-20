package com.example.snakeads.callback;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;

public class AdsCallback {
    public void onAdClosed(){

    }
    public void onAdLoaded() {
    }
    public void onAdInterstitialLoaded(InterstitialAd interstitialAd) {
    }
    public void onAdShowed() {
    }
    public void onAdFailedToLoad(@Nullable LoadAdError i) {
    }

    public void onAdFailedToLoad(){};

    public void onAdFailedToShow(@Nullable AdError adError) {
    }

    public void onAdClicked() {
    }

    public void onAdImpression() {
    }

    public void onAdClosedByUser(){
    }

    public void onNativeLoaded(NativeAd nativeAd){

    }

    public void onUserEarnedReward(RewardItem rewardItem){}

    public static void onEarnRevenue(Double Revenue){}
}
