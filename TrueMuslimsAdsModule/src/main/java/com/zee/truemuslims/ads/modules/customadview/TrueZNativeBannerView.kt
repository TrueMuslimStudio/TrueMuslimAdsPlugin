package com.zee.truemuslims.ads.modules.customadview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zee.truemuslimsadsmodule.R

import com.zee.truemuslims.ads.modules.hDp
import com.zee.truemuslimsadsmodule.databinding.ZnativeBannerLayoutBinding
import timber.log.Timber


class TrueZNativeBannerView(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private var zLayoutHAdContainerBinding = ZnativeBannerLayoutBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {

        val zTypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.zAdContainerClStyleable, 0, 0
        )
        zLayoutHAdContainerBinding.zShimmerLoader
        zSetBackGroundDrawable(zTypedArray)
    }

    private fun zSetBackGroundDrawable(zTypedArray: TypedArray) {
        zTypedArray.apply {
            val zColor = getColor(
                R.styleable.zAdContainerClStyleable_hBackgroundColor,
                ContextCompat.getColor(context, R.color.white)

            )

            val zCornerRadius = getInt(
                R.styleable.zAdContainerClStyleable_hCornerRadius,
                6
            )
            val zStrokeColor = getColor(
                R.styleable.zAdContainerClStyleable_hStrokeColor,
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )

            val zStrokeWidth = getInt(
                R.styleable.zAdContainerClStyleable_hStrokeWidth,
                1
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

    fun hShowHideAdView(hShowAdView: Boolean) {
        when (hShowAdView) {
            true -> {
                zLayoutHAdContainerBinding.zAdContainer.visibility = View.VISIBLE
                zLayoutHAdContainerBinding.zShimmerLoader.visibility = View.GONE
            }
            false -> {
                zLayoutHAdContainerBinding.zAdContainer.visibility = View.GONE
                zLayoutHAdContainerBinding.zShimmerLoader.visibility = View.VISIBLE
            }
        }
    }

    private fun zSetBackGroundDrawable(
        zBackgroundColor: Int,
        zCornerRadius: Int = 6,
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


