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

import com.facebook.shimmer.ShimmerFrameLayout
import com.zee.truemuslims.ads.modules.R
import com.zee.truemuslims.ads.modules.databinding.ZnativeAdvancedLayoutBinding

import com.zee.truemuslims.ads.modules.hDp

import timber.log.Timber

class TrueZNativeAdvancedView(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private val Z_SHOW_SHIMMER = -1234

    private var zLayoutZAdContainerBinding = ZnativeAdvancedLayoutBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {

        val zTypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.zAdContainerClStyleable, 0, 0
        )
        zSetBackGroundDrawable(zTypedArray)
    }

    private fun zSetBackGroundDrawable(zTypedArray: TypedArray) {
        zTypedArray.apply {
            val zColor = getColor(
                R.styleable.zAdContainerClStyleable_zBackgroundColor,
                ContextCompat.getColor(context, R.color.white)
            )

            val zCornerRadius = getInt(
                R.styleable.zAdContainerClStyleable_zCornerRadius,
                0
            )
            val zStrokeColor = getColor(
                R.styleable.zAdContainerClStyleable_zStrokeColor,
                ContextCompat.getColor(context, R.color.gnt_ad_bg_gray)
            )

            val zStrokeWidth = getInt(
                R.styleable.zAdContainerClStyleable_zStrokeWidth,
                2
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
            zLayoutZAdContainerBinding.hLoaderContainer.also {
                it.removeAllViews()
                it.addView(zFindLoaderView(this@apply))
            }
        }
    }

    private fun zFindLoaderView(typedArray: TypedArray): View {

        val zLoaderContainer = typedArray.getResourceId(
            R.styleable.zAdContainerClStyleable_zLoaderContainer,
            Z_SHOW_SHIMMER
        )
        when (zLoaderContainer) {
            Z_SHOW_SHIMMER -> {
                return ShimmerFrameLayout(context).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        hDp(context, 270).toInt()
                    )
                    LayoutInflater.from(this.context).also {
                        it.inflate(R.layout.shimmer_native_advanced_layout, this)
                    }
                }
            }
            else -> {
                return ConstraintLayout(context).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        hDp(context, 270).toInt()
                    )
                    setPadding(4, 4, 4, 4)
                    LayoutInflater.from(this.context).also {
                        it.inflate(zLoaderContainer, this)
                    }
                }
            }
        }

    }

    fun zShowHideAdLoader(hShowLoader: Boolean) {
        when (hShowLoader) {
            true -> {
                zLayoutZAdContainerBinding.hLoaderContainer.visibility = View.VISIBLE
                zLayoutZAdContainerBinding.zAdContainer.visibility = View.GONE
            }
            false -> {
                zLayoutZAdContainerBinding.hLoaderContainer.visibility = View.GONE
                zLayoutZAdContainerBinding.zAdContainer.visibility = View.VISIBLE
            }
        }

    }

    fun zShowHideAdView(hShowAdView: Boolean) {
        when (hShowAdView) {
            true -> {
                zLayoutZAdContainerBinding.zAdContainer.visibility = View.VISIBLE
                zLayoutZAdContainerBinding.hLoaderContainer.visibility = View.GONE
            }
            false -> {
                zLayoutZAdContainerBinding.zAdContainer.visibility = View.GONE
                zLayoutZAdContainerBinding.hLoaderContainer.visibility = View.VISIBLE
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
            zLayoutZAdContainerBinding.hAdRootCL.background = zGradientDrawable
        }
    }

    fun zShowAdView(
        viewGroup: ViewGroup? = null,
        view: View? = null,
    ) {
        try {
            viewGroup?.let {
                zLayoutZAdContainerBinding.zAdContainer.apply {
                    removeAllViews()
                    addView(it)
                }
                zShowHideAdLoader(hShowLoader = false)
            }
            view?.let {
                zLayoutZAdContainerBinding.zAdContainer.apply {
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