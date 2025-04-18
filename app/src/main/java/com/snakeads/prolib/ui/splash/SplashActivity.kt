package com.snakeads.prolib.ui.splash

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.activity.viewModels
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
        if (hasNetworkConnection()) {
            Handler(Looper.getMainLooper()).postDelayed({
                startNextAct()
            }, 3000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startNextAct()
            }, 3000)
        }
    }

    private fun startNextAct() {
        startActivity(
            MainActivity.newIntent(this)
        )
    }
}