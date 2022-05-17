package com.truemuslin.text.adslimit.adsmodule

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.truemuslin.text.adslimit.adsmodule.adlimits.TrueAdLimitUtils
import com.truemuslin.text.adslimit.adsmodule.adlimits.TruePrefUtils
import com.truemuslin.text.adslimit.adsmodule.callbacks.TrueAdCallbacks
import com.truemuslin.text.adslimit.adsmodule.callbacks.TrueInterCallbacks
import com.example.truemuslims.ads.modules.customadview.TrueHnativeAdvancedView
import com.truemuslin.text.adslimit.adsmodule.customadview.TrueHnativeBannerView
import com.truemuslin.text.adslimit.adsmodule.types.TrueAdsType
import com.truemuslin.text.adslimit.adsmodule.types.TrueWhatAd
import com.truemuslin.text.adslimit.databinding.AdmobNativeAdvancedLayoutBinding
import com.truemuslin.text.adslimit.databinding.AdmobNativeBannerLayoutBinding
import com.truemuslin.text.adslimit.templates.TrueNativeTemplateStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class TrueAdMobManager(
    private val hContext: Context?,
    private val hIdsMap: HashMap<TrueWhatAd, String>?,
) {
    var hInterstitialAd: InterstitialAd? = null
        private set
    private var hBannerAdView: AdView? = null
    private var hInterCallbacks: TrueInterCallbacks? = null
    private var hAdCallbacks: TrueAdCallbacks? = null
    private var unitId: String? = null
    private var unitIdInter: String? = null
    private var unitIdNative: String? = null
    private var unitIdNativeBanner: String? = null
    private var prefName: String? = null
    private var prefNameInter: String? = null
    private var prefNameNative: String? = null
    private var prefNameNativeBanner: String? = null
    var testEnabled = false
    var isAdLoaded = false

    @SuppressLint("BinaryOperationInTimber")
    fun hLoadInterstitialAd(
        context: Context,
        unitId: String,
        hTimeOut: Long = TrueConstants.h3SecTimeOut,
    ) {
        this.unitIdInter = unitId
        if (unitId.contains("/")) {
            prefNameInter = unitId.substring(unitId.lastIndexOf("/") + 1)
        }
        val newUnitId = unitId
        CoroutineScope(Dispatchers.Main).launch {
            var hCallBackCalled = false
            if (!TrueAdLimitUtils.isBanned(context, prefNameInter, "Interstitial Ad")) {
                // it will be executed when its true
                val adRequest = AdRequest.Builder().build()
                Handler().postDelayed({
                    InterstitialAd.load(
                        hContext!!,
                        newUnitId,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {

                                this@TrueAdMobManager.hInterstitialAd = interstitialAd

                                interstitialAd.fullScreenContentCallback =
                                    object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                            hInterCallbacks?.hOnAddDismissed(TrueAdsType.H_ADMOB)
                                            hCallBackCalled = true
                                        }

                                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                                            hInterCallbacks?.hOnAdFailedToShowFullContent(
                                                hAdType = TrueAdsType.H_ADMOB,
                                                hError = TrueError(
                                                    hMessage = adError.message,
                                                    hCode = adError.code,
                                                    hDomain = adError.domain,
                                                )
                                            )
                                            hCallBackCalled = true
                                        }

                                        override fun onAdShowedFullScreenContent() {
                                            hInterCallbacks?.hOnAddShowed(TrueAdsType.H_ADMOB)
                                            hCallBackCalled = true
                                            hInterstitialAd = null
                                        }

                                        override fun onAdClicked() {
                                            super.onAdClicked()
                                            TruePrefUtils.getInstance().init(context, prefNameInter)
                                                .updateClicksCounter()
                                        }

                                    }
                                TruePrefUtils.getInstance().init(context, prefNameInter)
                                    .updateImpressionCounter()
                                hInterCallbacks?.hOnAddLoaded(hAdType = TrueAdsType.H_ADMOB)
                                hCallBackCalled = true
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                hInterCallbacks?.hOnAdFailedToLoad(
                                    hAdType = TrueAdsType.H_ADMOB,
                                    hError = TrueError(
                                        hMessage = loadAdError.message,
                                        hCode = loadAdError.code,
                                        hDomain = loadAdError.domain,
                                    )
                                )
                                hCallBackCalled = true
                            }

                        }
                    )
                    if (hCallBackCalled.not()) {
                        hInterCallbacks?.hOnAdTimedOut(TrueAdsType.H_ADMOB)
                    }
                    /*delay(hTimeOut)*/
                },
                    TruePrefUtils.getInstance().init(context, prefNameInter).delayMs)
                delay(hTimeOut)
            } else {
                Timber.tag("AdmobInter").d("Inter Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                    context,
                    prefNameInter,
                    "Interstitial Ad"))
            }

        }

    }


    @SuppressLint("BinaryOperationInTimber")
    fun hShowNativeBanner(
        nativeBannerId: String,
        hNativeBannerView: TrueHnativeBannerView,
        hIsWithFallback: Boolean = true,
    ) {
        this.unitIdNativeBanner = nativeBannerId
        if (nativeBannerId.contains("/")) {
            prefNameNativeBanner = nativeBannerId.substring(nativeBannerId.lastIndexOf("/") + 1)
        }
        val adLoader = AdLoader.Builder(
            hContext,
            nativeBannerId
        )
            .forNativeAd { NativeAd: NativeAd? ->
                val cd = ColorDrawable()
                val styles =
                    TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
                AdmobNativeBannerLayoutBinding.inflate(
                    LayoutInflater.from(hContext),
                    null,
                    false
                ).apply {
                    myTemplate.visibility = View.VISIBLE
                    myTemplate.setStyles(styles)
                    NativeAd?.let {
                        myTemplate.setNativeAd(it)
                    }
                    hNativeBannerView.hShowAdView(viewGroup = root)
                }


            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    hNativeBannerView.hShowHideAdLoader(true)
                    hAdCallbacks?.hAdFailedToLoad(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER,
                        hError = TrueError(
                            hMessage = loadAdError.message,
                            hCode = loadAdError.code,
                            hDomain = loadAdError.domain,
                        ),
                        hNativeView = hNativeBannerView,
                        hIsWithFallback = hIsWithFallback

                    )
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    hAdCallbacks?.hAdClosed(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    hAdCallbacks?.hNativeAdOpened(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    hAdCallbacks?.hAdLoaded(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                    TruePrefUtils.getInstance().init(hContext, prefNameNativeBanner)
                        .updateImpressionCounter()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    hAdCallbacks?.hAdClicked(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                    TruePrefUtils.getInstance().init(hContext, prefNameNativeBanner)
                        .updateClicksCounter()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    hAdCallbacks?.hAdImpression(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                }
            })
            .build()
        if (!TrueAdLimitUtils.isBanned(hContext, prefNameNativeBanner, "Native Banner Ad")) {
            Handler().postDelayed({ adLoader.loadAd(AdRequest.Builder().build()) },
                TruePrefUtils.getInstance().init(hContext, prefNameNativeBanner).delayMs)
        } else {
            Timber.tag("AdmobInter").d("Native Banner Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                hContext,
                prefNameNativeBanner,
                "Native Banner Ad"))
        }

    }

    private val hAdSize: AdSize
        get() {
            val display =
                (hContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(hContext, adWidth)
        }

    @SuppressLint("BinaryOperationInTimber")
    fun hShowBanner(
        context: Context,
        hAdViewGroup: ViewGroup,
        unitId: String,
        hIsWithFallback: Boolean = true,
    ) {
        try {
            this.unitId = unitId
            if (unitId.contains("/")) {
                prefName = unitId.substring(unitId.lastIndexOf("/") + 1)
            }
            val newUnitId = unitId
            if (hContext != null) {
                hBannerAdView = AdView(hContext)
                hBannerAdView!!.adUnitId = newUnitId
                val adSize = hAdSize
                hAdViewGroup.layoutParams.height =
                    hGetPixelFromDp(hContext, 60)
                hAddPlaceHolderTextView(hAdViewGroup)
                hBannerAdView?.adSize = adSize
                hBannerAdView?.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        hAdCallbacks?.hAdLoaded(
                            hAdType = TrueAdsType.H_ADMOB,
                            hWhatAd = TrueWhatAd.H_BANNER
                        )
                        TruePrefUtils.getInstance().init(context, prefName)
                            .updateImpressionCounter()
                        if (hBannerAdView!!.parent != null) {
                            (hBannerAdView!!.parent as ViewGroup).removeView(hBannerAdView)
                        }
                        hAdViewGroup.addView(hBannerAdView)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        hAdCallbacks?.hAdFailedToLoad(
                            hAdType = TrueAdsType.H_ADMOB,
                            hError = TrueError(
                                hMessage = loadAdError.message,
                                hCode = loadAdError.code,
                                hDomain = loadAdError.domain,
                            ),
                            hNativeView = hAdViewGroup,
                            hWhatAd = TrueWhatAd.H_BANNER,
                            hIsWithFallback = hIsWithFallback,
                        )
                    }

                    override fun onAdClosed() {
                        hAdCallbacks?.hAdClosed(
                            hAdType = TrueAdsType.H_ADMOB,
                            hWhatAd = TrueWhatAd.H_BANNER
                        )
                    }

                    override fun onAdOpened() {
                        hAdCallbacks?.hNativeAdOpened(
                            hAdType = TrueAdsType.H_ADMOB,
                            hWhatAd = TrueWhatAd.H_BANNER
                        )
                        TruePrefUtils.getInstance().init(context, prefName).updateClicksCounter()
                        if (TruePrefUtils.getInstance().init(context, prefName).hideOnClick) {
                            if (hBannerAdView != null) {
                                hBannerAdView!!.visibility = View.GONE
                            }
                        }
                    }

                    override fun onAdClicked() {
                        hAdCallbacks?.hAdClicked(
                            hAdType = TrueAdsType.H_ADMOB,
                            hWhatAd = TrueWhatAd.H_BANNER
                        )
                    }

                    override fun onAdImpression() {
                        hAdCallbacks?.hAdImpression(
                            hAdType = TrueAdsType.H_ADMOB,
                            hWhatAd = TrueWhatAd.H_BANNER
                        )
                    }
                }
                val adRequest = AdRequest.Builder().build()
                // Check if Ad is Banned
                if (!TrueAdLimitUtils.isBanned(context, prefName, "Banner Ad")) {
                    Handler().postDelayed({ hBannerAdView!!.loadAd(adRequest) },
                        TruePrefUtils.getInstance().init(context, prefName).delayMs)
                } else {
                    Timber.tag("Banner_Ads")
                        .d("Banner Ad Is Banned : " + !TrueAdLimitUtils.isBanned(context,
                            prefName,
                            "Banner Ad"))
                }
            }
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
    }

    private fun hAddPlaceHolderTextView(adContainerView: ViewGroup?) {
        val valueTV = TextView(hContext)
        valueTV.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        valueTV.gravity = Gravity.CENTER
        adContainerView!!.addView(valueTV)
    }

    @SuppressLint("BinaryOperationInTimber")
    fun hShowNativeAdvanced(
        context: Context,
        nativeUnitId: String,
        hNativeAdvancedView: TrueHnativeAdvancedView,
        hIsWithFallback: Boolean = true,
    ) {
        this.unitIdNative = nativeUnitId
        // WorkAround for creating pref xml file as it doesn't support slash symbol .. so we get the after slash only
        if (nativeUnitId.contains("/")) {
            prefNameNative = nativeUnitId.substring(nativeUnitId.lastIndexOf("/") + 1)
        }
        val adLoader = AdLoader.Builder(
            hContext!!,
            nativeUnitId
        )
            .forNativeAd { unifiedNativeAd: NativeAd? ->
                val cd = ColorDrawable()
                val styles =
                    TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
                AdmobNativeAdvancedLayoutBinding.inflate(
                    LayoutInflater.from(hContext),
                    null,
                    false
                ).apply {
                    myTemplate.visibility = View.VISIBLE
                    myTemplate.setStyles(styles)
                    unifiedNativeAd?.let {
                        myTemplate.setNativeAd(it)
                    }
                    hNativeAdvancedView.hShowAdView(viewGroup = root)
                }

            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    hNativeAdvancedView.hShowHideAdLoader(true)
                    hAdCallbacks?.hAdFailedToLoad(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_ADVANCED,
                        hError = TrueError(
                            hMessage = loadAdError.message,
                            hCode = loadAdError.code,
                            hDomain = loadAdError.domain,
                        ),
                        hNativeView = hNativeAdvancedView,
                        hIsWithFallback = hIsWithFallback
                    )
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    hAdCallbacks?.hAdClosed(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                    hAdCallbacks?.hNativeAdOpened(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    hAdCallbacks?.hAdLoaded(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                    TruePrefUtils.getInstance().init(context, prefNameNative).updateImpressionCounter()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    hAdCallbacks?.hAdClicked(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                    TruePrefUtils.getInstance().init(context, prefNameNative).updateClicksCounter()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    hAdCallbacks?.hAdImpression(
                        hAdType = TrueAdsType.H_ADMOB,
                        hWhatAd = TrueWhatAd.H_NATIVE_BANNER
                    )
                }
            })
            .build()
        if (!TrueAdLimitUtils.isBanned(context, prefNameNative, "Native Ad")) {
            // it will be executed when its true
            Handler().postDelayed({ adLoader.loadAd(AdRequest.Builder().build()) },
                TruePrefUtils.getInstance().init(context, prefNameNative).delayMs)
        } else {
            Timber.tag("Native_Ads")
                .d("Native Ad Is Banned : " + !TrueAdLimitUtils.isBanned(context,
                    prefNameNative,
                    "Native Ad"))
        }

    }

    fun hSetInterCallbacks(interCallbacks: TrueInterCallbacks) {
        hInterCallbacks = interCallbacks
    }

    fun hSetNativeCallbacks(adCallbacks: TrueAdCallbacks) {
        hAdCallbacks = adCallbacks
    }

    companion object {
        fun hGetPixelFromDp(application: Context?, dp: Int): Int {
            val display =
                (application!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val scale = outMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }


    init {
        hContext?.let { context ->
            MobileAds.initialize(context) { initializationStatus ->
                Timber.d("Ad Mob Initiliztion status ${initializationStatus.adapterStatusMap}")
            }
        }
    }

}