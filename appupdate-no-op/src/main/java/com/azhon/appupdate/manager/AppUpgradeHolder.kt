package com.azhon.appupdate.manager

import androidx.fragment.app.FragmentActivity

class ViewType {
    companion object {
        const val None = 0
        const val Colorful = 1
        const val SimpleDialog = 2
        const val Win8 = 3
        const val Pixel = 4
    }
}
fun DownloadManager.download(activity: FragmentActivity) {

}

fun FragmentActivity.downloadApp(downloadManager: DownloadManager): DownloadManager {
    return downloadApp(downloadManager.config)
}

fun FragmentActivity.downloadApp(config: DownloadManager.DownloadConfig): DownloadManager {
    return DownloadManager.getInstance(config)
}

fun FragmentActivity.downloadApp(block: DownloadManager.DownloadConfig.() -> Unit): DownloadManager {
    val config = DownloadManager.DownloadConfig(application)
    config.block()
    return downloadApp(config)
}