package com.snakeads.prolib

import android.app.Application
import com.example.snakeads.AdsSnakeApplication
import com.example.snakeads.admob.AdMobSnake
import com.example.snakeads.admob.AppOpenAdManager
import com.example.snakeads.callback.AdsCallback
import com.snakeads.prolib.ui.splash.SplashActivity
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Application : AdsSnakeApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AppOpenAdManager.getInstance().disableAdsResumeAct(SplashActivity::class.java)
        AdMobSnake.getInstance().setLayoutLoadingAds(R.layout.dialog_loading_ads_custom)
        AdMobSnake.getInstance().setImmersiveMode(true)
        AppOpenAdManager.getInstance().setLayoutLoadingResumeAds(R.layout.dialog_loading_ads_custom)
    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getAdsResumeID(): String {
        return getString(R.string.resume_id)
    }

    override fun getResumeCallback(): AdsCallback {
        return object : AdsCallback(){
            override fun onAdClosed() {
                super.onAdClosed()
            }
        }
    }

    override fun buildDebug(): Boolean {
        return BuildConfig.DEBUG
    }

}