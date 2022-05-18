package com.zee.truemuslims.ads.modules.ads.plugin

import android.app.Application
import com.zee.truemuslims.ads.modules.BuildConfig

import com.zee.truemuslims.ads.modules.TrueAdManager
import com.zee.truemuslims.ads.modules.TrueConstants

import com.zee.truemuslims.ads.modules.adlimits.TrueAntiAdLimit
import com.zee.truemuslims.ads.modules.types.TrueAdPriorityType
import com.zee.truemuslims.ads.modules.types.TrueAdsType
import com.zee.truemuslims.ads.modules.types.TrueWhatAd
import timber.log.Timber

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        zTintTimber()

        val zIdsMap = hashMapOf<TrueAdsType, HashMap<TrueWhatAd, String>>()
        val zAdMobMap = hashMapOf<TrueWhatAd, String>()

        zAdMobMap[TrueWhatAd.Z_NATIVE_ADVANCED] = getString(R.string.admob_native_advanced_id)
        zAdMobMap[TrueWhatAd.Z_NATIVE_BANNER] = getString(R.string.admob_native_advanced_id)
        zAdMobMap[TrueWhatAd.Z_BANNER] = getString(R.string.admob_banner_id)
        zAdMobMap[TrueWhatAd.Z_INTER] = getString(R.string.admob_interstitial_id)

        zIdsMap[TrueAdsType.Z_ADMOB] = zAdMobMap

        TrueAdManager.zInitializeAds(
            this,
            zIdsMap
        )
        TrueAdManager.zSetNativeAdvancedPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetNativeBannerPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetInterstitialPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetBannerPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetTimeout(TrueConstants.h3SecTimeOut)
        TrueAntiAdLimit.getInstance()
            .init(this, "https://api.grezz.dev/anti-ad-limit/PUBGB.json")

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