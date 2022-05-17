package com.zee.truemuslims.ads.modules

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import com.zee.truemuslims.ads.modules.callbacks.TrueAdCallbacks
import com.zee.truemuslims.ads.modules.callbacks.TrueInterCallbacks
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeAdvancedView
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerView
import com.zee.truemuslims.ads.modules.fallbackstrategies.TrueAdMobFallbackTrueStrategy
import com.zee.truemuslims.ads.modules.types.TrueAdPriorityType
import com.zee.truemuslims.ads.modules.types.TrueAdPriorityType.*
import com.zee.truemuslims.ads.modules.types.TrueAdsType
import com.zee.truemuslims.ads.modules.types.TrueAdsType.*
import com.zee.truemuslims.ads.modules.types.TrueWhatAd

@SuppressLint("StaticFieldLeak")
object TrueAdManager {

    private var zAdMobManager: TrueAdMobManager? = null
    private var TAG = "AdManagerClass"
    lateinit var context: Context
    private var zBannerPriorityType: TrueAdPriorityType = Z_AD_MOB
    private var zNativeBannerPriorityType: TrueAdPriorityType = Z_AD_MOB
    private var zNativeAdvancedPriorityType: TrueAdPriorityType = Z_AD_MOB
    private var zInterstitialPriorityType: TrueAdPriorityType = Z_AD_MOB
    private var zTimeOut: Long = TrueConstants.h3SecTimeOut

    private var zAdManagerInterCallbacks: TrueInterCallbacks? = null
    private var zAdManagerAdCallbacks: TrueAdCallbacks? = null


    fun zInitializeAds(
        zContext: Context,
        zIdsMap: HashMap<TrueAdsType, HashMap<TrueWhatAd, String>>,

        ) {
        context = zContext
        zIdsMap.keys.forEach { adsType ->
            when (adsType) {
                Z_ADMOB -> {
                    zAdMobManager = TrueAdMobManager(
                        zContext,
                        zIdsMap[adsType],
                    ).also {
                        it.zSetInterCallbacks(zInterCallbacks)
                        it.zSetNativeCallbacks(zAdCallbacks)
                    }
                }
            }
        }
    }

    fun zhSetInterCallbacks(interCallbacks: TrueInterCallbacks) {
        zAdManagerInterCallbacks = interCallbacks
    }

    fun zSetNativeCallbacks(adCallbacks: TrueAdCallbacks) {
        zAdManagerAdCallbacks = adCallbacks
    }


    fun zLoadInterstitial(
        zActivity: Activity,
        zPriorityType: TrueAdPriorityType = zInterstitialPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zLoadInterstitialAd(
                context,
                zTimeOut
            )
            Z_NONE -> Unit
        }

    }

    fun zShowInterstitial(
        activity: Activity,
        priority: TrueAdPriorityType = zInterstitialPriorityType,
    ) {
        when (priority) {
            Z_AD_MOB -> {
                if (zAdMobManager?.zInterstitialAd != null) {
                    zAdMobManager?.zInterstitialAd?.show(activity)
                    return
                } else {
                    zAdMobManager?.zLoadInterstitialAd(
                        context
                    )
                }

            }
            else -> Unit
        }
    }

    /*fun hIsInterstitialAvailable(activity: Activity): Boolean {
        val hPriorityType: TrueAdPriorityType =
            hInterstitialPriorityType
        when (hPriorityType) {
            H_AD_MOB -> {
                if (hAdMobManager?.hInterstitialAd != null) {
                    return true
                } else {
                    hAdMobManager?.hLoadInterstitialAd(context,
                        "ca-app-pub-3940256099942544/1033173712")
                }
            }
            H_FACE_BOOK -> {

                if (hFacebookManger?.hGetFbInterstitialAd() != null &&
                    hFacebookManger!!.hGetFbInterstitialAd()?.isAdLoaded == true
                ) {
                    return true
                } else {
                    hFacebookManger?.hLoadFbInterstitial()
                }
            }
            else -> Unit
        }
        return false
    }*/

    private fun zGetInterFallBackPriority(zAdsType: TrueAdsType): TrueAdPriorityType {
        return when (zInterstitialPriorityType) {
            Z_AD_MOB -> TrueAdMobFallbackTrueStrategy.zInterstitialStrategy(
                zGlobalPriority = zInterstitialPriorityType,
                zAdsType = zAdsType
            )
            else -> Z_NONE
        }
    }

    fun zShowBanner(
        bannerAdContainer: ViewGroup,
        zPriorityType: TrueAdPriorityType = zBannerPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zShowBanner(
                context,
                bannerAdContainer
            )
            else -> Unit
        }
    }


    fun zShowNativeBanner(
        zNativeBannerView: TrueZNativeBannerView,
        zPriorityType: TrueAdPriorityType = zNativeBannerPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zShowNativeBanner(
                zNativeBannerView
            )
            else -> Unit
        }
    }

    private fun zGetFallBackPriorityForNativeBanner(
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType {
        return when (zNativeBannerPriorityType) {
            Z_AD_MOB ->
                TrueAdMobFallbackTrueStrategy.zNativeBannerStrategy(
                    zGlobalPriority = zNativeBannerPriorityType,
                    zAdsType = zAdsType
                )
            else -> Z_NONE
        }
    }

    fun zShowNativeAdvanced(
        zNativeAdvancedView: TrueZNativeAdvancedView,
        zPriorityType: TrueAdPriorityType = zNativeAdvancedPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zShowNativeAdvanced(
                context,
                zNativeAdvancedView
            )
            Z_NONE -> Unit
        }
    }

    private fun zGetFallbackPriorityForNativeAdvanced(
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType {
        return when (zNativeAdvancedPriorityType) {
            Z_AD_MOB -> TrueAdMobFallbackTrueStrategy.zNativeAdvancedStrategy(
                zGlobalPriority = zNativeAdvancedPriorityType,
                zAdsType = zAdsType
            )
            else -> Z_NONE
        }
    }

    private fun zGetFallbackPriorityForBanner(
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType {
        return when (zBannerPriorityType) {
            Z_AD_MOB -> TrueAdMobFallbackTrueStrategy.zBannerStrategy(
                zGlobalPriority = zBannerPriorityType,
                zAdsType = zAdsType
            )
            else -> Z_NONE
        }
    }


    fun zShowBannerWithOutFallback(
        bannerAdContainer: ViewGroup,
        zPriorityType: TrueAdPriorityType = zBannerPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zShowBanner(
                context,
                zAdViewGroup = bannerAdContainer,
                zIsWithFallback = false
            )
            else -> Unit
        }
    }


    fun zShowNativeBannerWithOutFallback(
        zNativeBannerView: TrueZNativeBannerView,
        zPriorityType: TrueAdPriorityType = zNativeBannerPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zShowNativeBanner(
                zNativeBannerView = zNativeBannerView,
                zIsWithFallback = false
            )

            else -> Unit
        }
    }


    fun zShowNativeAdvancedWithOutFallback(
        zNativeBannerView: TrueZNativeAdvancedView,
        zPriorityType: TrueAdPriorityType = zNativeBannerPriorityType,
    ) {
        when (zPriorityType) {
            Z_AD_MOB -> zAdMobManager?.zShowNativeAdvanced(
                context,
                zNativeAdvancedView = zNativeBannerView,
                zIsWithFallback = false
            )
            else -> Unit
        }
    }


    /**For Manually chaning the priorities*/
    fun zSetNativeBannerPriority(
        nativeBannerPriorityType: TrueAdPriorityType,
    ) {
        zNativeBannerPriorityType = nativeBannerPriorityType
    }

    fun zSetNativeAdvancedPriority(
        nativeAdvancedPriorityType: TrueAdPriorityType,
    ) {
        zNativeAdvancedPriorityType = nativeAdvancedPriorityType
    }

    fun zSetInterstitialPriority(
        interstitialPriorityType: TrueAdPriorityType,
    ) {
        zInterstitialPriorityType = interstitialPriorityType
    }

    fun zSetBannerPriority(
        bannerPriorityType: TrueAdPriorityType,
    ) {
        zBannerPriorityType = bannerPriorityType
    }

    fun zSetTimeout(timeOut: Long) {
        zTimeOut = timeOut
    }


    private var zInterCallbacks: TrueInterCallbacks = object : TrueInterCallbacks() {

        override fun zOnAdFailedToLoad(
            zAdType: TrueAdsType,
            zError: TrueError,
            zActivity: Activity?,
        ) {
            zAdManagerInterCallbacks?.zOnAdFailedToLoad(zAdType, zError)
            zActivity?.let {
                zLoadInterstitial(
                    zPriorityType = zGetInterFallBackPriority(
                        zAdType
                    ),
                    zActivity = it
                )
            }
        }

        override fun zOnAddLoaded(zAdType: TrueAdsType) {
            zAdManagerInterCallbacks?.zOnAddLoaded(zAdType)
        }

        override fun zOnAdFailedToShowFullContent(
            zAdType: TrueAdsType,
            zError: TrueError,
        ) {
            zAdManagerInterCallbacks?.zOnAdFailedToShowFullContent(zAdType, zError)
        }

        override fun zOnAddShowed(zAdType: TrueAdsType) {
            zAdManagerInterCallbacks?.zOnAddShowed(zAdType)
        }

        override fun zOnAddDismissed(zAdType: TrueAdsType) {
            zAdManagerInterCallbacks?.zOnAddDismissed(zAdType)
        }

        override fun zOnAdTimedOut(zAdType: TrueAdsType) {
            zAdManagerInterCallbacks?.zOnAdTimedOut(zAdType)
        }
    }

    private var zAdCallbacks: TrueAdCallbacks = object : TrueAdCallbacks() {
        override fun zAdLoaded(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
        ) {
            zAdManagerAdCallbacks?.zAdLoaded(
                zAdType = zAdType,
                zWhatAd = zWhatAd
            )
        }

        override fun zAdClicked(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
        ) {
            zAdManagerAdCallbacks?.zAdClicked(
                zAdType = zAdType,
                zWhatAd = zWhatAd
            )
        }

        override fun zAdImpression(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
        ) {
            zAdManagerAdCallbacks?.zAdImpression(
                zAdType = zAdType,
                zWhatAd = zWhatAd
            )
        }

        override fun zAdClosed(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
        ) {
            zAdManagerAdCallbacks?.zAdClosed(
                zAdType = zAdType,
                zWhatAd = zWhatAd
            )
        }

        override fun zAdFailedToLoad(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
            zError: TrueError,
            zNativeView: ViewGroup,
            zIsWithFallback: Boolean,
        ) {
            zAdManagerAdCallbacks?.zAdFailedToLoad(
                zAdType = zAdType,
                zWhatAd = zWhatAd,
                zError = zError,
                zNativeView = zNativeView,
                zIsWithFallback = zIsWithFallback,

                )
            when (zIsWithFallback) {
                true -> when (zWhatAd) {
                    TrueWhatAd.Z_NATIVE_BANNER -> zShowNativeBanner(
                        zNativeBannerView = zNativeView as TrueZNativeBannerView,
                        zPriorityType = zGetFallBackPriorityForNativeBanner(
                            zAdsType = zAdType
                        )
                    )
                    TrueWhatAd.Z_NATIVE_ADVANCED -> zShowNativeAdvanced(
                        zNativeAdvancedView = zNativeView as TrueZNativeAdvancedView,
                        zPriorityType = zGetFallbackPriorityForNativeAdvanced(
                            zAdsType = zAdType
                        )
                    )
                    TrueWhatAd.Z_BANNER -> zShowBanner(
                        bannerAdContainer = zNativeView,
                        zPriorityType = zGetFallbackPriorityForBanner(
                            zAdsType = zAdType
                        )
                    )
                    TrueWhatAd.Z_INTER -> Unit
                }
                false -> Unit
            }

        }

        override fun zNativeAdOpened(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
        ) {
            zAdManagerAdCallbacks?.zNativeAdOpened(
                zAdType = zAdType,
                zWhatAd = zWhatAd
            )
        }
    }

}




