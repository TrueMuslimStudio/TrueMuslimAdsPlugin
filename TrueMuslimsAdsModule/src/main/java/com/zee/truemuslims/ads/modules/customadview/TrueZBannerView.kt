package com.zee.truemuslims.ads.modules.customadview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zee.truemuslims.ads.modules.R
import com.zee.truemuslims.ads.modules.TrueConstants
import com.zee.truemuslims.ads.modules.databinding.ZbannerAdLayoutBinding


import com.zee.truemuslims.ads.modules.hDp

import timber.log.Timber


class TrueZBannerView(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private var zLayoutHAdContainerBinding = ZbannerAdLayoutBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {

        val zTypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.zAdBannerContainerClStyleable, 0, 0
        )
        zLayoutHAdContainerBinding.zShimmerLoader
        zSetBackGroundDrawable(zTypedArray)
    }

    private fun zSetBackGroundDrawable(zTypedArray: TypedArray) {
        zTypedArray.apply {
            val zColor = getColor(
                R.styleable.zAdBannerContainerClStyleable_zBannerBackgroundColor,
                ContextCompat.getColor(context, R.color.white)
            )

            val zCornerRadius = getInt(
                R.styleable.zAdBannerContainerClStyleable_zBannerLoaderContainer,
                0
            )
            val zStrokeColor = getColor(
                R.styleable.zAdBannerContainerClStyleable_zBannerStrokeColor,
                ContextCompat.getColor(context, R.color.teal_200)
            )

            val zStrokeWidth = getInt(
                R.styleable.zAdBannerContainerClStyleable_zBannerStrokeWidth,
                0
            )

            zSetBackGroundDrawable(
                zBackgroundColor = zColor,
                zCornerRadius = zCornerRadius,
                TrueStroke(
                    zContext = context,
                    zColor = zStrokeColor,
                    zWidth = zStrokeWidth,
                )
            )
        }

    }

    fun zShowHideAdLoader(hShowLoader: Boolean) {
        if (!TrueConstants.isNetworkAvailable(context)) {
            zLayoutHAdContainerBinding.zShimmerLoader.visibility = View.GONE
            zLayoutHAdContainerBinding.zAdContainer.visibility = View.GONE
        } else {
            when (hShowLoader) {
                true -> {
                    zLayoutHAdContainerBinding.zShimmerLoader.visibility = View.VISIBLE
                    zLayoutHAdContainerBinding.zAdContainer.visibility = View.GONE
                }
                false -> {
                    zLayoutHAdContainerBinding.zShimmerLoader.visibility = View.GONE
                    zLayoutHAdContainerBinding.zAdContainer.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun zSetBackGroundDrawable(
        zBackgroundColor: Int,
        zCornerRadius: Int = 0,
        zStroke: TrueStroke = TrueStroke(context)
    ) {
        val zGradientDrawable = GradientDrawable()
        zGradientDrawable.apply {
            setColor(zBackgroundColor)
            cornerRadius = hDp(context, zCornerRadius)
            setStroke(
                zStroke.zWidth,
                zStroke.zColor
            )
            zLayoutHAdContainerBinding.hAdRootCL.background = zGradientDrawable
        }
    }

    fun zShowAdView(
        viewGroup: ViewGroup? = null,
        view: View? = null,
    ) {
        try {
            viewGroup?.let {
                zLayoutHAdContainerBinding.zAdContainer.apply {
                    removeAllViews()
                    addView(it)
                }
                zShowHideAdLoader(hShowLoader = false)
            }
            view?.let {
                zLayoutHAdContainerBinding.zAdContainer.apply {
                    removeAllViews()
                    addView(it)
                }
                zShowHideAdLoader(hShowLoader = false)
            }

        } catch (e: Exception) {
            Timber.d("Exception is ${e.message}")
        }

    }
}


