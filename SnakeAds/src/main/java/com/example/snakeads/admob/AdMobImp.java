package com.example.snakeads.admob;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.snakeads.R;
import com.example.snakeads.callback.AdsCallback;
import com.example.snakeads.utils.AdType;
import com.example.snakeads.utils.AppUtil;
import com.example.snakeads.utils.FirebaseUtil;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaAspectRatio;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdMobImp extends AdMobSnake {

    private static final String LOG_TAG = "AdMobSnake";
    private InterstitialAd mInterstitialAd;
    private static AdMobImp INSTANCE;
    private Context context;

    private Boolean isImmersiveMode = false;

    public static final String BANNER_INLINE_SMALL_STYLE = "BANNER_INLINE_SMALL_STYLE";
    public static final String BANNER_INLINE_LARGE_STYLE = "BANNER_INLINE_LARGE_STYLE";

    private static int MAX_SMALL_INLINE_BANNER_HEIGHT = 50;

    @Override
    public void initAdmob(Context context) {
        MobileAds.initialize(context, initializationStatus -> {
        });
        this.context = context;
    }

    @Override
    public void setLayoutLoadingAds(int layoutID) {
        AppUtil.layoutLoadingAds = layoutID;
    }

    @Override
    public void showAdsInterstitialSplash(Activity activity, String idAds, long timeDelay, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(this.context)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adsCallback.onAdClosed();
                }
            }, 3000);
            return;
        }
        mInterstitialAd = null;
        InterstitialAd.load(activity, idAds, getAdRequest(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
                adsCallback.onAdFailedToLoad(loadAdError);
                adsCallback.onAdClosed();
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
                interstitialAd.setOnPaidEventListener(adValue -> {
                    FirebaseUtil.logPaidAdImpression(context,
                            adValue,
                            interstitialAd.getAdUnitId(), AdType.INTERSTITIAL);
                    adsCallback.onEarnRevenue((double) adValue.getValueMicros());
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppUtil.showDialogLoadingAds(activity);
                        showAdsInter(activity, adsCallback);
                    }
                }, timeDelay);

            }
        });


    }


    @Override
    public void loadAndShowAdsInterstitial(Activity activity, String idAds, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdClosed();
            return;
        }
        AppUtil.showDialogLoadingAds(activity);
        InterstitialAd.load(activity, idAds, getAdRequest(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adsCallback.onAdFailedToLoad(loadAdError);
                adsCallback.onAdClosed();
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                showAdsInter(activity, interstitialAd, adsCallback);


            }
        });

    }

    void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void loadAdsInterstitial(Activity activity, String idAds, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity)) {
            return;
        }
        InterstitialAd.load(activity, idAds, getAdRequest(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adsCallback.onAdFailedToLoad(loadAdError);

            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                adsCallback.onAdInterstitialLoaded(interstitialAd);
                interstitialAd.setOnPaidEventListener(adValue -> {
                    FirebaseUtil.logPaidAdImpression(context,
                            adValue,
                            interstitialAd.getAdUnitId(), AdType.INTERSTITIAL);
                    adsCallback.onEarnRevenue((double) adValue.getValueMicros());
                });


            }
        });

    }

    @Override
    public void showAdsInterstitial(Activity activity, InterstitialAd interstitialAd, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity) || interstitialAd == null) {
            adsCallback.onAdClosed();
            return;
        }
        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            // Do something
            AppUtil.showDialogLoadingAds(activity);
            showAdsInter(activity, interstitialAd, adsCallback);
        } else {
            adsCallback.onAdClosed();
        }

    }

    @Override
    public void setImmersiveMode(Boolean isImmersiveMode) {
        this.isImmersiveMode = isImmersiveMode;
    }

    @Override
    public void loadNativeAds(Activity activity, String idAds, Boolean isReloadWhenClick, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdFailedToLoad();
            return;
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        AdLoader adLoader = new AdLoader.Builder(activity, idAds)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        adsCallback.onNativeLoaded(nativeAd);
                        nativeAd.setOnPaidEventListener(adValue -> {
                            FirebaseUtil.logPaidAdImpression(activity,
                                    adValue,
                                    idAds,
                                    AdType.NATIVE);
                            AdsCallback.onEarnRevenue((double) adValue.getValueMicros());
                        });


                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        adsCallback.onAdClicked();
                        if (isReloadWhenClick) {
                            loadNativeAds(activity, idAds, isReloadWhenClick, adsCallback);
                        }
                        FirebaseUtil.logClickAdsEvent(context, idAds);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        adsCallback.onAdFailedToLoad(loadAdError);
                        adsCallback.onAdFailedToLoad();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        adsCallback.onAdImpression();
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();
        adLoader.loadAd(getAdRequest());

    }

    @Override
    public void loadNativeAdsFullScreen(Activity activity, String idAds, Boolean isReloadWhenClick, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdFailedToLoad();
            return;
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .setCustomControlsRequested(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
                .build();

        AdLoader adLoader = new AdLoader.Builder(activity, idAds)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        adsCallback.onNativeLoaded(nativeAd);
                        nativeAd.setOnPaidEventListener(adValue -> {
                            FirebaseUtil.logPaidAdImpression(activity,
                                    adValue,
                                    idAds,
                                    AdType.NATIVE);
                            AdsCallback.onEarnRevenue((double) adValue.getValueMicros());
                        });

                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        adsCallback.onAdClicked();
                        if (isReloadWhenClick) {
                            loadNativeAds(activity, idAds, isReloadWhenClick, adsCallback);
                        }
                        FirebaseUtil.logClickAdsEvent(context, idAds);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        adsCallback.onAdFailedToLoad(loadAdError);
                        adsCallback.onAdFailedToLoad();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        adsCallback.onAdImpression();
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();
        adLoader.loadAd(getAdRequest());
    }

    @Override
    public void loadAndShowNativeAds(Activity activity, String idAds, Boolean isReloadWhenClick, ViewGroup viewAds, NativeAdView adView, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdFailedToLoad();
            return;
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        AdLoader adLoader = new AdLoader.Builder(activity, idAds)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        adsCallback.onNativeLoaded(nativeAd);
                        nativeAd.setOnPaidEventListener(adValue -> {
                            FirebaseUtil.logPaidAdImpression(activity,
                                    adValue,
                                    idAds,
                                    AdType.NATIVE);
                            AdsCallback.onEarnRevenue((double) adValue.getValueMicros());
                        });
                        viewAds.removeAllViews();
                        viewAds.addView(adView);
                        pushNativeAdView(nativeAd, adView);


                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        adsCallback.onAdClicked();
                        if (isReloadWhenClick) {
                            loadAndShowNativeAds(activity, idAds, isReloadWhenClick, viewAds, adView, adsCallback);
                        }
                        FirebaseUtil.logClickAdsEvent(context, idAds);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        adsCallback.onAdFailedToLoad(loadAdError);
                        adsCallback.onAdFailedToLoad();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        adsCallback.onAdImpression();
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();
        adLoader.loadAd(getAdRequest());
    }

    @Override
    public void pushNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }

        if (adView.getMediaView() != null) {
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        }


        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }


        if (adView.getPriceView() != null) {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }


        if (adView.getStoreView() != null) {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }


        if (adView.getStarRatingView() != null) {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }


        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    @Override
    public void loadBannerAds(Activity activity, String idAds, ViewGroup viewAds, AdsCallback adsCallback) {
        FrameLayout adContainer = viewAds.findViewById(R.id.banner_container);
        FrameLayout adShimmer = viewAds.findViewById(R.id.banner_shimmer);
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdFailedToLoad();
            adShimmer.setVisibility(View.GONE);
            adContainer.setVisibility(View.GONE);
            return;
        }
        adShimmer.setVisibility(View.VISIBLE);
        adContainer.setVisibility(View.VISIBLE);
        AdView adView = new AdView(activity);
        adView.setAdUnitId(idAds);
        AdSize adSize = getAdSize(activity, false, BANNER_INLINE_LARGE_STYLE);
        adView.setAdSize(adSize);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                adsCallback.onAdClicked();
                FirebaseUtil.logClickAdsEvent(context, idAds);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adsCallback.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adsCallback.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adsCallback.onAdImpression();

                // Remove banner from view hierarchy.


            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adShimmer.setVisibility(View.GONE);
                adsCallback.onAdLoaded();
                adView.setOnPaidEventListener(adValue -> {
                    FirebaseUtil.logPaidAdImpression(context,
                            adValue,
                            adView.getAdUnitId(), AdType.BANNER);
                });
            }
        });
        adContainer.removeAllViews();
        adContainer.addView(adView);
        adView.loadAd(getAdRequest());


    }

    @Override
    public void loadBannerCollapseAds(Activity activity, String idAds, String gravity, AdsCallback adsCallback) {
        FrameLayout adContainer = activity.findViewById(R.id.banner_container);
        FrameLayout adShimmer = activity.findViewById(R.id.banner_shimmer);
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdFailedToLoad();
            adShimmer.setVisibility(View.GONE);
            adContainer.setVisibility(View.GONE);
            return;
        }
        adShimmer.setVisibility(View.VISIBLE);
        adContainer.setVisibility(View.VISIBLE);
        AdView adView = new AdView(activity);
        adView.setAdUnitId(idAds);
        AdSize adSize = getAdSize(activity, false, "");
        adView.setAdSize(adSize);
        adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                adsCallback.onAdClicked();
                FirebaseUtil.logClickAdsEvent(context, idAds);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adsCallback.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adsCallback.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adsCallback.onAdImpression();

                // Remove banner from view hierarchy.


            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adShimmer.setVisibility(View.GONE);
                adsCallback.onAdLoaded();
                adView.setOnPaidEventListener(adValue -> {
                    FirebaseUtil.logPaidAdImpression(context,
                            adValue,
                            adView.getAdUnitId(), AdType.BANNER);
                });
            }
        });
        adContainer.removeAllViews();
        adContainer.addView(adView);
        adView.loadAd(getAdRequestForCollapsibleBanner(gravity));
    }

    @Override
    public void loadAndShowRewardedAds(Activity activity, String idAds, AdsCallback adsCallback) {
        if (!AppUtil.isNetworkConnected(activity)) {
            adsCallback.onAdFailedToLoad();
            return;
        }
        AppUtil.showDialogLoadingAds(activity);
        RewardedAd.load(activity, idAds, getAdRequest(), new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                adsCallback.onAdFailedToLoad(loadAdError);
                AppUtil.dismissDialogLoadingAds();
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                showRewardAd(activity, rewardedAd, adsCallback);
                rewardedAd.setOnPaidEventListener(adValue -> {
                    FirebaseUtil.logPaidAdImpression(context,
                            adValue,
                            rewardedAd.getAdUnitId(),
                            AdType.REWARDED);
                });
            }
        });
    }

    private void showRewardAd(Activity activity, RewardedAd rewardedAd, AdsCallback adsCallback) {
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                adsCallback.onAdClicked();
                FirebaseUtil.logClickAdsEvent(context, rewardedAd.getAdUnitId());
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                adsCallback.onAdClosed();
                AppUtil.dismissDialogLoadingAds();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                adsCallback.onAdFailedToShow(adError);
                AppUtil.dismissDialogLoadingAds();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adsCallback.onAdImpression();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                adsCallback.onAdShowed();
            }
        });
        rewardedAd.show(activity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                adsCallback.onUserEarnedReward(rewardItem);
            }
        });
    }

    private AdRequest getAdRequestForCollapsibleBanner(String gravity) {
        AdRequest.Builder builder = new AdRequest.Builder();
        Bundle admobExtras = new Bundle();
        admobExtras.putString("collapsible", gravity);
        builder.addNetworkExtrasBundle(AdMobAdapter.class, admobExtras);
        return builder.build();
    }

    private AdSize getAdSize(Activity mActivity, Boolean useInlineAdaptive, String inlineStyle) {

        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        if (useInlineAdaptive) {
            if (inlineStyle.equalsIgnoreCase(BANNER_INLINE_LARGE_STYLE)) {
                return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(mActivity, adWidth);
            } else {
                return AdSize.getInlineAdaptiveBannerAdSize(adWidth, MAX_SMALL_INLINE_BANNER_HEIGHT);
            }
        }
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth);

    }

    private void showAdsInter(Activity activity, AdsCallback adsCallback) {
        AppOpenAdManager.getInstance().setInterstitialShowing(true);
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                adsCallback.onAdClicked();
                FirebaseUtil.logClickAdsEvent(context, mInterstitialAd.getAdUnitId());
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                adsCallback.onAdClosed();
                AppUtil.dismissDialogLoadingAds();
                AppOpenAdManager.getInstance().setInterstitialShowing(false);
                mInterstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                adsCallback.onAdClosed();
                mInterstitialAd = null;
                AppUtil.dismissDialogLoadingAds();
                AppOpenAdManager.getInstance().setInterstitialShowing(false);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adsCallback.onAdImpression();

            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                adsCallback.onAdShowed();

            }
        });
        mInterstitialAd.setImmersiveMode(isImmersiveMode);
        mInterstitialAd.show(activity);
    }


    private void showAdsInter(Activity activity, InterstitialAd interstitialAd, AdsCallback adsCallback) {
        AppOpenAdManager.getInstance().setInterstitialShowing(true);
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                adsCallback.onAdClicked();
                FirebaseUtil.logClickAdsEvent(context, interstitialAd.getAdUnitId());

            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                adsCallback.onAdClosed();
                AppUtil.dismissDialogLoadingAds();
                AppOpenAdManager.getInstance().setInterstitialShowing(false);
                Log.e(LOG_TAG, "onAdDismissedFullScreenContent: ");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                adsCallback.onAdClosed();
                AppUtil.dismissDialogLoadingAds();
                AppOpenAdManager.getInstance().setInterstitialShowing(false);
                Log.e(LOG_TAG, "onAdFailedToShowFullScreenContent: ");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                adsCallback.onAdImpression();

            }

            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                adsCallback.onAdShowed();
                hideSystemUI(activity);
                Log.e(LOG_TAG, "onAdShowedFullScreenContent: ");

            }
        });
        interstitialAd.setImmersiveMode(isImmersiveMode);
        interstitialAd.show(activity);
    }


    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public static AdMobImp getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AdMobImp();
        }
        return INSTANCE;
    }


}
