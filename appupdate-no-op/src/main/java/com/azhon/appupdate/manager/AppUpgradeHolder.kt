package com.azhon.appupdate.manager

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhon.appupdate.R

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
        it
    }
}

fun AppCompatActivity.downloadApp(block: DownloadManager.Config.() -> Unit): DownloadManager {
    val config = DownloadManager.Config(application)
    config.block()
    return downloadApp(config)
}