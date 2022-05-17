package com.zee.truemuslims.ads.modules.fallbackstrategies

import com.zee.truemuslims.ads.modules.types.TrueAdPriorityType
import com.zee.truemuslims.ads.modules.types.TrueAdsType

interface TrueStrategy {
    fun zBannerStrategy(
        zGlobalPriority: TrueAdPriorityType,
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType

    fun zNativeAdvancedStrategy(
        zGlobalPriority: TrueAdPriorityType,
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType

    fun zInterstitialStrategy(
        zGlobalPriority: TrueAdPriorityType,
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType

    fun zNativeBannerStrategy(
        zGlobalPriority: TrueAdPriorityType,
        zAdsType: TrueAdsType,
    ): TrueAdPriorityType
}