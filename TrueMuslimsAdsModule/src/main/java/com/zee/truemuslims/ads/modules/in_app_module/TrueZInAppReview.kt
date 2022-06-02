package com.zee.truemuslims.ads.modules.in_app_module

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task

class TrueZInAppReview(var context: Activity) {
    fun zShowRatingDialogue() {
        val reviewManager = context.let { ReviewManagerFactory.create(it) }
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow: Task<Void> =
                    reviewManager.launchReviewFlow(context, reviewInfo)
                        .addOnFailureListener {
                        }.addOnCompleteListener {

                        }
                flow.addOnCompleteListener {
                }
            } else {
                showRateAppFallbackDialog(context)
            }
        }.addOnFailureListener {

        }
    }

    private fun showRateAppFallbackDialog(context: Activity) {
        this.let {
            MaterialAlertDialogBuilder(context)
                .setTitle("Rate App")
                .setMessage("If you are enjoying our app,please take a moment to rate it on playStore")
                .setPositiveButton("Rate Now") { _, _ -> }
                .setNegativeButton(
                    "No,Thanks"
                ) { _, _ -> }
                .setNeutralButton(
                    "Remind me later"
                ) { _, _ -> }
                .setOnDismissListener { }
                .show()
        }
    }
}