# TrueMuslim_AdsPlugin
Works with Admob.
Added ad types are NativeBanner, NativeAdvanced, Interstitial and Banner Ads for Admob.
Ui Always stays stable, no displacement.
By Default some fallback strategies are added for all Ads in case any fails other takes its place.
Shimmer animations while Ads is loaded.
Banner size is calculated automatically according to varying screen sizes.
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.TrueMuslimStudio:TrueMuslimAdsProject:1.0'
	}
	
Step 3. Add the (Required) debug and release ids in the project level gradle file like so:

resValue 'string', 'AdMob_AppId', '"ca-app-pub-3940256099942544~3347511713"'
resValue 'string', 'Admob_BannerId', '"ca-app-pub-3940256099942544/6300978111"'
resValue 'string', 'Admob_NativeAdvancedId', '"ca-app-pub-3940256099942544/2247696110"'
resValue 'string', 'Admob_InterstitialId', '"ca-app-pub-3940256099942544/1033173712"'

Step 4. Add the relevant strings in strings file

 <string name="admob_app_id">@string/AdMob_AppId</string>
 <string name="admob_banner_id">@string/Admob_BannerId</string>
 <string name="admob_native_advanced_id">@string/Admob_NativeAdvancedId</string>
 <string name="admob_interstitial_id">@string/Admob_InterstitialId</string>
 
Step 5. Initialize the AndroidAdManager in Application class. by passing the Required
ids and context in the Admanager's constructor.

 val zIdsMap = hashMapOf<AdsType, HashMap<TrueWhatAd, String>>()
 val zAdMobMap = hashMapOf<TrueWhatAd, String>()

 zAdMobMap[TrueWhatAd.Z_NATIVE_ADVANCED] = getString(R.string.admob_native_advanced_id)
 zAdMobMap[TrueWhatAd.Z_NATIVE_BANNER] = getString(R.string.admob_native_advanced_id)
 zAdMobMap[TrueWhatAd.Z_BANNER] = getString(R.string.admob_banner_id)
 zAdMobMap[TrueWhatAd.Z_INTER] = getString(R.string.admob_interstitial_id)


 zIdsMap[TrueAdsType.H_ADMOB] = zAdMobMap

 TrueAdManager.zInitializeAds(
        this,
        zIdsMap
    )
    
Step 6. By default all priorities are set to Admob->MopUp->Facebook-None. if one fails,
other takes its place. To change the priorities use following methods:

TrueAdManager.zSetNativeAdvancedPriority(TrueAdPriorityType.H_MOP_UP)
TrueAdManager.zSetNativeBannerPriority(TrueAdPriorityType.H_MOP_UP) 

Step 7. To set the Ads Callback use the following methods in your calling activity, and override the methods.

TrueAdManager.zSetInterCallbacks(zInterCallbacks)
TrueAdManager.zSetNativeCallbacks(zNativeCallbacks)

Callbacks for these adevents are available in "TrueAdCallbacks" and "TrueInterCallbacks" Abstract classes.
open fun zAdLoaded(
        zAdType: TrueAdsType,
        zWhatAd: TrueWhatAd,
    ) {
    }

    open fun zAdClicked(
        zAdType: TrueAdsType,
        zWhatAd: TrueWhatAd
    ) {
    }

    open fun zAdImpression(
        zAdType: TrueAdsType,
        zWhatAd: TrueWhatAd
    ) {
    }

    open fun zAdClosed(
        zAdType: TrueAdsType,
        zWhatAd: TrueWhatAd
    ) {
    }

    open fun zAdFailedToLoad(
        zAdType: TrueAdsType,
        zWhatAd: TrueWhatAd,
        zError: TrueError,
        zNativeView: ViewGroup,
        zIsWithFallback: Boolean
    ) {
    }

    open fun zNativeAdOpened(
        zAdType: TrueAdsType,
        zWhatAd: TrueWhatAd
    ) {
    }
    
Step 8: By default AdContainers i.e. HnativeBannerView and HnativeAdvacncedView are rounded and
are given app's primary color and stroke. To change use method:

fun zSetBackGroundDrawable(
    zBackgroundColor: Int,
    zCornerRadius: Int = 6,
    zStroke: Stroke = Stroke(context)
) {
    val zGradientDrawable = GradientDrawable()
    zGradientDrawable.apply {
        setColor(hBackgroundColor)
        cornerRadius = hDp(context, zCornerRadius)
        setStroke(
            zStroke.hWidth,
            zStroke.hColor
        )
        zLayoutHAdcontainerBinding.zAdRootCL.background = zGradientDrawable
    }
}

Step 9: Layout for Native Banner Ad.

 <com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerView
 android:id="@+id/zNativeBanner"
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 app:hBackgroundColor="@color/black"
 app:hCornerRadius="6"
 app:hStrokeWidth="6"
 app:hStrokeColor="@color/black"/>
 
 Step 10: To Show you own Holder while ad is loading over NativeAdvanced use following:.
 
 <com.zee.truemuslims.ads.modules.customadview.TrueZNativeAdvancedView
        android:id="@+id/zNativeAdvancedBanner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hLoaderContainer="@layout/native_advanced_alternative"/>
