package com.example.snakeads.admob;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.example.snakeads.callback.AdsCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public abstract class AdMobSnake {

    public static AdMobSnake getInstance() {
        return AdMobImp.getInstance();
    }

    public abstract void initAdmob(Context context);

    public abstract void setLayoutLoadingAds(int layoutID);

    public abstract void showAdsInterstitialSplash(Activity activity, String idAds, long timeDelay, AdsCallback adsCallback);

    public abstract void loadAndShowAdsInterstitial(Activity activity, String idAds, AdsCallback adsCallback);

    public abstract void loadAdsInterstitial(Activity activity, String idAds, AdsCallback adsCallback);

    public abstract void showAdsInterstitial(Activity activity, InterstitialAd interstitialAd, AdsCallback adsCallback);

    public abstract void setImmersiveMode(Boolean isImmersiveMode);

    public abstract void loadNativeAds(Activity activity, String idAds, Boolean isReloadWhenClick, AdsCallback adsCallback);

    public abstract void loadNativeAdsFullScreen(Activity activity, String idAds, Boolean isReloadWhenClick, AdsCallback adsCallback);

    public abstract void loadAndShowNativeAds(Activity activity, String idAds, Boolean isReloadWhenClick, ViewGroup viewAds, NativeAdView adView, AdsCallback adsCallback);

    public abstract void pushNativeAdView(NativeAd nativeAd, NativeAdView adView);

    public abstract void loadBannerAds(Activity activity, String idAds, ViewGroup viewAds, AdsCallback adsCallback);

    public abstract void loadBannerCollapseAds(Activity activity, String idAds, String gravity, AdsCallback adsCallback);

    public abstract void loadAndShowRewardedAds(Activity activity, String idAds, AdsCallback adsCallback);

}
