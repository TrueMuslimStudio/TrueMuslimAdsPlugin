package com.zee.truemuslims.ads.modules

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.zee.truemuslims.ads.modules.database.TrueZSPRepository
import timber.log.Timber
import java.util.*

class TrueZAppOpenAd(private val myApplication: Application, var openAdId: String) :
    Application.ActivityLifecycleCallbacks,
    LifecycleObserver {
    var TAG = "TrueZAppOpenAdClass"
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null
    private var currentActivity: Activity? = null
    private var isShowingAd = false
    var intent: Intent = Intent()
    private var loadTime: Long = 0
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun fetchAd() {
        if (isAdAvailable()) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                Log.d(TAG, "OnAdLoaded : ${TrueZSPRepository.getOpenAdValue(myApplication)}")
                TrueZSPRepository.saveOpenAdValue(myApplication, true)
                appOpenAd = ad
                loadTime = Date().time
                if (appOpenAd != null) {
                    val fullScreenContentCallback: FullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                appOpenAd = null
                                isShowingAd = false
                                TrueZSPRepository.saveOpenAdValue(myApplication, false)
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                            }

                            override fun onAdShowedFullScreenContent() {
                                isShowingAd = true
                            }
                        }
                    appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
                    appOpenAd!!.show(currentActivity!!)
                }
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
                Timber.d("OnAdFailedToLoad: " + TrueZSPRepository.getOpenAdValue(myApplication))

            }
        }
        if (appOpenAd != null) {
            showAdIfAvailable()
        }

        val request = adRequest
        AppOpenAd.load(
            myApplication,
            openAdId, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback!!
        )
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(currentActivity!!)
        } else {
            fetchAd()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!TrueConstants.isInterstitialAdShow(myApplication)) {
                showAdIfAvailable()
            }
        }, 1000)
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && !TrueZSPRepository.getSubscription(
            myApplication
        ) && wasLoadTimeLessThanNHoursAgo(4)
    }

    /** Utility method to check if ad was loaded more than 4 hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
}