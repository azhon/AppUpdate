package com.azhon.appupdate.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.azhon.appupdate.R
import com.azhon.appupdate.base.bean.DownloadStatus
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.manager.HttpDownloadManager
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.FileUtil
import com.azhon.appupdate.util.LogUtil
import com.azhon.appupdate.util.NotificationUtil
import kotlinx.coroutines.*
import java.io.File

/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.service
 * FileName:    DownloadService
 * CreateDate:  2022/4/7 on 11:42
 * Desc:
 *
 * @author azhon
 */

class DownloadService : Service(), OnDownloadListener {
    val scope: CoroutineScope =
        CoroutineScope(Dispatchers.IO) + SupervisorJob() + CoroutineName(Constant.COROUTINE_NAME)

    companion object {
        private const val TAG = "DownloadService"
    }

    private lateinit var manager: DownloadManager
    private var lastProgress = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }
        init()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun init() {
        manager = try {
            DownloadManager.getInstance()
        } catch (e: IllegalArgumentException) {
            LogUtil.e(TAG, "An exception occurred by DownloadManager=null,please check your code!")
            return
        }
        FileUtil.createDirDirectory(manager.config.downloadPath)

        val enable = NotificationUtil.notificationEnable(this@DownloadService)
        LogUtil.d(
            TAG,
            if (enable) "Notification switch status: opened" else " Notification switch status: closed"
        )
        if (checkApkMd5()) {
            LogUtil.d(TAG, "Apk already exist and install it directly.")
            //install apk
            done(File(manager.config.downloadPath, manager.config.apkName))
        } else {
            LogUtil.d(TAG, "Apk don't exist will start download.")
            download()
        }
    }

    /**
     * Check whether the Apk has been downloaded, don't download again
     */
    private fun checkApkMd5(): Boolean {
        if (manager.config.apkMD5.isBlank()) {
            return false
        }
        val file = File(manager.config.downloadPath, manager.config.apkName)
        if (file.exists()) {
            return FileUtil.md5(file).equals(manager.config.apkMD5, ignoreCase = true)
        }
        return false
    }

    @Synchronized
    private fun download() {
        if (manager.downloadState) {
            LogUtil.e(TAG, "Currently downloading, please download again!")
            return
        }
        try {
            manager.config.httpManager = HttpDownloadManager(manager.config.downloadPath)
        } catch (e: IllegalStateException) {
            Log.i(TAG, "download: HttpManager已存在")
        }
        scope.launch {
            withContext(Dispatchers.Main) {
                manager.config.httpManager!!.download(manager.config.apkUrl, manager.config.apkName)
                    .collect {
                        when (it) {
                            is DownloadStatus.Start -> start()
                            is DownloadStatus.Downloading -> downloading(it.max, it.progress)
                            is DownloadStatus.Done -> done(it.apk)
                            is DownloadStatus.Cancel -> this@DownloadService.cancel()
                            is DownloadStatus.Error -> error(it.e)
                        }
                    }
            }
        }
        manager.downloadState = true
    }

    override fun start() {
        LogUtil.i(TAG, "download start")
        if (manager.config.showBgdToast) {
            Toast.makeText(this, R.string.app_update_background_downloading, Toast.LENGTH_SHORT)
                .show()
        }
        if (manager.config.showNotification) {
            NotificationUtil.showNotification(
                this@DownloadService, manager.config.smallIcon,
                resources.getString(R.string.app_update_start_download),
                resources.getString(R.string.app_update_start_download_hint)
            )
        }
        manager.config.onDownloadListeners.forEach { it.start() }
    }

    override fun downloading(max: Int, progress: Int) {
        if (manager.config.showNotification) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            if (curr == lastProgress) return
            LogUtil.i(TAG, "downloading max: $max --- progress: $progress")
            lastProgress = curr
            val content = if (curr < 0) "" else "$curr%"
            NotificationUtil.showProgressNotification(
                this@DownloadService, manager.config.smallIcon,
                resources.getString(R.string.app_update_start_downloading),
                content, if (max == -1) -1 else 100, curr
            )
        }
        manager.config.onDownloadListeners.forEach { it.downloading(max, progress) }
    }

    override fun done(apk: File) {
        LogUtil.d(TAG, "apk downloaded to ${apk.path}")
        manager.downloadState = false
        //If it is android Q (api=29) and above, (showNotification=false) will also send a
        // download completion notification
        if (manager.config.showNotification || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationUtil.showDoneNotification(
                this@DownloadService, manager.config.smallIcon,
                resources.getString(R.string.app_update_download_completed),
                resources.getString(R.string.app_update_click_hint),
                Constant.AUTHORITIES!!, apk
            )
        }
        if (manager.config.jumpInstallPage) {
            ApkUtil.installApk(this@DownloadService, Constant.AUTHORITIES!!, apk)
        }
        manager.config.onDownloadListeners.forEach { it.done(apk) }

        // release objects
        releaseResources()
    }

    override fun cancel() {
        LogUtil.i(TAG, "download cancel")
        manager.downloadState = false
        if (manager.config.showNotification) {
            NotificationUtil.cancelNotification(this@DownloadService)
        }
        manager.config.onDownloadListeners.forEach { it.cancel() }
    }

    override fun error(e: Throwable) {
        LogUtil.e(TAG, "download error: $e")
        manager.downloadState = false
        if (manager.config.showNotification) {
            NotificationUtil.showErrorNotification(
                this@DownloadService, manager.config.smallIcon,
                resources.getString(R.string.app_update_download_error),
                resources.getString(R.string.app_update_continue_downloading),
            )
        }
        manager.config.onDownloadListeners.forEach { it.error(e) }
    }

    private fun releaseResources() {
        manager.release()
        stopSelf()
        scope.cancel("DownloadManager has close")
    }
}