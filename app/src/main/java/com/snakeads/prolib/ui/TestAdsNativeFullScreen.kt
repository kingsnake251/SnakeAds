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
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.snakeads.prolib.R
import com.snakeads.prolib.base.BaseActivity
import com.snakeads.prolib.databinding.ActivityTestAdsInterBinding
import com.snakeads.prolib.databinding.ActivityTestAdsNativeFullScreenBinding

class TestAdsNativeFullScreen : BaseActivity<ActivityTestAdsNativeFullScreenBinding,MainVM>() {
    override val viewModel: MainVM by viewModels()
    override fun setBinding(layoutInflater: LayoutInflater): ActivityTestAdsNativeFullScreenBinding {
        return ActivityTestAdsNativeFullScreenBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNativeAds()
    }

    override fun observeData() {
        
    }

    fun loadNativeAds() {
        AdMobSnake.getInstance()
            .loadNativeAdsFullScreen(this, getString(R.string.native_id), true, object : AdsCallback() {
                override fun onNativeLoaded(nativeAd: NativeAd?) {
                    super.onNativeLoaded(nativeAd)
                    var adView = LayoutInflater.from(this@TestAdsNativeFullScreen)
                        .inflate(R.layout.layout_native_ads_full_screen, null) as NativeAdView
                    binding.frAds.removeAllViews()
                    binding.frAds.addView(adView)
                    AdMobSnake.getInstance().pushNativeAdView(nativeAd, adView)
                }

                override fun onAdFailedToLoad() {
                    super.onAdFailedToLoad()

                }


            })
    }
}