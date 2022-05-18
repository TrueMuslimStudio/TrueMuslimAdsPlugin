package com.zee.truemuslims.ads.modules

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import com.zee.truemuslims.ads.modules.adlimits.TrueAdLimitUtils
import com.zee.truemuslims.ads.modules.adlimits.TruePrefUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.zee.truemuslims.ads.modules.callbacks.TrueAdCallbacks
import com.zee.truemuslims.ads.modules.callbacks.TrueInterCallbacks
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeAdvancedView
import com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerView
import com.zee.truemuslims.ads.modules.databinding.AdmobNativeAdvancedLayoutBinding
import com.zee.truemuslims.ads.modules.databinding.AdmobNativeBannerLayoutBinding
import com.zee.truemuslims.ads.modules.types.TrueAdsType
import com.zee.truemuslims.ads.modules.types.TrueWhatAd
import com.zee.truemuslims.ads.modules.templates.TrueNativeTemplateStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class TrueAdMobManager(
    private val zContext: Context?,
    private val zIdsMap: HashMap<TrueWhatAd, String>?,
) {
    var zInterstitialAd: InterstitialAd? = null
        private set
    private var zBannerAdView: AdView? = null
    private var zInterCallbacks: TrueInterCallbacks? = null
    private var zAdCallbacks: TrueAdCallbacks? = null
    private var prefName: String? = null
    private var prefNameInter: String? = null
    private var prefNameNative: String? = null
    private var prefNameNativeBanner: String? = null

    @SuppressLint("BinaryOperationInTimber")
    fun zLoadInterstitialAd(
        context: Context,
        zTimeOut: Long = TrueConstants.h3SecTimeOut,
    ) {
        zIdsMap?.get(TrueWhatAd.Z_INTER)?.let { interId ->
            if (interId.contains("/")) {
                prefNameInter = interId.substring(interId.lastIndexOf("/") + 1)
            }
            CoroutineScope(Dispatchers.Main).launch {
                var zCallBackCalled = false
                if (!TrueAdLimitUtils.isBanned(context, prefNameInter, "Interstitial Ad")) {
                    /** it will be executed when its true*/
                    val adRequest = AdRequest.Builder().build()
                    Handler().postDelayed(
                        {
                            InterstitialAd.load(
                                zContext!!,
                                interId,
                                adRequest,
                                object : InterstitialAdLoadCallback() {
                                    override fun onAdLoaded(interstitialAd: InterstitialAd) {

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
                                        zCallBackCalled = true
                                    }

                                }
                            )
                            if (zCallBackCalled.not()) {
                                zInterCallbacks?.zOnAdTimedOut(TrueAdsType.Z_ADMOB)
                            }
                            /*delay(hTimeOut)*/
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

    }


    @SuppressLint("BinaryOperationInTimber")
    fun zShowNativeBanner(
        zNativeBannerView: TrueZNativeBannerView,
        zIsWithFallback: Boolean = true,
    ) {
        zIdsMap?.get(TrueWhatAd.Z_NATIVE_BANNER)?.let { nativeAdvancedId ->
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
                    AdmobNativeBannerLayoutBinding.inflate(
                        LayoutInflater.from(zContext),
                        null,
                        false
                    ).apply {
                        myTemplate.visibility = View.VISIBLE
                        myTemplate.setStyles(styles)
                        NativeAd?.let {
                            myTemplate.setNativeAd(it)
                        }
                        zNativeBannerView.zShowAdView(viewGroup = root)
                    }


                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        zNativeBannerView.zShowHideAdLoader(true)
                        zAdCallbacks?.zAdFailedToLoad(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER,
                            zError = TrueError(
                                zMessage = loadAdError.message,
                                zCode = loadAdError.code,
                                zDomain = loadAdError.domain,
                            ),
                            zNativeView = zNativeBannerView,
                            zIsWithFallback = zIsWithFallback

                        )
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        zAdCallbacks?.zAdClosed(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        zAdCallbacks?.zNativeAdOpened(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        zAdCallbacks?.zAdLoaded(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                        TruePrefUtils.getInstance().init(zContext, prefNameNativeBanner)
                            .zUpdateImpressionCounter()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        zAdCallbacks?.zAdClicked(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                        TruePrefUtils.getInstance().init(zContext, prefNameNativeBanner)
                            .zUpdateClicksCounter()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        zAdCallbacks?.zAdImpression(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                    }
                })
                .build()
            if (!TrueAdLimitUtils.isBanned(zContext, prefNameNativeBanner, "Native Banner Ad")) {
                Handler().postDelayed(
                    { adLoader.loadAd(AdRequest.Builder().build()) },
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

    private val hAdSize: AdSize
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

    @SuppressLint("BinaryOperationInTimber")
    fun zShowBanner(
        context: Context,
        zAdViewGroup: ViewGroup,
        zIsWithFallback: Boolean = true,
    ) {
        try {
            zIdsMap?.get(TrueWhatAd.Z_BANNER)?.let { bannerId ->
                if (bannerId.contains("/")) {
                    prefName = bannerId.substring(bannerId.lastIndexOf("/") + 1)
                }
                if (zContext != null) {
                    zBannerAdView = AdView(zContext)
                    zBannerAdView!!.adUnitId = bannerId
                    val adSize = hAdSize
                    zAdViewGroup.layoutParams.height =
                        zGetPixelFromDp(zContext, 60)
                    zAddPlaceHolderTextView(zAdViewGroup)
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
                            zAdViewGroup.addView(zBannerAdView)
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            zAdCallbacks?.zAdFailedToLoad(
                                zAdType = TrueAdsType.Z_ADMOB,
                                zError = TrueError(
                                    zMessage = loadAdError.message,
                                    zCode = loadAdError.code,
                                    zDomain = loadAdError.domain,
                                ),
                                zNativeView = zAdViewGroup,
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
                            if (TruePrefUtils.getInstance().init(context, prefName).hideOnClick) {
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
                        Handler().postDelayed(
                            { zBannerAdView!!.loadAd(adRequest) },
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
        zIsWithFallback: Boolean = true,
    ) {
        zIdsMap?.get(TrueWhatAd.Z_NATIVE_ADVANCED)?.let { nativeAdvancedId ->
            if (nativeAdvancedId.contains("/")) {
                prefNameNative = nativeAdvancedId.substring(nativeAdvancedId.lastIndexOf("/") + 1)
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
                            myTemplate.setNativeAd(it)
                        }
                        zNativeAdvancedView.zShowAdView(viewGroup = root)
                    }

                }.withAdListener(object : AdListener() {
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
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        zAdCallbacks?.zNativeAdOpened(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        zAdCallbacks?.zAdLoaded(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                        TruePrefUtils.getInstance().init(context, prefNameNative)
                            .zUpdateImpressionCounter()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        zAdCallbacks?.zAdClicked(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                        TruePrefUtils.getInstance().init(context, prefNameNative)
                            .zUpdateClicksCounter()
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        zAdCallbacks?.zAdImpression(
                            zAdType = TrueAdsType.Z_ADMOB,
                            zWhatAd = TrueWhatAd.Z_NATIVE_BANNER
                        )
                    }
                })
                .build()
            if (!TrueAdLimitUtils.isBanned(context, prefNameNative, "Native Ad")) {
                /** It will be executed when its true*/
                Handler().postDelayed(
                    { adLoader.loadAd(AdRequest.Builder().build()) },
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

    companion object {
        fun zGetPixelFromDp(application: Context?, dp: Int): Int {
            val display =
                (application!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val scale = outMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }


    init {
        zContext?.let { context ->
            MobileAds.initialize(context) { initializationStatus ->
                Timber.d("Ad Mob Initialization status ${initializationStatus.adapterStatusMap}")
            }
        }
    }

}