package com.azhon.appupdate.manager

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.azhon.appupdate.R
import com.azhon.appupdate.listener.LifecycleCallbacksAdapter
import com.azhon.appupdate.view.SimpleUpdateDialog
import com.azhon.appupdate.view.UpdateDialogActivity
import com.azhon.appupdate.view.PixelUpdateDialogFragment
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

fun FragmentActivity.downloadApp(downloadManager: DownloadManager): DownloadManager {
    return downloadApp(downloadManager.config)
}

fun FragmentActivity.downloadApp(config: DownloadManager.DownloadConfig): DownloadManager {
    return DownloadManager.getInstance(config, application).let {
        if (it.canDownload()) {
            showUi(it, this)
        } else {
            if (it.config.viewConfig.showNewerToast) {
                Toast.makeText(
                    application, R.string.app_update_latest_version, Toast.LENGTH_SHORT
                ).show()
            }
        }
        it
    }
}

fun FragmentActivity.downloadApp(block: DownloadManager.DownloadConfig.() -> Unit): DownloadManager {
    val config = DownloadManager.DownloadConfig(application)
    config.block()
    return downloadApp(config)
}


fun showUi(downloadManager: DownloadManager, activity: FragmentActivity) {
    val application = activity.application

    when (downloadManager.config.viewType) {
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
                        downloadManager.clearListener()
                    }
                }
            })
        }

        ViewType.None -> {
            //自定义界面，自行决定何时下载，此处不做额外处理
            //it.startService2Download()
        }

        ViewType.SimpleDialog -> {
            SimpleUpdateDialog.openAlertDialog(activity, downloadManager)
        }

        ViewType.Pixel -> {
            PixelUpdateDialogFragment.open(activity)
        }

        ViewType.Win8 -> {
            Win8UpdateDialogFragment.open(activity)
        }
    }

}