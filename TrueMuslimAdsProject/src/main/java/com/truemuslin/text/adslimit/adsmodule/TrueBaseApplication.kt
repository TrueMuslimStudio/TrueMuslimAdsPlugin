package com.truemuslin.text.adslimit.adsmodule

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.truemuslin.text.adslimit.BuildConfig
import com.truemuslin.text.adslimit.adsmodule.adlimits.TrueAntiAdLimit
import com.truemuslin.text.adslimit.adsmodule.types.TrueAdPriorityType
import com.truemuslin.text.adslimit.adsmodule.types.TrueAdsType
import com.truemuslin.text.adslimit.adsmodule.types.TrueWhatAd
import timber.log.Timber


class TrueBaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this
        ) { }
        hInintTimber()

        val hIdsMap = hashMapOf<TrueAdsType, HashMap<TrueWhatAd, String>>()
        val hAdMobMap = hashMapOf<TrueWhatAd, String>()
        val hFacebookMap = hashMapOf<TrueWhatAd, String>()

        hIdsMap[TrueAdsType.H_ADMOB] = hAdMobMap
        hIdsMap[TrueAdsType.H_FACEBOOK] = hFacebookMap

        TrueAdManager.hInitializeAds(
            this,
            hIdsMap
        )
        TrueAdManager.hSetNativeAdvancedPriority(TrueAdPriorityType.H_AD_MOB)
        TrueAdManager.hSetNativeBannerPriority(TrueAdPriorityType.H_AD_MOB)
        TrueAdManager.hSetInterstitialPriority(TrueAdPriorityType.H_AD_MOB)
        TrueAdManager.hSetBannerPriority(TrueAdPriorityType.H_AD_MOB)
        TrueAdManager.hSetTimeout(TrueConstants.h3SecTimeOut)

        // Initialize Ad Limit Protector
        TrueAntiAdLimit.getInstance()
            .init(this, "https://api.grezz.dev/anti-ad-limit/PUBGB.json")

    }

    private fun hInintTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(
                    priority: Int,
                    tag: String?,
                    message: String,
                    t: Throwable?,
                ) {
                    super.log(priority, String.format("HashimTimberTags %s", tag), message, t)
                }
            })
        }
    }

}