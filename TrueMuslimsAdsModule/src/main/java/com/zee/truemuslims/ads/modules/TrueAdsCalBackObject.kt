package com.zee.truemuslims.ads.modules

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import com.zee.truemuslims.ads.modules.callbacks.TrueAdCallbacks
import com.zee.truemuslims.ads.modules.callbacks.TrueInterCallbacks
import com.zee.truemuslims.ads.modules.database.TrueZSPRepository
import com.zee.truemuslims.ads.modules.types.TrueAdsType

import com.zee.truemuslims.ads.modules.types.TrueWhatAd
import timber.log.Timber

object TrueAdsCalBackObject {
    var interstitialAdnValue: Boolean = false
    var TAG = "MainActivityClass"

    @SuppressLint("LogNotTimber")
    fun zInterCalBacks(context: Activity): TrueInterCallbacks {
        val zInterCallbacks = object : TrueInterCallbacks() {
            override fun zOnAdFailedToLoad(
                zAdType: TrueAdsType,
                zError: TrueError,
                zActivity: Activity?
            ) {
                Log.d(TAG, "hOnAdFailedToLoad: " + zError.zMessage)
                interstitialAdnValue = false
                Timber.d("InterCallbacks hOnAdFailedToLoad And AdType is $zAdType")
            }

            override fun zOnAddLoaded(zAdType: TrueAdsType) {
                Log.d(TAG, "hOnAddLoaded: " + zAdType.name)
                Timber.d("InterCallbacks hOnAddLoaded And AdType is $zAdType")
            }

            override fun zOnAdFailedToShowFullContent(zAdType: TrueAdsType, zError: TrueError) {
                Log.d(TAG, "hOnAdFailedToShowFullContent: " + zError.zMessage)
                interstitialAdnValue = false
                Timber.d("InterCallbacks hOnAdFailedToShowFullContent And AdType is $zAdType")
            }

            override fun zOnAddShowed(zAdType: TrueAdsType) {
                Log.d(TAG, "hOnAddShowed: " + zAdType.name)
                interstitialAdnValue = true
                TrueZSPRepository.setAdInterCountValue(
                    context,
                    TrueZSPRepository.getAdInterCountValue(context) + 1
                )
                Timber.d("InterCallbacks hOnAddShowed And AdType is $zAdType")
            }

            override fun zOnAddDismissed(zAdType: TrueAdsType) {
                TrueZSPRepository.saveInterAdValue(
                    context,
                    0
                )
                Log.d(TAG, "hOnAddDismissed: " + zAdType.name)
                interstitialAdnValue = false
                Timber.d("InterCallbacks hOnAddDismissed And AdType is $zAdType")
            }

            override fun zOnAdTimedOut(zAdType: TrueAdsType) {
                Log.d(TAG, "hOnAdTimedOut: " + zAdType.name)
                interstitialAdnValue = false
                Timber.d("InterCallbacks hOnAdTimedOut And AdType is $zAdType")
            }
        }
        return zInterCallbacks
    }

    fun zNativeCalBacks(context: Context): TrueAdCallbacks {
        val zNativeCallbacks = object : TrueAdCallbacks() {

            override fun zAdLoaded(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
                Timber.d("TrueAdCallbacks hNativeAdvLoaded And AdType is $zAdType and What Add $zWhatAd")
            }

            override fun zAdClicked(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
                Timber.d(" TrueAdCallbacks hNativeAdvClicked And AdType is $zAdType  and What Add $zWhatAd")
            }

            override fun zAdImpression(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
                Timber.d("TrueAdCallbacks hNativeAdvImpression And AdType is $zAdType  and What Add $zWhatAd")
            }

            override fun zAdClosed(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
                Timber.d("TrueAdCallbacks hNativeAdvClosed And AdType is $zAdType  and What Add $zWhatAd")
            }

            override fun zAdFailedToLoad(
                zAdType: TrueAdsType,
                zWhatAd: TrueWhatAd,
                zError: TrueError,
                zNativeView: ViewGroup,
                zIsWithFallback: Boolean
            ) {
                Timber.d("TrueAdCallbacks hNativeAdvFailedToLoad And AdType is $zAdType and error is $zError  and What Add $zWhatAd")
            }

            override fun zNativeAdOpened(zAdType: TrueAdsType, zWhatAd: TrueWhatAd) {
                Timber.d("TrueAdCallbacks hNativeAdvOpened And AdType is $zAdType  and What Add $zWhatAd")
            }
        }
        return zNativeCallbacks
    }
}