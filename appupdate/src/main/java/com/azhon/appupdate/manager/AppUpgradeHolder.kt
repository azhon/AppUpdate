package com.azhon.appupdate.manager

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhon.appupdate.R
import com.azhon.appupdate.listener.LifecycleCallbacksAdapter
import com.azhon.appupdate.service.DownloadService
import com.azhon.appupdate.view.SimpleUpdateDialog
import com.azhon.appupdate.view.UpdateDialogActivity
import com.azhon.appupdate.view.UpdateDialogFragment
import com.azhon.appupdate.view.Win8UpdateDialogFragment

class ViewType {
    companion object {
        const val None = 0
        const val Colorful = 1
        const val SimpleDialog = 2
        const val Win8 = 3
        const val Pixel = 4
    }
}

fun AppCompatActivity.downloadApp(downloadManager: DownloadManager): DownloadManager {
    return downloadApp(downloadManager.config)
}

fun AppCompatActivity.downloadApp(config: DownloadManager.Config): DownloadManager {
    return DownloadManager.getInstance(config).let {
        if (it.canDownload()) {
            when (config.viewType) {
                ViewType.Colorful -> {
                    application.startActivity(
                        Intent(application, UpdateDialogActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    // Fix memory leak
                    application.registerActivityLifecycleCallbacks(object :
                        LifecycleCallbacksAdapter() {
                        override fun onActivityDestroyed(activity: Activity) {
                            super.onActivityDestroyed(activity)
                            if (this.javaClass.name == activity.javaClass.name) {
                                it.clearListener()
                            }
                        }
                    })
                }
                ViewType.None -> {
                    it.directDownload()
                }
                ViewType.SimpleDialog->{
                    SimpleUpdateDialog.openAlertDialog(this,it)
                }
                ViewType.Pixel->{
                    UpdateDialogFragment.open(this)
                }
                ViewType.Win8->{
                    Win8UpdateDialogFragment.open(this)
                }
            }
            if (it.config.viewConfig.showNewerToast) {
                Toast.makeText(
                    application, R.string.app_update_latest_version, Toast.LENGTH_SHORT
                ).show()
            }
        }
        it
    }
}

fun AppCompatActivity.downloadApp(block: DownloadManager.Config.() -> Unit): DownloadManager {
    val config = DownloadManager.Config(application)
    config.block()
    return downloadApp(config)
}