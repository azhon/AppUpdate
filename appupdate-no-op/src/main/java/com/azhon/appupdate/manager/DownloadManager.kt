package com.azhon.appupdate.manager

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
        return true

    }

    /**
     * download file without dialog
     */
    fun download() {

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


        fun enableLog(enable: Boolean): Config {
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