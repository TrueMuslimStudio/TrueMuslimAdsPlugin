package com.zee.truemuslims.ads.modules.callbacks

import android.app.Activity
import com.zee.truemuslims.ads.modules.TrueError
import com.zee.truemuslims.ads.modules.types.TrueAdsType

abstract class TrueInterCallbacks {

    open fun zOnAdFailedToLoad(
        zAdType: TrueAdsType,
        zError: TrueError,
        zActivity: Activity? = null
    ) {

    }

    open fun zOnAddLoaded(
        zAdType: TrueAdsType
    ) {
    }

    open fun zOnAdFailedToShowFullContent(
        zAdType: TrueAdsType,
        zError: TrueError
    ) {
    }

    open fun zOnAddShowed(
        zAdType: TrueAdsType
    ) {
    }

    open fun zOnAddDismissed(
        zAdType: TrueAdsType
    ) {
    }

    open fun zOnAdTimedOut(
        zAdType: TrueAdsType
    ) {

    }
}