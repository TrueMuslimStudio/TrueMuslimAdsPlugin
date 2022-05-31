package com.zee.truemuslims.ads.modules.ads.plugin

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.zee.truemuslims.ads.modules.BuildConfig

import com.zee.truemuslims.ads.modules.TrueAdManager
import com.zee.truemuslims.ads.modules.TrueConstants

import com.zee.truemuslims.ads.modules.adlimits.TrueAntiAdLimit
import com.zee.truemuslims.ads.modules.types.TrueAdPriorityType
import timber.log.Timber

class BaseApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        zTintTimber()

        TrueAdManager.zInitializeAds(
            this
        )
        TrueAdManager.zSetNativeAdvancedPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetNativeBannerPriorityFlipping(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetNativeBannerPrioritySimple(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetInterstitialPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetBannerPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetTimeout(TrueConstants.h3SecTimeOut)
        if (TrueConstants.isNetworkAvailable(TrueAdManager.context) && TrueConstants.isNetworkSpeedHigh()) {
            TrueAntiAdLimit.getInstance()
                .init(this, "https://suhaatech.com/AdsId/testads.json")
        }
    }


    private fun zTintTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(
                    priority: Int,
                    tag: String?,
                    message: String,
                    t: Throwable?
                ) {
                    super.log(priority, String.format("HashimTimberTags %s", tag), message, t)
                }
            })
        }
    }

}