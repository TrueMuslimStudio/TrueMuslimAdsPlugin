package com.zee.truemuslims.ads.modules.templates

import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable

class TrueBannerTemplateStyle {
    /**The background color for the bulk of the ad.*/
    var mainBackgroundColor: ColorDrawable? = null
        private set

    /** A class that provides helper methods to build a style object. *  */
    class Builder {
        private val styles: TrueBannerTemplateStyle = TrueBannerTemplateStyle()

        fun withMainBackgroundColor(mainBackgroundColor: ColorDrawable?): Builder {
            styles.mainBackgroundColor = mainBackgroundColor
            return this
        }

        fun build(): TrueBannerTemplateStyle {
            return styles
        }

    }
}