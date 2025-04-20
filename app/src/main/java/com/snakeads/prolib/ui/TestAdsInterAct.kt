package com.snakeads.prolib.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snakeads.admob.AdMobSnake
import com.example.snakeads.callback.AdsCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.snakeads.prolib.R
import com.snakeads.prolib.base.BaseActivity
import com.snakeads.prolib.databinding.ActivityMainBinding
import com.snakeads.prolib.databinding.ActivityTestAdsInterBinding

class TestAdsInterAct : BaseActivity<ActivityTestAdsInterBinding, MainVM>() {
    override val viewModel: MainVM by viewModels()
    override fun setBinding(layoutInflater: LayoutInflater): ActivityTestAdsInterBinding {
        return ActivityTestAdsInterBinding.inflate(layoutInflater)
    }

    override fun initView() {
       // loadNativeAds()
        loadAndShowNativeAds()
      //  loadBanner()
        loadBannerCollapse()
    }

    override fun observeData() {

    }
    fun loadBannerCollapse(){
        AdMobSnake.getInstance().loadBannerCollapseAds(this,getString(R.string.banner_collapse_id),"bottom",object : AdsCallback(){})
    }

    fun loadBanner(){
        AdMobSnake.getInstance().loadBannerAds(this,getString(R.string.banner_id),binding.banner,object : AdsCallback(){})
    }

    fun loadNativeAds() {
        AdMobSnake.getInstance()
            .loadNativeAds(this, getString(R.string.native_id), true, object : AdsCallback() {
                override fun onNativeLoaded(nativeAd: NativeAd?) {
                    super.onNativeLoaded(nativeAd)
                    var adView = LayoutInflater.from(this@TestAdsInterAct)
                        .inflate(R.layout.layout_native_ads, null) as NativeAdView
                    binding.frAds.removeAllViews()
                    binding.frAds.addView(adView)
                    AdMobSnake.getInstance().pushNativeAdView(nativeAd, adView)
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()

                }


            })
    }

    fun loadAndShowNativeAds() {
        var adView = LayoutInflater.from(this@TestAdsInterAct)
            .inflate(R.layout.layout_native_ads, null) as NativeAdView
        AdMobSnake.getInstance().loadAndShowNativeAds(
            this,
            getString(R.string.native_id),
            true,
            binding.frAds,
            adView,
            object : AdsCallback() {
                override fun onNativeLoaded(nativeAd: NativeAd?) {
                    super.onNativeLoaded(nativeAd)
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()

                }


            })
    }
}