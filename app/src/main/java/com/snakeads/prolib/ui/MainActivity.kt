package com.snakeads.prolib.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.example.snakeads.admob.AdMobSnake
import com.example.snakeads.callback.AdsCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.snakeads.prolib.R
import com.snakeads.prolib.base.BaseActivity
import com.snakeads.prolib.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainVM>() {

    var mInterstitialAd: InterstitialAd? = null

    override val viewModel: MainVM by viewModels()
    override fun setBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadAdsInterstitial()
        binding.btnLoadAndShowInter.setOnClickListener {
            AdMobSnake.getInstance().loadAndShowAdsInterstitial(
                this,
                getString(R.string.interstitial_id),
                object : AdsCallback() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        startActivity(Intent(this@MainActivity, TestAdsInterAct::class.java))
                    }
                })
        }
        binding.btnShowInter.setOnClickListener {
            if (mInterstitialAd != null) {
                AdMobSnake.getInstance()
                    .showAdsInterstitial(this, mInterstitialAd, object : AdsCallback() {
                        override fun onAdClosed() {
                            super.onAdClosed()
                            mInterstitialAd = null
                            loadAdsInterstitial()
                            startActivity(Intent(this@MainActivity, TestAdsInterAct::class.java))
                        }
                    })
            }

        }

        binding.btnLoadNativeFullScreen.setOnClickListener {
            startActivity(Intent(this@MainActivity, TestAdsNativeFullScreen::class.java))
        }
        binding.btnShowRewardedAds.setOnClickListener {
            AdMobSnake.getInstance().loadAndShowRewardedAds(this,getString(R.string.reward_id),object : AdsCallback(){
                override fun onUserEarnedReward(rewardItem: RewardItem?) {
                    super.onUserEarnedReward(rewardItem)
                    Toast.makeText(this@MainActivity, "rewarded success", Toast.LENGTH_SHORT).show()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                }
            })
        }
    }

    fun loadAdsInterstitial() {
        AdMobSnake.getInstance()
            .loadAdsInterstitial(this, getString(R.string.interstitial_id), object : AdsCallback() {
                override fun onAdFailedToLoad(i: LoadAdError?) {
                    super.onAdFailedToLoad(i)
                    mInterstitialAd = null
                }

                override fun onAdInterstitialLoaded(interstitialAd: InterstitialAd?) {
                    super.onAdInterstitialLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                }
            })
    }

    override fun observeData() {

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}