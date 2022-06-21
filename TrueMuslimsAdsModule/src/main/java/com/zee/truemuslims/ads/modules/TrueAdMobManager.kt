package com.zee.truemuslims.ads.modules

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.zee.truemuslims.ads.modules.adlimits.TrueAdLimitUtils
import com.zee.truemuslims.ads.modules.adlimits.TruePrefUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.zee.truemuslims.ads.modules.TrueConstants.isAppInstalledFromPlay
import com.zee.truemuslims.ads.modules.callbacks.TrueAdCallbacks
import com.zee.truemuslims.ads.modules.callbacks.TrueInterCallbacks
import com.zee.truemuslims.ads.modules.customadview.TrueZBannerView
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeAdvancedView
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerFlippingView
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerSimpleView
import com.zee.truemuslims.ads.modules.databinding.*
import com.zee.truemuslims.ads.modules.templates.TrueBannerTemplateStyle
import com.zee.truemuslims.ads.modules.types.TrueAdsType
import com.zee.truemuslims.ads.modules.types.TrueWhatAd
import com.zee.truemuslims.ads.modules.templates.TrueNativeTemplateStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class TrueAdMobManager(
    private val zContext: Context?
) {
    var zInterstitialAd: InterstitialAd? = null
        private set
    private var zBannerAdView: AdView? = null
    private var zInterCallbacks: TrueInterCallbacks? = null
    private var zAdCallbacks: TrueAdCallbacks? = null
    private var prefName: String? = null
    private var prefNameInter: String? = null
    private var prefNameNative: String? = null
    private var prefNameNativeInAdvanced: String? = null
    private var prefNameFlippingNativeInAdvanced: String? = null
    private var prefNameSimpleNativeInAdvanced: String? = null
    private var prefNameNativeBanner: String? = null
    private var prefNameFlippingNativeBanner: String? = null

    var TAG = "TrueAdMobClass"
    lateinit var dialog: Dialog

    @Suppress("DEPRECATION")
    companion object {
        var mAdmobNative: NativeAd? = null
        var mFlippingAdmobNative: NativeAd? = null
        var mSimpleAdmobNative: NativeAd? = null
        var admobNativeAdLoader: AdLoader? = null
        var mSimpleAdmobNativeAdLoader: AdLoader? = null
        var mFlippingAdmobNativeAdLoader: AdLoader? = null

        fun zGetPixelFromDp(application: Context?, dp: Int): Int {
            val display =
                (application!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val scale = outMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun zLoadInterstitialAd(
        context: Activity,
        interId: String
    ) {
        dialog = Dialog(context)
        loadAds(context)
        if (interId.contains("/")) {
            prefNameInter = interId.substring(interId.lastIndexOf("/") + 1)
        }
        CoroutineScope(Dispatchers.Main).launch {
            var zCallBackCalled = false
            if (!TrueAdLimitUtils.isBanned(context, prefNameInter, "Interstitial Ad")) {
                dialog.show()
                /** it will be executed when its true*/
                val adRequest = AdRequest.Builder().build()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        InterstitialAd.load(
                            zContext!!,
                            interId,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                    dialog.dismiss()
                                    this@TrueAdMobManager.zInterstitialAd = interstitialAd

                                    interstitialAd.fullScreenContentCallback =
                                        object : FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                zInterCallbacks?.zOnAddDismissed(TrueAdsType.Z_ADMOB)
                                                zCallBackCalled = true
                                            }

                                            override fun onAdFailedToShowFullScreenContent(
                                                adError: AdError
                                            ) {

                                                zInterCallbacks?.zOnAdFailedToShowFullContent(
                                                    zAdType = TrueAdsType.Z_ADMOB,
                                                    zError = TrueError(
                                                        zMessage = adError.message,
                                                        zCode = adError.code,
                                                        zDomain = adError.domain,
                                                    )
                                                )
                                                zCallBackCalled = true
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                zInterCallbacks?.zOnAddShowed(TrueAdsType.Z_ADMOB)
                                                zCallBackCalled = true
                                                zInterstitialAd = null
                                            }

                                            override fun onAdClicked() {
                                                super.onAdClicked()
                                                TruePrefUtils.getInstance()
                                                    .init(context, prefNameInter)
                                                    .zUpdateClicksCounter()
                                            }

                                        }
                                    TruePrefUtils.getInstance().init(context, prefNameInter)
                                        .zUpdateImpressionCounter()
                                    zInterCallbacks?.zOnAddLoaded(zAdType = TrueAdsType.Z_ADMOB)
                                    zCallBackCalled = true
                                    interstitialAd.show(context)
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    zInterCallbacks?.zOnAdFailedToLoad(
                                        zAdType = TrueAdsType.Z_ADMOB,
                                        zError = TrueError(
                                            zMessage = loadAdError.message,
                                            zCode = loadAdError.code,
                                            zDomain = loadAdError.domain,
                                        )
                                    )
                                    dialog.dismiss()
                                    zCallBackCalled = true
                                }

                            }
                        )
                        if (zCallBackCalled.not()) {
                            zInterCallbacks?.zOnAdTimedOut(TrueAdsType.Z_ADMOB)
                        }
                    },
                    TruePrefUtils.getInstance().init(context, prefNameInter).delayMs
                )
            } else {
                Timber.tag("AdmobInter").d(
                    "Inter Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                        context,
                        prefNameInter,
                        "Interstitial Ad"
                    )
                )
            }

        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun zLoadNativeBannerFlipping(
        zNativeBannerFlippingView: TrueZNativeBannerFlippingView,
        nativeAdvancedId: String,
        zIsWithFallback: Boolean = true
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if (nativeAdvancedId.contains("/")) {
                prefNameFlippingNativeBanner =
                    nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
            }

            val adLoader = AdLoader.Builder(
                zContext!!,
                nativeAdvancedId
            )
                .forNativeAd { NativeAd: NativeAd? ->
                    val cd = ColorDrawable()
                    val styles =
                        TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
                    AdmobNativeBannerLayoutBinding.inflate(
                        LayoutInflater.from(zContext),
                        null,
                        false
                    ).apply {
                        myTemplate.visibility = View.VISIBLE
                        myTemplate.setStyles(styles)
                        NativeAd?.let {
                            myTemplate.setNativeAd(it, true)
                        }
                        zNativeBannerFlippingView.zShowAdView(viewGroup = root)
                    }
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        zNativeBannerFlippingView.zShowHideAdLoader(true)
                        zAdCallbacks?.zAdFailedToLoad(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_FLIPPING,
                            zError = TrueError(
                                zMessage = loadAdError.message,
                                zCode = loadAdError.code,
                                zDomain = loadAdError.domain,
                            ),
                            zNativeView = zNativeBannerFlippingView,
                            zIsWithFallback = zIsWithFallback
                        )
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        zAdCallbacks?.zAdClosed(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_FLIPPING
                        )
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        zAdCallbacks?.zNativeAdOpened(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_FLIPPING
                        )
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        zAdCallbacks?.zAdLoaded(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_FLIPPING
                        )
                        TruePrefUtils.getInstance().init(zContext, prefNameFlippingNativeBanner)
                            .zUpdateImpressionCounter()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        zAdCallbacks?.zAdClicked(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_FLIPPING
                        )
                        TruePrefUtils.getInstance().init(zContext, prefNameFlippingNativeBanner)
                            .zUpdateClicksCounter()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        zAdCallbacks?.zAdImpression(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_FLIPPING
                        )
                    }
                })
                .build()
            if (!TrueAdLimitUtils.isBanned(
                    zContext,
                    prefNameFlippingNativeBanner,
                    "Native Banner Ad"
                )
            ) {
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        Log.d(TAG, "Ad Id Is: : $prefNameFlippingNativeBanner")
                        adLoader.loadAd(AdRequest.Builder().build())
                    },
                    TruePrefUtils.getInstance().init(zContext, prefNameFlippingNativeBanner).delayMs
                )
            } else {
                Timber.tag("AdmobInter").d(
                    "Native Banner Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                        zContext,
                        prefNameFlippingNativeBanner,
                        "Native Banner Ad"
                    )
                )
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun zShowNativeBannerSimple(
        zNativeBannerFlippingView: TrueZNativeBannerSimpleView,
        nativeAdvancedId: String,
        zIsWithFallback: Boolean = true
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if (nativeAdvancedId.contains("/")) {
                prefNameNativeBanner =
                    nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
            }
            val adLoader = AdLoader.Builder(
                zContext!!,
                nativeAdvancedId
            )
                .forNativeAd { NativeAd: NativeAd? ->
                    val cd = ColorDrawable()
                    val styles =
                        TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
                    AdmobNativeBannerLayoutSimpleBinding.inflate(
                        LayoutInflater.from(zContext),
                        null,
                        false
                    ).apply {
                        myTemplate.visibility = View.VISIBLE
                        myTemplate.setStyles(styles)
                        NativeAd?.let {
                            myTemplate.setNativeAd(it, false)
                        }
                        zNativeBannerFlippingView.zShowAdView(viewGroup = root)
                    }
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        zNativeBannerFlippingView.zShowHideAdLoader(true)
                        zAdCallbacks?.zAdFailedToLoad(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_SIMPLE,
                            zError = TrueError(
                                zMessage = loadAdError.message,
                                zCode = loadAdError.code,
                                zDomain = loadAdError.domain,
                            ),
                            zNativeView = zNativeBannerFlippingView,
                            zIsWithFallback = zIsWithFallback
                        )
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        zAdCallbacks?.zAdClosed(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_SIMPLE
                        )
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        zAdCallbacks?.zNativeAdOpened(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_SIMPLE
                        )
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        zAdCallbacks?.zAdLoaded(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_SIMPLE
                        )
                        TruePrefUtils.getInstance().init(zContext, prefNameNativeBanner)
                            .zUpdateImpressionCounter()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        zAdCallbacks?.zAdClicked(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_SIMPLE
                        )
                        TruePrefUtils.getInstance().init(zContext, prefNameNativeBanner)
                            .zUpdateClicksCounter()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        zAdCallbacks?.zAdImpression(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER_SIMPLE
                        )
                    }
                })
                .build()
            if (!TrueAdLimitUtils.isBanned(
                    zContext,
                    prefNameNativeBanner,
                    "Native Banner Ad"
                )
            ) {
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        adLoader.loadAd(AdRequest.Builder().build())
                    },
                    TruePrefUtils.getInstance().init(zContext, prefNameNativeBanner).delayMs
                )
            } else {
                Timber.tag("AdmobInter").d(
                    "Native Banner Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                        zContext,
                        prefNameNativeBanner,
                        "Native Banner Ad"
                    )
                )
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun zShowBanner(
        context: Context,
        zBannerView: TrueZBannerView,
        bannerId: String,
        zIsWithFallback: Boolean = true
    ) {
        try {
            CoroutineScope(Dispatchers.Main).launch {
                if (bannerId.contains("/")) {
                    prefName = bannerId.substring(bannerId.lastIndexOf("/") + 1)
                }
                if (zContext != null) {
                    zBannerAdView = AdView(zContext)
                    zBannerAdView!!.adUnitId = bannerId
                    val adSize = adaptiveBannerAdSize
                    zBannerView.layoutParams.height =
                        zGetPixelFromDp(zContext, 60)
                    zAddPlaceHolderTextView(zBannerView)
                    zBannerAdView?.adSize = adSize
                    zBannerAdView?.adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            zAdCallbacks?.zAdLoaded(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zWhatAd = TrueWhatAd.Z_BANNER
                            )
                            TruePrefUtils.getInstance().init(context, prefName)
                                .zUpdateImpressionCounter()
                            if (zBannerAdView!!.parent != null) {
                                (zBannerAdView!!.parent as ViewGroup).removeView(zBannerAdView)
                            }
                            val cd = ColorDrawable()
                            val styles =
                                TrueBannerTemplateStyle.Builder().withMainBackgroundColor(cd)
                                    .build()
                            AdmobBannerLayoutBinding.inflate(
                                LayoutInflater.from(zContext),
                                null,
                                false
                            ).apply {
                                myTemplate.visibility = View.VISIBLE
                                myTemplate.setStyles(styles)

                                zBannerView.zShowAdView(viewGroup = root)
                            }
                            zBannerView.addView(zBannerAdView)
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            zBannerView.zShowHideAdLoader(true)
                            zAdCallbacks?.zAdFailedToLoad(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zError = TrueError(
                                    zMessage = loadAdError.message,
                                    zCode = loadAdError.code,
                                    zDomain = loadAdError.domain,
                                ),
                                zNativeView = zBannerView,
                                zWhatAd = TrueWhatAd.Z_BANNER,
                                zIsWithFallback = zIsWithFallback,
                            )

                        }

                        override fun onAdClosed() {
                            zAdCallbacks?.zAdClosed(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zWhatAd = TrueWhatAd.Z_BANNER
                            )
                        }

                        override fun onAdOpened() {
                            zAdCallbacks?.zNativeAdOpened(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zWhatAd = TrueWhatAd.Z_BANNER
                            )
                            TruePrefUtils.getInstance().init(context, prefName)
                                .zUpdateClicksCounter()
                            if (TruePrefUtils.getInstance()
                                    .init(context, prefName).hideOnClick
                            ) {
                                if (zBannerAdView != null) {
                                    zBannerAdView!!.visibility = View.GONE
                                }
                            }
                        }

                        override fun onAdClicked() {
                            zAdCallbacks?.zAdClicked(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zWhatAd = TrueWhatAd.Z_BANNER
                            )
                        }

                        override fun onAdImpression() {
                            zAdCallbacks?.zAdImpression(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zWhatAd = TrueWhatAd.Z_BANNER
                            )
                        }
                    }
                    val adRequest = AdRequest.Builder().build()
                    /** Check if Ad is Banned*/
                    if (!TrueAdLimitUtils.isBanned(context, prefName, "Banner Ad")) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            {

                                zBannerAdView!!.loadAd(adRequest)
                            },
                            TruePrefUtils.getInstance().init(context, prefName).delayMs
                        )
                    } else {
                        Timber.tag("Banner_Ads")
                            .d(
                                "Banner Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                                    context,
                                    prefName,
                                    "Banner Ad"
                                )
                            )
                    }
                }
            }
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }

    }

    private fun zAddPlaceHolderTextView(adContainerView: ViewGroup?) {
        val valueTV = TextView(zContext)
        valueTV.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        valueTV.gravity = Gravity.CENTER
        adContainerView!!.addView(valueTV)
    }


    @SuppressLint("BinaryOperationInTimber")
    fun zShowNativeAdvanced(
        context: Context,
        zNativeAdvancedView: TrueZNativeAdvancedView,
        nativeAdvancedId: String,
        zIsWithFallback: Boolean = true
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            if (nativeAdvancedId.contains("/")) {
                prefNameNative =
                    nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
            }
            val adLoader = AdLoader.Builder(
                zContext!!,
                nativeAdvancedId
            )
                .forNativeAd { unifiedNativeAd: NativeAd? ->
                    val cd = ColorDrawable()
                    val styles =
                        TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
                    AdmobNativeAdvancedLayoutBinding.inflate(
                        LayoutInflater.from(zContext),
                        null,
                        false
                    ).apply {
                        myTemplate.visibility = View.VISIBLE
                        myTemplate.setStyles(styles)
                        unifiedNativeAd?.let {
                            myTemplate.setNativeAd(it, false)
                        }
                        zNativeAdvancedView.zShowAdView(viewGroup = root)
                    }

                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        zNativeAdvancedView.zShowHideAdLoader(true)
                        zAdCallbacks?.zAdFailedToLoad(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED,
                            zError = TrueError(
                                zMessage = loadAdError.message,
                                zCode = loadAdError.code,
                                zDomain = loadAdError.domain,
                            ),
                            zNativeView = zNativeAdvancedView,
                            zIsWithFallback = zIsWithFallback
                        )
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        zAdCallbacks?.zAdClosed(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                        )
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        zAdCallbacks?.zNativeAdOpened(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                        )
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        zAdCallbacks?.zAdLoaded(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                        )
                        TruePrefUtils.getInstance().init(context, prefNameNative)
                            .zUpdateImpressionCounter()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        zAdCallbacks?.zAdClicked(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                        )
                        TruePrefUtils.getInstance().init(context, prefNameNative)
                            .zUpdateClicksCounter()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        zAdCallbacks?.zAdImpression(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                        )
                    }
                })
                .build()
            if (!TrueAdLimitUtils.isBanned(context, prefNameNative, "Native Ad")) {
                /** It will be executed when its true*/
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        adLoader.loadAd(AdRequest.Builder().build())
                        /**delay(TruePrefUtils.getInstance().init(context, prefNameNative).delayMs)*/
                    },
                    TruePrefUtils.getInstance().init(context, prefNameNative).delayMs
                )
            } else {
                Timber.tag("Native_Ads")
                    .d(
                        "Native Ad Is Banned : " + !TrueAdLimitUtils.isBanned(
                            context,
                            prefNameNative,
                            "Native Ad"
                        )
                    )
            }
        }

    }

    fun zSetInterCallbacks(interCallbacks: TrueInterCallbacks) {
        zInterCallbacks = interCallbacks
    }

    fun zSetNativeCallbacks(adCallbacks: TrueAdCallbacks) {
        zAdCallbacks = adCallbacks
    }


    init {
        zContext?.let { context ->
            MobileAds.initialize(context) { initializationStatus ->
                Timber.d("Ad Mob Initialization status ${initializationStatus.adapterStatusMap}")
            }
        }
    }

    /**Adaptive Banner Size*/
    @Suppress("DEPRECATION")
    private val adaptiveBannerAdSize: AdSize
        get() {
            val display =
                (zContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels = outMetrics.widthPixels.toFloat()
            val density = outMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(zContext, adWidth)
        }


    /**Load Ads Dialogue*/
    private fun loadAds(activity: Activity) {
        val loadAdsLayoutBinding: LoadAdsLayoutBinding =
            LoadAdsLayoutBinding.inflate(activity.layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(loadAdsLayoutBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        loadAdsLayoutBinding.tvMessage.text =
            TrueAdManager.context.resources.getString(R.string.loading_ad)
    }

    /**Load And Show Native In Advance*/
    fun loadAdmobNativeInAdvance(
        context: Context,
        nativeAdvancedId: String,
    ) {
        if (nativeAdvancedId.contains("/")) {
            prefNameNativeInAdvanced =
                nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
        }
        val builder = AdLoader.Builder(
            context, nativeAdvancedId
        )
        builder.forNativeAd { nativeAd ->
            if (mAdmobNative != null) {
                mAdmobNative!!.destroy()
            }
            mAdmobNative = nativeAd
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions: NativeAdOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        admobNativeAdLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                zAdCallbacks?.zAdClosed(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }

            override fun onAdOpened() {
                super.onAdOpened()
                zAdCallbacks?.zNativeAdOpened(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                zAdCallbacks?.zAdLoaded(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
                TruePrefUtils.getInstance().init(context, prefNameNative)
                    .zUpdateImpressionCounter()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                zAdCallbacks?.zAdClicked(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
                TruePrefUtils.getInstance().init(context, prefNameNative)
                    .zUpdateClicksCounter()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                zAdCallbacks?.zAdImpression(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }
        }).build()
        if (isAppInstalledFromPlay(context)) {
            if (!TrueAdLimitUtils.isBanned(
                    context,
                    prefNameNativeInAdvanced,
                    "Native Ad In Advance"
                )
            ) {
                /** It will be executed when its true*/
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        admobNativeAdLoader!!.loadAd(AdRequest.Builder().build())
                    }, TruePrefUtils.getInstance().init(context, prefNameNative).delayMs
                )
            }
        }
    }

    private fun inflateAdNativeAdInAdvance(
        context: Activity,
        nativeAd: NativeAd,
        zNativeAdvancedView: TrueZNativeAdvancedView
    ) {
        val cd = ColorDrawable()
        val styles =
            TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
        AdmobNativeAdvancedLayoutBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        ).apply {
            myTemplate.visibility = View.VISIBLE
            myTemplate.setStyles(styles)
            nativeAd.let {
                myTemplate.setNativeAd(it, false)
            }
            zNativeAdvancedView.zShowAdView(viewGroup = root)
        }
    }

    fun showAdmobNativeInAdvance(
        context: Activity,
        nativeAdId: String,
        zNativeAdvancedView: TrueZNativeAdvancedView,
    ) {
        if (isAppInstalledFromPlay(context)) {
            if (admobNativeAdLoader != null && !admobNativeAdLoader!!.isLoading) {
                if (mAdmobNative != null) {
                    inflateAdNativeAdInAdvance(context, mAdmobNative!!, zNativeAdvancedView)
                }
            } else {
                loadAdmobNativeInAdvance(context, nativeAdId)
            }
        }
    }

    /**Load And Show Flipping Native In Advance*/
    fun loadAdmobFlippingNativeInAdvance(
        context: Context,
        nativeAdvancedId: String,
    ) {
        if (nativeAdvancedId.contains("/")) {
            prefNameFlippingNativeInAdvanced =
                nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
        }
        val builder = AdLoader.Builder(
            context, nativeAdvancedId
        )
        builder.forNativeAd { nativeAd ->
            if (mFlippingAdmobNative != null) {
                mFlippingAdmobNative!!.destroy()
            }
            mFlippingAdmobNative = nativeAd
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions: NativeAdOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        mFlippingAdmobNativeAdLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }

            override fun onAdClosed() {
                super.onAdClosed()
                zAdCallbacks?.zAdClosed(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }

            override fun onAdOpened() {
                super.onAdOpened()
                zAdCallbacks?.zNativeAdOpened(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                zAdCallbacks?.zAdLoaded(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
                TruePrefUtils.getInstance().init(context, prefNameFlippingNativeInAdvanced)
                    .zUpdateImpressionCounter()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                zAdCallbacks?.zAdClicked(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
                TruePrefUtils.getInstance().init(context, prefNameFlippingNativeInAdvanced)
                    .zUpdateClicksCounter()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                zAdCallbacks?.zAdImpression(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }
        }).build()
        if (isAppInstalledFromPlay(context)) {
            if (!TrueAdLimitUtils.isBanned(
                    context,
                    prefNameFlippingNativeInAdvanced,
                    "Native Ad In Advance"
                )
            ) {
                /** It will be executed when its true*/
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        mFlippingAdmobNativeAdLoader!!.loadAd(AdRequest.Builder().build())
                    },
                    TruePrefUtils.getInstance()
                        .init(context, prefNameFlippingNativeInAdvanced).delayMs
                )
            }
        }
    }

    private fun inflateFlippingNativeAdInAdvance(
        nativeAd: NativeAd,
        trueZNativeBannerSimpleView: TrueZNativeBannerFlippingView
    ) {
        val cd = ColorDrawable()
        val styles =
            TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
        AdmobNativeBannerLayoutBinding.inflate(
            LayoutInflater.from(zContext),
            null,
            false
        ).apply {
            myTemplate.visibility = View.VISIBLE
            myTemplate.setStyles(styles)
            nativeAd.let {
                myTemplate.setNativeAd(it, true)
            }
            trueZNativeBannerSimpleView.zShowAdView(viewGroup = root)
        }
    }

    fun showAdmobFlippingNativeInAdvance(
        context: Activity,
        nativeAdId: String,
        trueZNativeBannerSimpleView: TrueZNativeBannerFlippingView,
    ) {
        if (isAppInstalledFromPlay(context)) {
            if (mFlippingAdmobNativeAdLoader != null && !mFlippingAdmobNativeAdLoader!!.isLoading) {
                if (mFlippingAdmobNative != null) {
                    inflateFlippingNativeAdInAdvance(
                        mFlippingAdmobNative!!,
                        trueZNativeBannerSimpleView
                    )
                }
            } else {
                loadAdmobFlippingNativeInAdvance(context, nativeAdId)
            }
        }
    }

    /**Load And Show Simple Native In Advance*/
    fun loadAdmobSimpleNativeInAdvance(
        context: Context,
        nativeAdvancedId: String,
    ) {
        Toast.makeText(context, "Native Advance Ad Id: $nativeAdvancedId", Toast.LENGTH_SHORT)
            .show()
        if (nativeAdvancedId.contains("/")) {
            prefNameSimpleNativeInAdvanced =
                nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
        }
        Toast.makeText(
            context,
            "Prefer Native 1 2: $prefNameSimpleNativeInAdvanced",
            Toast.LENGTH_SHORT
        ).show()
        val builder = AdLoader.Builder(
            context, nativeAdvancedId
        )
        builder.forNativeAd { nativeAd ->
            if (mSimpleAdmobNative != null) {
                mSimpleAdmobNative!!.destroy()
            }
            mSimpleAdmobNative = nativeAd
        }
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions: NativeAdOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        Toast.makeText(context, "Video option: $videoOptions", Toast.LENGTH_SHORT).show()
        builder.withNativeAdOptions(adOptions)
        Toast.makeText(context, "Builder: $builder", Toast.LENGTH_SHORT).show()
        mSimpleAdmobNativeAdLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                Toast.makeText(context, "Ad Failed: ${loadAdError.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                zAdCallbacks?.zAdClosed(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }

            override fun onAdOpened() {
                super.onAdOpened()
                zAdCallbacks?.zNativeAdOpened(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                zAdCallbacks?.zAdLoaded(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
                TruePrefUtils.getInstance().init(context, prefNameSimpleNativeInAdvanced)
                    .zUpdateImpressionCounter()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                zAdCallbacks?.zAdClicked(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
                TruePrefUtils.getInstance().init(context, prefNameSimpleNativeInAdvanced)
                    .zUpdateClicksCounter()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                zAdCallbacks?.zAdImpression(
                    zAdType = TrueAdsType.Z_ADMOB,
                    zWhatAd = TrueWhatAd.Z_NATIVE_ADVANCED
                )
            }
        }).build()
        Toast.makeText(context, "Ad Builder Is : ${mSimpleAdmobNativeAdLoader}", Toast.LENGTH_SHORT)
            .show()
        if (isAppInstalledFromPlay(context)) {
//            Toast.makeText(context, "Builder Is : $builder", Toast.LENGTH_SHORT).show()

            Toast.makeText(
                context,
                "prefNameSimpleNativeInAdvanced123: $prefNameSimpleNativeInAdvanced",
                Toast.LENGTH_SHORT
            ).show()
            if (!TrueAdLimitUtils.isBanned(
                    context,
                    prefNameSimpleNativeInAdvanced,
                    "Native Ad In Advance"
                )
            ) {
                Toast.makeText(
                    context,
                    "prefNameSimpleNativeInAdvanced12345: $prefNameSimpleNativeInAdvanced",
                    Toast.LENGTH_SHORT
                ).show()
                /** It will be executed when its true*/
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        mSimpleAdmobNativeAdLoader!!.loadAd(AdRequest.Builder().build())
                        Toast.makeText(
                            context,
                            "prefNameSimpleNativeInAdvanced123456: $prefNameSimpleNativeInAdvanced",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    TruePrefUtils.getInstance()
                        .init(context, prefNameSimpleNativeInAdvanced).delayMs
                )
            }
        }
    }

    private fun inflateSimpleNativeAdInAdvance(
        nativeAd: NativeAd,
        trueZNativeBannerSimpleView: TrueZNativeBannerSimpleView
    ) {
        val cd = ColorDrawable()
        val styles =
            TrueNativeTemplateStyle.Builder().withMainBackgroundColor(cd).build()
        AdmobNativeBannerLayoutSimpleBinding.inflate(
            LayoutInflater.from(zContext),
            null,
            false
        ).apply {
            myTemplate.visibility = View.VISIBLE
            myTemplate.setStyles(styles)
            nativeAd.let {
                myTemplate.setNativeAd(it, false)
            }
            trueZNativeBannerSimpleView.zShowAdView(viewGroup = root)
        }
    }

    fun showAdmobSimpleNativeInAdvance(
        context: Activity,
        nativeAdId: String,
        trueZNativeBannerSimpleView: TrueZNativeBannerSimpleView,
    ) {
        if (isAppInstalledFromPlay(context)) {
            if (mSimpleAdmobNativeAdLoader != null && !mSimpleAdmobNativeAdLoader!!.isLoading) {
                Toast.makeText(
                    context,
                    "mSimpleAdmobNativeAdLoader: $mSimpleAdmobNativeAdLoader",
                    Toast.LENGTH_SHORT
                ).show()
                if (mSimpleAdmobNative != null) {
                    Toast.makeText(
                        context,
                        "mSimpleAdmobNativeAdLoader123: $mSimpleAdmobNativeAdLoader",
                        Toast.LENGTH_SHORT
                    ).show()
                    inflateSimpleNativeAdInAdvance(
                        mSimpleAdmobNative!!,
                        trueZNativeBannerSimpleView
                    )
                }
            } else {
                Toast.makeText(context, "Request To Load:", Toast.LENGTH_SHORT).show()
                loadAdmobSimpleNativeInAdvance(context, nativeAdId)
            }
        }
    }

}