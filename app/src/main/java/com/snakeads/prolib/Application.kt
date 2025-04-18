package com.snakeads.prolib

import android.app.Application
import com.example.snakeads.AdsSnakeApplication
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