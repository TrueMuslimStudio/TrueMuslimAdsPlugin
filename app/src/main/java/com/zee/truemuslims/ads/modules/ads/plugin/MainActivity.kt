package com.zee.truemuslims.ads.modules.ads.plugin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.zee.truemuslims.ads.modules.TrueAdManager
import com.zee.truemuslims.ads.modules.TrueError
import com.zee.truemuslims.ads.modules.ads.plugin.databinding.ActivityMainBinding
import com.zee.truemuslims.ads.modules.callbacks.TrueAdCallbacks
import com.zee.truemuslims.ads.modules.callbacks.TrueInterCallbacks
import com.zee.truemuslims.ads.modules.in_app_module.TrueZInAppUpdate
import com.zee.truemuslims.ads.modules.in_app_module.TrueZInAppReview


import com.zee.truemuslims.ads.modules.types.TrueAdsType
import com.zee.truemuslims.ads.modules.types.TrueWhatAd
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var zMainBinding: ActivityMainBinding
    var TAG = "MainActivityy"
    lateinit var trueZInAppUpdate: TrueZInAppUpdate
    lateinit var trueInAppReview: TrueZInAppReview
    private val hInterCallbacks = object : TrueInterCallbacks() {
        override fun zOnAdFailedToLoad(
            zAdType: TrueAdsType,
            zError: TrueError,
            zActivity: Activity?,
        ) {
            Timber.d("InterCallbacks hOnAdFailedToLoad And AdType is $zAdType")
        }

        override fun zOnAddLoaded(zAdType: TrueAdsType) {
            Timber.d("InterCallbacks hOnAddLoaded And AdType is $zAdType")
        }

        override fun zOnAdFailedToShowFullContent(
            zAdType: TrueAdsType,
            zError: TrueError,
        ) {
            Timber.d("InterCallbacks hOnAdFailedToShowFullContent And AdType is $zAdType")
        }

        override fun zOnAddShowed(zAdType: TrueAdsType) {
            Timber.d("InterCallbacks hOnAddShowed And AdType is $zAdType")
        }

        override fun zOnAddDismissed(zAdType: TrueAdsType) {
            Timber.d("InterCallbacks hOnAddDismissed And AdType is $zAdType")
        }

        override fun zOnAdTimedOut(zAdType: TrueAdsType) {
            Timber.d("InterCallbacks hOnAdTimedOut And AdType is $zAdType")
        }
    }


    private val hNativeCallbacks = object : TrueAdCallbacks() {

        override fun zAdLoaded(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
            Timber.d("AdCallbacks hNativeAdvLoaded And AdType is $zAdType and What Add $zWhatAd")
        }

        override fun zAdClicked(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
            Timber.d(" AdCallbacks hNativeAdvClicked And AdType is $zAdType  and What Add $zWhatAd")
        }

        override fun zAdImpression(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
            Timber.d("AdCallbacks hNativeAdvImpression And AdType is $zAdType  and What Add $zWhatAd")
        }

        override fun zAdClosed(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
            Timber.d("AdCallbacks hNativeAdvClosed And AdType is $zAdType  and What Add $zWhatAd")
        }


        override fun zAdFailedToLoad(
            zAdType: TrueAdsType,
            zWhatAd: TrueWhatAd,
            zError: TrueError,
            zNativeView: ViewGroup,
            zIsWithFallback: Boolean,
        ) {
            Timber.d("AdCallbacks hNativeAdvFailedToLoad And AdType is $zAdType and error is $zError  and What Add $zWhatAd")
        }


        override fun zNativeAdOpened(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
            Timber.d("AdCallbacks hNativeAdvOpened And AdType is $zAdType  and What Add $zWhatAd")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(zMainBinding.root)
        /**Get Call Backs*/
        TrueAdManager.zhSetInterCallbacks(
            resources.getString(R.string.Admob_InterstitialId),
            hInterCallbacks
        )
        TrueAdManager.zSetNativeCallbacks(
            resources.getString(R.string.Admob_NativeAdvancedId),
            hNativeCallbacks
        )

        TrueAdManager.zShowNativeAdvanced(
            zMainBinding.zNativeAdvancedBanner,
            getString(R.string.Admob_NativeAdvancedId)
        )
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
            TrueAdManager.zShowInterstitial(
                this,
                resources.getString(R.string.Admob_InterstitialId)
            )
        }
        /**Check Update Module*/
        trueZInAppUpdate = TrueZInAppUpdate(this)
        zMainBinding.zCheckUpdate.setOnClickListener {
            trueZInAppUpdate.getInAppUpdate()
        }
        trueInAppReview = TrueZInAppReview(this)
        trueInAppReview.zShowRatingDialogue()
    }

}