package com.azhon.appupdate.manager

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.azhon.appupdate.R
import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.LifecycleCallbacksAdapter
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.service.DownloadService
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.LogUtil
import com.azhon.appupdate.util.NotNullSingleVar
import com.azhon.appupdate.util.NullableSingleVar
import com.azhon.appupdate.view.PixelUpdateDialogFragment
import com.azhon.appupdate.view.SimpleUpdateDialog
import com.azhon.appupdate.view.UpdateDialogActivity
import com.azhon.appupdate.view.Win8UpdateDialogFragment
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
class DownloadManager private constructor(
    var config: DownloadConfig,
    internal var application: Application
) : Serializable {

    companion object {
        private const val TAG = "DownloadManager"

        @Volatile
        private var instance: DownloadManager? = null

        fun config(application: Application, block: DownloadConfig.() -> Unit): DownloadManager {
            val config = DownloadConfig(application)
            config.block()
            return getInstance(config, application)
        }

        fun isDownloading(): Boolean {
            return instance?.downloadState ?: false
        }

        fun existInstance(): DownloadManager? = instance

        internal fun getInstance(
            config: DownloadConfig? = null,
            application: Application? = null
        ): DownloadManager {
            if (instance != null && config != null) {
                instance!!.reConfig(config)
            }
            if (instance == null) {
                if (config == null || application == null) throw IllegalArgumentException("config or application is null")
                synchronized(this) {
                    instance ?: DownloadManager(config, application).also { instance = it }
                }
            }
            return instance!!
        }
    }

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
    fun checkThenDownload() {
        if (canDownload()) {
            application.startService(Intent(application, DownloadService::class.java))
        } else {
            Log.e(TAG, "download: cannot download")
        }
    }

    /**
     * with out check whether can download, so you need to check it yourself
     */
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
            return false
        }
        Constant.AUTHORITIES = "${application.packageName}.fileProvider"
        return true
    }

    /**
     * when download not start,HttpManager maybe is null
     * in this case, will exec "then" block
     */
    fun cancel(then: () -> Unit = {}) {
        config.httpManager?.cancel() ?: then()
    }

    /**
     * release objects
     */
    internal fun release() {
        config.httpManager?.release()
        clearListener()
        instance = null
    }

    internal fun reConfig(config: DownloadConfig) {
        this.config.httpManager?.release()
        this.config = config
    }

    fun clearListener() {
        config.onButtonClickListener = null
        config.onDownloadListeners.clear()
    }

    class DownloadConfig constructor(application: Application) {
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
        var downloadPath = ApkUtil.getDefaultCachePath(application)

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
        var viewType: Int = ViewType.Colorful


        fun enableLog(enable: Boolean): DownloadConfig {
            LogUtil.enable(enable)
            return this
        }

        fun registerDownloadListener(onDownloadListener: OnDownloadListener): DownloadConfig {
            this.onDownloadListeners.add(onDownloadListener)
            return this
        }

        fun registerButtonListener(onButtonClickListener: OnButtonClickListener): DownloadConfig {
            this.onButtonClickListener = onButtonClickListener
            return this
        }

        private fun getOneHttpManager(): BaseHttpDownloadManager {
            if (httpManager == null) {
                synchronized(this) {
                    if (httpManager == null) {
                        try {
                            httpManager = HttpDownloadManager(downloadPath)
                        } catch (e: Exception) {
                            Log.e(TAG, "getOneHttpManager: 重复赋值", e)
                        }
                    }
                }
            }
            return httpManager!!
        }

        /**
         * 真正的下载
         * @return Flow<DownloadStatus>
         */
        internal fun download() = getOneHttpManager().download(apkUrl, apkName)

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
         * custom layout id
         */
        var layoutId: Int = -1

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
        val ctx = activity.application
        var config: DownloadManager.DownloadConfig =
            DownloadManager.DownloadConfig(application = ctx)
        var dialogConfig: DownloadManager.DialogConfig = DownloadManager.DialogConfig()

        fun apkUrl(apkUrl: String): Builder {
            config.apkUrl = apkUrl
            return this
        }

        fun downloadPath(path: String): Builder {
            config.downloadPath = path
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

        fun dialogLayout(@LayoutRes layout: Int): Builder {
            dialogConfig.layoutId = layout
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
            return DownloadManager.getInstance(config, application = ctx)
        }
    }

}

fun DownloadManager.download(activity: FragmentActivity) {
    if (canDownload()) {
        showUi(this, activity)
    } else {
        if (config.viewConfig.showNewerToast) {
            Toast.makeText(
                application, R.string.app_update_latest_version, Toast.LENGTH_SHORT
            ).show()
        }
    }
}

/**
 * 这个方法将会忽略配置中的viewType，使用默认UpdateDialogActivity进行下载
 * @receiver DownloadManager
 */
fun DownloadManager.download() {
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
                clearListener()
            }
        }
    })

    if (!canDownload()) {
        if (config.viewConfig.showNewerToast) {
            Toast.makeText(
                application, R.string.app_update_latest_version, Toast.LENGTH_SHORT
            ).show()
        }
    }
}
