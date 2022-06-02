package com.zee.truemuslims.ads.modules.in_app_module

import android.app.Activity
import android.graphics.Color
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import timber.log.Timber


class TrueZInAppUpdate(private var activity: Activity) {
    private val updateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private val updateCode = 123
    fun getInAppUpdate() {
        val task: com.google.android.play.core.tasks.Task<AppUpdateInfo> =
            updateManager.appUpdateInfo
        task.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    updateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        updateCode
                    )
                } catch (e: Exception) {
                    Timber.d("Update Error: " + e.message)
                }

            }
        }
        updateManager.registerListener(listener)
    }

    private val listener = InstallStateUpdatedListener {
        if (it.installStatus() == InstallStatus.DOWNLOADED) {
            popUp()
        }
    }

    private fun popUp() {
        val snackBar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "App Update Almost Done",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Reload") {
            updateManager.completeUpdate()
        }
        snackBar.setTextColor(Color.parseColor("#FF0000"))
        snackBar.show()
    }

}