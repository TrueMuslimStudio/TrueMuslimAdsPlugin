package com.zee.truemuslims.ads.modules.ads.plugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.malik.suhaatech.ads.modules.interfaces.TrueAdCallBackInterface
import com.zee.truemuslims.ads.modules.TrueAdManager
import com.zee.truemuslims.ads.modules.TrueAdMobManager
import com.zee.truemuslims.ads.modules.TrueAdsCalBackObject
import com.zee.truemuslims.ads.modules.TrueConstants
import com.zee.truemuslims.ads.modules.ads.plugin.databinding.ActivityMainBinding
import com.zee.truemuslims.ads.modules.database.TrueZSPRepository

import com.zee.truemuslims.ads.modules.in_app_module.TrueZInAppUpdate
import com.zee.truemuslims.ads.modules.in_app_module.TrueZInAppReview

class MainActivity : AppCompatActivity() {
    private lateinit var zMainBinding: ActivityMainBinding
    var TAG = "MainActivityy"
    lateinit var trueZInAppUpdate: TrueZInAppUpdate
    lateinit var trueInAppReview: TrueZInAppReview
    lateinit var trueAdManager: TrueAdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(zMainBinding.root)
        trueAdManager = TrueAdMobManager(this)
        /**Get Call Backs*/
        TrueAdManager.zhSetInterCallbacks(
            resources.getString(R.string.Admob_InterstitialId),
            TrueAdsCalBackObject.zInterCalBacks(this)
        )
        TrueAdManager.zSetNativeCallbacks(
            resources.getString(R.string.Admob_NativeAdvancedId),
            TrueAdsCalBackObject.zNativeCalBacks(this)
        )

        /*TrueAdManager.zShowNativeAdvanced(
            zMainBinding.zNativeAdvancedBanner,
            getString(R.string.Admob_NativeAdvancedId)
        )*/
        /* trueAdManager.loadAdmobNative(
             this,
             "ca-app-pub-3940256099942544/2247696110"
         )*/
        /*trueAdManager.showAdmobNative(
            this,
            "ca-app-pub-3940256099942544/2247696110",
            zMainBinding.zNativeAdvancedBanner
        )*/
        TrueAdManager.zShowNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id),
            zMainBinding.zNativeAdvancedBanner
        )
        TrueAdManager.zShowFlippingNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id),
            zMainBinding.zNativeFlippingBanner
        )
        TrueAdManager.zShowSimpleNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id),
            zMainBinding.zNativeSimpleBanner
        )
        /*TrueAdManager.zShowInterstitialInAdvance(this)*/
        TrueAdManager.zShowFlippingNativeBanner(
            zMainBinding.zNativeFlippingBanner,
            getString(R.string.Admob_NativeAdvancedId)
        )
        TrueAdManager.zShowSimpleNativeBanner(
            zMainBinding.zNativeSimpleBanner,
            getString(R.string.Admob_NativeAdvancedId)
        )

        TrueAdManager.zShowBannerWithOutFallback(
            zMainBinding.zBannerContainer,
            getString(R.string.Admob_BannerId)
        )

        zMainBinding.zShowInter.setOnClickListener {
            if (TrueConstants.isNetworkAvailable(this) && TrueConstants.isNetworkSpeedHigh()) {
                TrueAdManager.zShowInterstitial(
                    this,
                    resources.getString(R.string.Admob_InterstitialId),
                    object : TrueAdCallBackInterface {
                        override fun onShowAdComplete() {
                            if (TrueZSPRepository.getIfAdAvailable(this@MainActivity)) {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        MainActivity::class.java
                                    )
                                )
                            } else {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }
                        }

                    }
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )
            }
        }
        /**Check Update Module*/
        trueZInAppUpdate = TrueZInAppUpdate(this)
        zMainBinding.zCheckUpdate.setOnClickListener {
            /*trueZInAppUpdate.getInAppUpdate()*/
            TrueAdManager.zShowInterstitialWithOutCallBacks(
                this,
                resources.getString(R.string.Admob_InterstitialId)
            )
        }
        trueInAppReview = TrueZInAppReview(this)
        trueInAppReview.zShowRatingDialogue()
    }

}