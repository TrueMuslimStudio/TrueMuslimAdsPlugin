package com.zee.truemuslims.ads.modules.abtesting

import android.app.Activity
import android.widget.Toast
import com.zee.truemuslims.ads.modules.BuildConfig
import com.zee.truemuslims.ads.modules.TrueAdManager

class CheckABTestingClass() {
    /** 1 - get a string variable , fetch KEY from firebase console and pass it*/
    // 1- var mainConfigureStringValue = ""
    fun getABTesting(
//        context: Activity
    ) {
        /** 2 - Set Firebase.remoteConfigure*/
        // 2 - val remoteConfig = Firebase.remoteConfig

        /**Set Time to Refresh Value From Firebase on RunTime set it default otherwise you can change it */
        // 3 -val configSettings = remoteConfigSettings {
        //    minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
        //        0 // Kept 0 for quick debug
        //    } else {
        //        60 * 60 // Change this based on your requirement
        //    }
        //}

        /** 3 - Set Configure Setting to remoteConfig.setConfigSettingsAsync*/
        // 4 -remoteConfig.setConfigSettingsAsync(configSettings)

        /** 4 - Fetch Key Value from Firebase Console*/
        // 5 - remoteConfig.fetchAndActivate().addOnCompleteListener(context) { task ->
        //        if (task.isSuccessful) {
        //        val updated = task.result
        //        mainConfigureStringValue = remoteConfig.getString(configureStringValue)
        //        remoteConfig.fetchAndActivate()
        //        } else {
        //        val updated = task.result
        //        }

        /** 5 - Call LoadAds*/
        //        5 - loadAds()
        //}

    }

    /** 6 - Fetch Data From Firebase Using Key*/
    //  6 -  private fun loadAds() {
    //        when (configureStringValue) {
    //            "ad1" -> {
    //                TrueAdManager.zShowBannerWithOutFallback(binding.hBannerContainer)
    //            }
    //            "ad2" -> {
    //                TrueAdManager.zShowNativeBanner(binding.hNativeBanner)
    //            }
    //            "ad3" -> {
    //                Toast.makeText(applicationContext, "Ad Loaded", Toast.LENGTH_SHORT).show()
    //            }
    //        }
    //    }
}