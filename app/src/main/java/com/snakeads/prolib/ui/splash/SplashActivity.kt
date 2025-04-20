package com.snakeads.prolib.ui.splash

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import com.example.snakeads.admob.AdMobSnake
import com.example.snakeads.admob.AppOpenAdManager
import com.example.snakeads.callback.AdsCallback
import com.snakeads.prolib.R
import com.snakeads.prolib.base.BaseActivity
import com.snakeads.prolib.databinding.ActivitySplashBinding
import com.snakeads.prolib.extensions.hasNetworkConnection
import com.snakeads.prolib.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding,SplashVM>() {

    override val viewModel: SplashVM by viewModels()


    override fun setBinding(layoutInflater: LayoutInflater): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initView() {
        initAdsSplash()
    }

    override fun observeData() {

    }

    private fun initAdsSplash() {
        //Load inter ads
        AdMobSnake.getInstance().showAdsInterstitialSplash(this,getString(R.string.interstitial_id),2000L,object : AdsCallback(){
            override fun onAdClosed() {
                super.onAdClosed()
                startNextAct()
            }

        })
    }

    private fun startNextAct() {
        startActivity(
            MainActivity.newIntent(this)
        )
    }

    override fun onResume() {
        super.onResume()
        AppOpenAdManager.getInstance().disableAdsResumeAct(SplashActivity::class.java)
    }
}