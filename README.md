# TrueMuslim_AdsPlugin
Works with Admob.
Added ad types are NativeBanner, NativeAdvanced, Interstitial and Banner Ads for Admob.
Ui Always stays stable, no displacement.
By Default some fallback strategies are added for all Ads in case any fails other takes its place.
Shimmer animations while Ads is loaded.
Banner size is calculated automatically according to varying screen sizes.

Step 1. Add the JitPack repository to your build file , Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency

	dependencies {
	       implementation 'com.github.TrueMuslimStudio:TrueMuslimAdsPlugin:1.2.1'
	}
	
	
Step 3. Add InAppUpdate Module in Main Activity:

 	lateinit var trueInAppUpdate = TrueInAppUpdate(this)
 	trueInAppUpdate.getInAppUpdate().
 
Step 4. Add Admob App Open Ad in Application Class:

	var trueZAppOpenAd: TrueZAppOpenAd? = null
	trueZAppOpenAd = TrueZAppOpenAd(this, "Add App Open Ad Id")
	
Step 5. Get Json File from server and Add it in BaseApplication	:

	if (TrueConstants.isNetworkAvailable(TrueAdManager.context) && TrueConstants.isNetworkSpeedHigh() && TrueConstants.isAppInstalledFromPlay(this)) {
            TrueAntiAdLimit.getInstance()
                .init(this, "https://example.json")
        }
	
To get a Git project into your build:

Step 6. Add the (Required) debug and release ids in the project level gradle file like so:

	resValue 'string', 'AdMob_AppId', '"ca-app-pub-3940256099942544~3347511713"'
	resValue 'string', 'Admob_BannerId', '"ca-app-pub-3940256099942544/6300978111"'
	resValue 'string', 'Admob_NativeAdvancedId', '"ca-app-pub-3940256099942544/2247696110"'
	resValue 'string', 'Admob_InterstitialId', '"ca-app-pub-3940256099942544/1033173712"'

Step 7. Add the relevant strings in strings file

	 <string name="admob_app_id">@string/AdMob_AppId</string>
	 <string name="admob_banner_id">@string/Admob_BannerId</string>
	 <string name="admob_native_advanced_id">@string/Admob_NativeAdvancedId</string>
	 <string name="admob_interstitial_id">@string/Admob_InterstitialId</string>
 
Step 8. Initialize the AndroidAdManager in Application class. by passing the Required
context in the Admanager's constructor.

	 TrueAdManager.zInitializeAds(this)
    
Step 9. By default all priorities are set to Admob.

	TrueAdManager.zSetNativeAdvancedPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetBannerPriority(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetNativeBannerPriorityFlipping(TrueAdPriorityType.Z_AD_MOB)
        TrueAdManager.zSetNativeBannerPrioritySimple(TrueAdPriorityType.Z_AD_MOB) 

Step 10. To set the Ads Callback use the following methods in your calling activity.

	  TrueAdManager.zhSetInterCallbacks(
            "Interstitial Ad",
            TrueAdsCalBackObject.zInterCalBacks(this)
        )
        TrueAdManager.zSetNativeCallbacks(
           "Native Ad Id",
            TrueAdsCalBackObject.zNativeCalBacks(this)
        )

Step 11:Load Ads By passing View And AdId.

Banner Ad:

	TrueAdManager.zShowBannerWithOutFallback(zMainBinding.zBannerContainer,getString(R.string.Admob_BannerId))
	    
Native Advance Ad:

	TrueAdManager.zShowNativeAdvanced(hMainBinding.zNativeAdvancedBanner,getString(R.string.Admob_NativeAdvancedId))

Interstitial Ad:

	TrueAdManager.zShowInterstitial(
                this,
              adsId
            )
Simple Native Banner Ad:
	
	TrueAdManager.zShowSimpleNativeBanner(
              zMainBinding.zNativeSimpleBanner,
              getString(R.string.Admob_NativeAdvancedId)
          )
	  
Fliping Native Banner Ad:
	
	TrueAdManager.zShowFlippingNativeBanner(
              zMainBinding.zNativeFlippingBanner,
              getString(R.string.Admob_NativeAdvancedId)
          )
	  
Load And Show Interstitial Ads at Same Time:

	TrueAdManager.zShowInterstitial(this,resources.getString(R.string.Admob_InterstitialId))
	
Load InTerstitial Ad In Advance On Splash Sreen Accourding Ads Implementation:

	TrueAdManager.zLoadInterstitialInAdvance(
           this,
            adId
        )
	

Load Native Advance Ad in Advance On Splash Screen:

	 TrueAdManager.zLoadNativeAdInAdvance(
            this,
            adsId
        )
	
Load Simple Native Banner Ad in Advance On Splash Screen:	

        TrueAdManager.zLoadSimpleNativeAdInAdvance(
            this,
            adsId
        )
	
Load Flipping Native Banner Ad in Advance On Splash Screen:	

	 TrueAdManager.zLoadFlippingNativeAdInAdvance(
            this,
            adsId
        )
	
Show InTerstitial Ad In Advance :	

	TrueAdManager.zShowInterstitialInAdvance(this)
	
Show Native Ad in Advance:

	 TrueAdManager.zShowNativeAdInAdvance(
           this,
           adsId,
           adLayout
        )
	
Show Simple Native Banner Ad in Advance:

	 TrueAdManager.zShowSimpleNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id),
            zMainBinding.zNativeSimpleBanner
        )
	
Show Flipping Native Banner Ad in Advance:	

	 TrueAdManager.zShowFlippingNativeAdInAdvance(
            this,
            resources.getString(R.string.admob_native_advanced_id),
            zMainBinding.zNativeFlippingBanner
        )
	
Step 12: By default AdContainers i.e. ZnativeBannerView and ZnativeAdvacncedView are rounded and
are given app's primary color and stroke. To change use method:

	fun zSetBackGroundDrawable(
	    zBackgroundColor: Int,
	    zCornerRadius: Int = 6,
	    zStroke: Stroke = Stroke(context)
	) {
	    val zGradientDrawable = GradientDrawable()
	    zGradientDrawable.apply {
		setColor(zBackgroundColor)
		cornerRadius = hDp(context, zCornerRadius)
		setStroke(
		    zStroke.zWidth,
		    zStroke.zColor
		)
		zLayoutHAdcontainerBinding.zAdRootCL.background = zGradientDrawable
	    }
	}

Step 13: Layout for Native Banner Ad.

	 <com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerView
	 android:layout_width="match_parent"
	 android:layout_height="wrap_content"
	 app:zBackgroundColor="@color/black"
	 app:zCornerRadius="6"
	 app:zStrokeWidth="6"
	 app:zStrokeColor="@color/black"/>
 
 Step 14: To Show you own Holder while ad is loading over NativeAdvanced use following:.
 
	 <com.zee.truemuslims.ads.modules.customadview.TrueZNativeAdvancedView
		android:layout_width="match_parent"
		android:layout_height="wrap_content"/>
		
 Step 15: Add Service Class in Manifest.

	<service android:name="com.zee.truemuslims.ads.modules.adlimits.TrueJSONPullService" />
