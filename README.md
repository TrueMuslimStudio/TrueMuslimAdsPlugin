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
	        implementation 'com.github.TrueMuslimStudio:TrueMuslimAdsProject:1.0.3'
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

	 TrueAdManager.zInitializeAds(this)
    
Step 6. By default all priorities are set to AdmobNone.

	TrueAdManager.zSetNativeAdvancedPriority(TrueAdPriorityType.Z_MOP_UP)
	TrueAdManager.zSetNativeBannerPriority(TrueAdPriorityType.Z_MOP_UP) 

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
Step 8:Load Ads By passing View And AdId.

Banner AdView

	zMainBinding.zBannerContainer,getString(R.string.Admob_BannerId)
	    
Native Advance AdView

	TrueAdManager.zShowNativeAdvanced(hMainBinding.zNativeAdvancedBanner,getString(R.string.Admob_NativeAdvancedId))
	    
Interstitial Ads:

	TrueAdManager.zShowInterstitial(this,resources.getString(R.string.Admob_InterstitialId))
	
Step 9: By default AdContainers i.e. ZnativeBannerView and ZnativeAdvacncedView are rounded and
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

Step 10: Layout for Native Banner Ad.

	 <com.zee.truemuslims.ads.modules.customadview.TrueZNativeBannerView
	 android:id="@+id/zNativeBanner"
	 android:layout_width="match_parent"
	 android:layout_height="wrap_content"
	 app:zBackgroundColor="@color/black"
	 app:zCornerRadius="6"
	 app:zStrokeWidth="6"
	 app:zStrokeColor="@color/black"/>
 
 Step 11: To Show you own Holder while ad is loading over NativeAdvanced use following:.
 
	 <com.zee.truemuslims.ads.modules.customadview.TrueZNativeAdvancedView
		android:id="@+id/zNativeAdvancedBanner"
		android:layout_width="match_parent"
		android:layout_height="wrap_content"
		app:zLoaderContainer="@layout/native_advanced_alternative"/>
