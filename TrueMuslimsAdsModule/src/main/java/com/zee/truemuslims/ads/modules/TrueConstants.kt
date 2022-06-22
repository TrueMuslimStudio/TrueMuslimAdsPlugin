package com.zee.truemuslims.ads.modules

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.zee.truemuslims.ads.modules.TrueAdManager.context
import java.lang.Exception


object TrueConstants {
    const val h2SecTimeOut = 2000L
    const val h3SecTimeOut = 3000L
    const val h5SecTimeOut = 5000L
    const val h8SecTimeOut = 8000L
    var mShowInterstitialAds = false
    fun isInterstitialAdShow(context: Context): Boolean {
        return mShowInterstitialAds
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        /** For 29 api or above*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
        /** For below 29 api*/
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    fun isNetworkSpeedHigh(): Boolean {
        var networkSpeed = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo

        /**should check null because in airplane mode it will be null*/
        val nc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.getNetworkCapabilities(cm.activeNetwork)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        try {
            val downSpeed = nc!!.linkDownstreamBandwidthKbps
            val upSpeed = nc.linkUpstreamBandwidthKbps
            if (downSpeed / 1000 >= 2) {
                networkSpeed = true
            }
        } catch (e: Exception) {

        }
        return networkSpeed
    }

    fun isAppInstalledFromPlay(mContext: Context): Boolean {
        return if (BuildConfig.DEBUG) true else {
            try {
                val applicationInfo = mContext.packageManager.getApplicationInfo(
                    mContext.applicationInfo.packageName,
                    0
                )
                "com.android.vending" == mContext.packageManager.getInstallerPackageName(
                    applicationInfo.packageName
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }

        }
    }
}