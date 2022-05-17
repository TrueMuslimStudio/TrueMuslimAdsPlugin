package com.zee.truemuslims.ads.modules

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.zee.truemuslims.ads.modules.callbacks.TrueAdCallbacks
import com.zee.truemuslims.ads.modules.callbacks.TrueInterCallbacks
import com.zee.truemuslims.ads.modules.databinding.ActivityMainBinding
import com.zee.truemuslims.ads.modules.types.TrueAdsType
import com.zee.truemuslims.ads.modules.types.TrueWhatAd
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var hMainBinding: ActivityMainBinding

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
        hMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(hMainBinding.root)

        TrueAdManager.zhSetInterCallbacks(hInterCallbacks)
        TrueAdManager.zSetNativeCallbacks(hNativeCallbacks)


//        TrueAdManager.hLoadInterstitial(this)
        TrueAdManager.zShowNativeAdvanced(hMainBinding.hNativeAdvancedBanner)
        TrueAdManager.zShowNativeBanner(hMainBinding.hNativeBanner)
        TrueAdManager.zShowBannerWithOutFallback(hMainBinding.hBannerContainer)


        hMainBinding.hShowInter.setOnClickListener {
            TrueAdManager.zShowInterstitial(this)
        }
    }
}