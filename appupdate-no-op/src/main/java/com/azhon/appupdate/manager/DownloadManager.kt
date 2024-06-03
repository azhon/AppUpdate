package com.azhon.appupdate.manager

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
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
class DownloadManager private constructor(val config: DownloadConfig) : Serializable {

    companion object {
        private const val TAG = "DownloadManager"

        @Volatile
        private var instance: DownloadManager? = null

        fun config(application: Application, block: DownloadConfig.() -> Unit): DownloadManager {
            val config = DownloadConfig(application)
            config.block()
            return getInstance(config)
        }

        internal fun getInstance(config: DownloadConfig? = null): DownloadManager {
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
        return true

    }

    /**
     * download file without dialog
     */
    fun checkThenDownload() {

    }

    fun directDownload() {

    }

    private fun checkParams(): Boolean {

        return true
    }

    /**
     * when download not start,HttpManager maybe is null
     * in this case, will exec "then" block
     */
    fun cancel(listener: OnDownloadListener? = null, then: () -> Unit={}) {

    }

    /**
     * release objects
     */
    internal fun release() {
    }

    fun clearListener() {

    }

    class DownloadConfig constructor(var application: Application) {
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
         * 如果不设置此值，将不检查当前应用的versioncode，直接下载安装更新
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
        var downloadPath = ""

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
        var httpManager: BaseHttpDownloadManager?=null

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
        val onDownloadListeners =mutableListOf<OnDownloadListener>()

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
        var notifyId = 1

        /**
         * View type
         */
        var viewType: Int = 1


        fun enableLog(enable: Boolean): DownloadConfig {
            return this
        }

        fun registerDownloadListener(onDownloadListener: OnDownloadListener): DownloadConfig {
            this.onDownloadListeners.add(onDownloadListener)
            return this
        }

        internal var viewConfig: DialogConfig = DialogConfig()

        /**
         * 配置视图
         */
        fun configDialog(block: DialogConfig.() -> Unit): DownloadConfig {
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
    /**
     * 兼容旧方式
     * @property config DownloadConfig
     * @property dialogConfig DialogConfig
     * @constructor
     */
    class Builder constructor(activity: Activity) {
        var config: DownloadManager.DownloadConfig =
            DownloadManager.DownloadConfig(application = activity.application)
        var dialogConfig: DownloadManager.DialogConfig = DownloadManager.DialogConfig()

        fun apkUrl(apkUrl: String): Builder {
            config.apkUrl = apkUrl
            return this
        }

        fun apkName(apkName: String): Builder {
            config.apkName = apkName
            return this
        }

        fun apkVersionCode(apkVersionCode: Int): Builder {
            config.apkVersionCode = apkVersionCode
            return this
        }

        fun apkVersionName(apkVersionName: String): Builder {
            config.apkVersionName = apkVersionName
            return this
        }

        fun showNewerToast(showNewerToast: Boolean): Builder {
            dialogConfig.showNewerToast = showNewerToast
            return this
        }

        fun smallIcon(smallIcon: Int): Builder {
            config.smallIcon = smallIcon
            return this
        }

        fun apkDescription(apkDescription: String): Builder {
            config.apkDescription = apkDescription
            return this
        }

        fun apkSize(apkSize: String): Builder {
            config.apkSize = apkSize
            return this
        }

        fun apkMD5(apkMD5: String): Builder {
            config.apkMD5 = apkMD5
            return this
        }

        fun httpManager(httpManager: BaseHttpDownloadManager): Builder {
            config.httpManager = httpManager
            return this
        }

        fun notificationChannel(notificationChannel: NotificationChannel): Builder {
            config.notificationChannel = notificationChannel
            return this
        }

        fun onButtonClickListener(onButtonClickListener: OnButtonClickListener): Builder {
            config.onButtonClickListener = onButtonClickListener
            return this
        }

        fun onDownloadListener(onDownloadListener: OnDownloadListener): Builder {
            config.onDownloadListeners.add(onDownloadListener)
            return this
        }

        fun showNotification(showNotification: Boolean): Builder {
            config.showNotification = showNotification
            return this
        }

        fun jumpInstallPage(jumpInstallPage: Boolean): Builder {
            config.jumpInstallPage = jumpInstallPage
            return this
        }

        fun showBgdToast(showBgdToast: Boolean): Builder {
            config.showBgdToast = showBgdToast
            return this
        }

        fun forcedUpgrade(forcedUpgrade: Boolean): Builder {
            config.forcedUpgrade = forcedUpgrade
            return this
        }

        fun notifyId(notifyId: Int): Builder {
            config.notifyId = notifyId
            return this
        }

        fun dialogImage(dialogImage: Int): Builder {
            dialogConfig.dialogImage = dialogImage
            return this
        }

        fun dialogButtonColor(dialogButtonColor: Int): Builder {
            dialogConfig.dialogButtonColor = dialogButtonColor
            return this
        }

        fun dialogButtonTextColor(dialogButtonTextColor: Int): Builder {
            dialogConfig.dialogButtonTextColor = dialogButtonTextColor
            return this
        }

        fun dialogProgressBarColor(dialogProgressBarColor: Int): Builder {
            dialogConfig.dialogProgressBarColor = dialogProgressBarColor
            return this
        }

        fun viewType(viewType: Int = ViewType.Colorful) {
            config.viewType = viewType
        }

        fun enableLog(enable: Boolean): Builder {
            config.enableLog(enable)
            return this
        }

        fun build(): DownloadManager {
            config.viewConfig = dialogConfig
            return DownloadManager.getInstance(config)
        }
    }

}