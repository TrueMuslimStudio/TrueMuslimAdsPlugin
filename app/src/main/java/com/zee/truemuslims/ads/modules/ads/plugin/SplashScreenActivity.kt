package com.zee.truemuslims.ads.modules.ads.plugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.zee.truemuslims.ads.modules.TrueAdManager
import com.zee.truemuslims.ads.modules.TrueAdMobManager
import com.zee.truemuslims.ads.modules.ads.plugin.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivitySplashScreenBinding
    lateinit var trueAdMobManager: TrueAdMobManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        trueAdMobManager = TrueAdMobManager(this)
        /*trueAdMobManager.loadAdmobNative(
            this,
            "ca-app-pub-3940256099942544/2247696110"
        )*/
        TrueAdManager.zLoadNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id)
        )
        TrueAdManager.zLoadSimpleNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id)
        )
        /*TrueAdManager.zLoadInterstitialInAdvance(
            this,
            resources.getString(R.string.admob_interstitial_id)
        )*/

        /*     TrueAdManager.zLoadSimpleNativeAdInAdvance(
                 this,
                 resources.getString(R.string.admob_native_advanced_id)
             )*/
        Handler(Looper.myLooper()!!).postDelayed({
            mainBinding.splashPB.visibility = View.GONE
            mainBinding.getStartedBtn.visibility = View.VISIBLE
        }, 3000)
        mainBinding.getStartedBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}