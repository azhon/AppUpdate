package com.azhon.appupdate.manager

import android.app.Application
import android.app.NotificationChannel
import android.content.Intent
import android.util.Log
import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.service.DownloadService
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.LogUtil
import com.azhon.appupdate.util.NotNullSingleVar
import com.azhon.appupdate.util.NullableSingleVar
import java.io.Serializable

/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.manager
 * FileName:    DownloadManager
 * CreateDate:  2022/4/7 on 10:36
 * Desc:
 *
 * @author azhon
 */
class DownloadManager private constructor(val config: Config) : Serializable {

    companion object {
        private const val TAG = "DownloadManager"

        @Volatile
        private var instance: DownloadManager? = null

        fun config(application: Application, block: Config.() -> Unit): DownloadManager {
            val config = Config(application)
            config.block()
            return getInstance(config)
        }

        internal fun getInstance(config: Config? = null): DownloadManager {
            if (instance != null && config != null) {
                instance!!.release()
            }
            if (instance == null) {
                if (config == null) throw IllegalArgumentException("config is null")
                synchronized(this) {
                    instance ?: DownloadManager(config).also { instance = it }
                }
            }
            return instance!!
        }
    }

    private var application: Application = config.application
    private var apkVersionCode: Int = config.apkVersionCode

    var downloadState: Boolean = false

    /**
     * test whether can download
     */
    fun canDownload(): Boolean {
        val params = checkParams()
        val versionCodeCheck: Boolean =
            if (apkVersionCode == Int.MIN_VALUE) {
                true
            } else {
                apkVersionCode > ApkUtil.getVersionCode(application)
            }
        return (params && versionCodeCheck)

    }

    /**
     * download file without dialog
     */
    fun download() {
        if (canDownload()) {
            application.startService(Intent(application, DownloadService::class.java))
        } else {
            Log.e(TAG, "download: cannot download")
        }
    }

    fun directDownload() {
        application.startService(Intent(application, DownloadService::class.java))
    }

    private fun checkParams(): Boolean {
        if (config.apkUrl.isEmpty()) {
            LogUtil.e(TAG, "apkUrl can not be empty!")
            return false
        }
        if (config.apkName.isEmpty()) {
            LogUtil.e(TAG, "apkName can not be empty!")
            return false
        }
        if (!config.apkName.endsWith(Constant.APK_SUFFIX)) {
            LogUtil.e(TAG, "apkName must endsWith .apk!")
            return false
        }
        if (config.smallIcon == -1) {
            LogUtil.e(TAG, "smallIcon can not be empty!");
            return false
        }
        if (config.apkDescription.isEmpty()) {
            LogUtil.e(TAG, "apkDescription can not be empty!")
        }
        Constant.AUTHORITIES = "${application.packageName}.fileProvider"
        return true
    }

    /**
     * when download not start,HttpManager maybe is null
     * in this case, will exec "then" block
     */
    fun cancel(listener: OnDownloadListener? = null, then: () -> Unit={}) {
        config.httpManager?.cancel() ?: then()
        listener?.let { config.onDownloadListeners.remove(it) }
    }

    /**
     * release objects
     */
    internal fun release() {
        config.httpManager?.release()
        clearListener()
        instance = null
    }

    fun clearListener() {
        config.onButtonClickListener = null
        config.onDownloadListeners.clear()
    }

    class Config constructor(var application: Application) {
        /**
         * Apk download url
         */
        var apkUrl = ""

        /**
         * Apk file name on disk
         */
        var apkName = ""

        /**
         * The apk versionCode that needs to be downloaded
         */
        var apkVersionCode = Int.MIN_VALUE

        /**
         * The versionName of the dialog reality
         */
        var apkVersionName = ""

        /**
         * The file path where the Apk is saved
         * eg: /storage/emulated/0/Android/data/ your packageName /cache
         */
        var downloadPath = application.externalCacheDir?.path ?: String.format(
            Constant.APK_PATH,
            application.packageName
        )

        /**
         * Notification icon resource
         */
        var smallIcon = -1

        /**
         * New version description information
         */
        var apkDescription = ""

        /**
         * Apk Size,Unit MB
         */
        var apkSize = ""

        /**
         * Apk md5 file verification(32-bit) verification repeated download
         */
        var apkMD5 = ""

        /**
         * Apk download manager
         */
        var httpManager: BaseHttpDownloadManager? by NullableSingleVar()

        /**
         * The following are unimportant filed
         */

        /**
         * adapter above Android O notification
         */
        var notificationChannel: NotificationChannel? = null

        /**
         * download listeners
         */
        val onDownloadListeners: MutableList<OnDownloadListener> by NotNullSingleVar(mutableListOf())

        /**
         * dialog button click listener
         */
        var onButtonClickListener: OnButtonClickListener? = null

        /**
         * Whether to show the progress of the notification
         */
        var showNotification = true

        /**
         * Whether the installation page will pop up automatically after the download is complete
         */
        var jumpInstallPage = true

        /**
         * Does the download start tip "Downloading a new version in the background..."
         */
        var showBgdToast = true

        /**
         * Whether to force an upgrade
         */
        var forcedUpgrade = false

        /**
         * Notification id
         */
        var notifyId = Constant.DEFAULT_NOTIFY_ID

        /**
         * View type
         */
        var viewType: Int = ViewType.None


        fun enableLog(enable: Boolean): Config {
            LogUtil.enable(enable)
            return this
        }

        fun registerDownloadListener(onDownloadListener: OnDownloadListener): Config {
            this.onDownloadListeners.add(onDownloadListener)
            return this
        }

        internal val viewConfig: DialogConfig = DialogConfig()

        /**
         * 配置视图
         */
        fun configDialog(block: DialogConfig.() -> Unit): Config {
            viewConfig.block()
            return this
        }
    }

    class DialogConfig() {
        /**
         * whether to tip to user "Currently the latest version!"
         */
        var showNewerToast = false

        /**
         * dialog background Image resource
         */
        var dialogImage = -1

        /**
         * dialog button background color
         */
        var dialogButtonColor = -1

        /**
         * dialog button text color
         */
        var dialogButtonTextColor = -1

        /**
         * dialog progress bar color and progress-text color
         */
        var dialogProgressBarColor = -1
    }

}