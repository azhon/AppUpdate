package com.azhon.appupdate.manager

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.content.Intent
import android.widget.Toast
import com.azhon.appupdate.R
import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.LifecycleCallbacksAdapter
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.service.DownloadService
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.LogUtil
import com.azhon.appupdate.view.UpdateDialogActivity
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
class DownloadManager private constructor(builder: Builder) : Serializable {

    companion object {
        private const val TAG = "DownloadManager"
        private var instance: DownloadManager? = null

        internal fun getInstance(builder: Builder? = null): DownloadManager? {
            if (instance != null && builder != null) {
                instance!!.release()
            }
            if (instance == null) {
                if (builder == null) return null
                instance = DownloadManager(builder)
            }
            return instance!!
        }
    }

    private var application: Application = builder.application
    private var apkVersionCode: Int
    private var showNewerToast: Boolean
    internal var contextClsName: String = builder.contextClsName
    internal var apkUrl: String
    internal var apkName: String
    internal var apkVersionName: String
    internal var downloadPath: String
    internal var smallIcon: Int
    internal var apkDescription: String
    internal var apkSize: String
    internal var apkMD5: String
    internal var httpManager: BaseHttpDownloadManager?
    internal var notificationChannel: NotificationChannel?
    internal var onDownloadListeners: MutableList<OnDownloadListener>
    internal var onButtonClickListener: OnButtonClickListener?
    internal var showNotification: Boolean
    internal var jumpInstallPage: Boolean
    internal var showBgdToast: Boolean
    internal var forcedUpgrade: Boolean
    internal var notifyId: Int
    internal var dialogImage: Int
    internal var dialogButtonColor: Int
    internal var dialogButtonTextColor: Int
    internal var dialogProgressBarColor: Int
    var downloadState: Boolean = false


    init {
        apkUrl = builder.apkUrl
        apkName = builder.apkName
        apkVersionCode = builder.apkVersionCode
        apkVersionName = builder.apkVersionName
        downloadPath =
            builder.downloadPath ?: String.format(Constant.APK_PATH, application.packageName)
        showNewerToast = builder.showNewerToast
        smallIcon = builder.smallIcon
        apkDescription = builder.apkDescription
        apkSize = builder.apkSize
        apkMD5 = builder.apkMD5
        httpManager = builder.httpManager
        notificationChannel = builder.notificationChannel
        onDownloadListeners = builder.onDownloadListeners
        onButtonClickListener = builder.onButtonClickListener
        showNotification = builder.showNotification
        jumpInstallPage = builder.jumpInstallPage
        showBgdToast = builder.showBgdToast
        forcedUpgrade = builder.forcedUpgrade
        notifyId = builder.notifyId
        dialogImage = builder.dialogImage
        dialogButtonColor = builder.dialogButtonColor
        dialogButtonTextColor = builder.dialogButtonTextColor
        dialogProgressBarColor = builder.dialogProgressBarColor
        // Fix memory leak
        application.registerActivityLifecycleCallbacks(object : LifecycleCallbacksAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                if (contextClsName == activity.javaClass.name) {
                    clearListener()
                }
            }
        })
    }

    /**
     * Start download
     */
    fun download() {
        if (!checkParams()) {
            return
        }
        if (checkVersionCode()) {
            application.startService(Intent(application, DownloadService::class.java))
        } else {
            if (apkVersionCode > ApkUtil.getVersionCode(application)) {
                application.startActivity(
                    Intent(application, UpdateDialogActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                if (showNewerToast) {
                    Toast.makeText(
                        application, R.string.app_update_latest_version, Toast.LENGTH_SHORT
                    ).show()
                }
                LogUtil.d(TAG, application.resources.getString(R.string.app_update_latest_version))
            }
        }

    }

    private fun checkParams(): Boolean {
        if (apkUrl.isEmpty()) {
            LogUtil.e(TAG, "apkUrl can not be empty!")
            return false
        }
        if (apkName.isEmpty()) {
            LogUtil.e(TAG, "apkName can not be empty!")
            return false
        }
        if (!apkName.endsWith(Constant.APK_SUFFIX)) {
            LogUtil.e(TAG, "apkName must endsWith .apk!")
            return false
        }
        if (smallIcon == -1) {
            LogUtil.e(TAG, "smallIcon can not be empty!");
            return false
        }
        Constant.AUTHORITIES = "${application.packageName}.fileProvider"
        return true
    }

    /**
     * Check the set apkVersionCode if it is not the default then use the built-in dialog
     * If it is the default value Int.MIN_VALUE, directly start the service download
     */
    private fun checkVersionCode(): Boolean {
        if (apkVersionCode == Int.MIN_VALUE) {
            return true
        }
        if (apkDescription.isEmpty()) {
            LogUtil.e(TAG, "apkDescription can not be empty!")
        }
        return false
    }

    fun cancel() {
        httpManager?.cancel()
    }

    /**
     * release objects
     */
    internal fun release() {
        httpManager?.release()
        clearListener()
        instance = null
    }

    private fun clearListener() {
        onButtonClickListener = null
        onDownloadListeners.clear()
    }

    class Builder constructor(activity: Activity) {

        /**
         * library context
         */
        internal var application: Application = activity.application

        /**
         * Fix the memory leak caused by Activity destroy
         */
        internal var contextClsName: String = activity.javaClass.name

        /**
         * Apk download url
         */
        internal var apkUrl = ""

        /**
         * Apk file name on disk
         */
        internal var apkName = ""

        /**
         * The apk versionCode that needs to be downloaded
         */
        internal var apkVersionCode = Int.MIN_VALUE

        /**
         * The versionName of the dialog reality
         */
        internal var apkVersionName = ""

        /**
         * The file path where the Apk is saved
         * eg: /storage/emulated/0/Android/data/ your packageName /cache
         */
        internal var downloadPath = application.externalCacheDir?.path

        /**
         * whether to tip to user "Currently the latest version!"
         */
        internal var showNewerToast = false

        /**
         * Notification icon resource
         */
        internal var smallIcon = -1

        /**
         * New version description information
         */
        internal var apkDescription = ""

        /**
         * Apk Size,Unit MB
         */
        internal var apkSize = ""

        /**
         * Apk md5 file verification(32-bit) verification repeated download
         */
        internal var apkMD5 = ""

        /**
         * Apk download manager
         */
        internal var httpManager: BaseHttpDownloadManager? = null

        /**
         * The following are unimportant filed
         */

        /**
         * adapter above Android O notification
         */
        internal var notificationChannel: NotificationChannel? = null

        /**
         * download listeners
         */
        internal var onDownloadListeners = mutableListOf<OnDownloadListener>()

        /**
         * dialog button click listener
         */
        internal var onButtonClickListener: OnButtonClickListener? = null

        /**
         * Whether to show the progress of the notification
         */
        internal var showNotification = true

        /**
         * Whether the installation page will pop up automatically after the download is complete
         */
        internal var jumpInstallPage = true

        /**
         * Does the download start tip "Downloading a new version in the background..."
         */
        internal var showBgdToast = true

        /**
         * Whether to force an upgrade
         */
        internal var forcedUpgrade = false

        /**
         * Notification id
         */
        internal var notifyId = Constant.DEFAULT_NOTIFY_ID

        /**
         * dialog background Image resource
         */
        internal var dialogImage = -1

        /**
         * dialog button background color
         */
        internal var dialogButtonColor = -1

        /**
         * dialog button text color
         */
        internal var dialogButtonTextColor = -1

        /**
         * dialog progress bar color and progress-text color
         */
        internal var dialogProgressBarColor = -1


        fun apkUrl(apkUrl: String): Builder {
            this.apkUrl = apkUrl
            return this
        }

        fun apkName(apkName: String): Builder {
            this.apkName = apkName
            return this
        }

        fun apkVersionCode(apkVersionCode: Int): Builder {
            this.apkVersionCode = apkVersionCode
            return this
        }

        fun apkVersionName(apkVersionName: String): Builder {
            this.apkVersionName = apkVersionName
            return this
        }

        fun showNewerToast(showNewerToast: Boolean): Builder {
            this.showNewerToast = showNewerToast
            return this
        }

        fun smallIcon(smallIcon: Int): Builder {
            this.smallIcon = smallIcon
            return this
        }

        fun apkDescription(apkDescription: String): Builder {
            this.apkDescription = apkDescription
            return this
        }

        fun apkSize(apkSize: String): Builder {
            this.apkSize = apkSize
            return this
        }

        fun apkMD5(apkMD5: String): Builder {
            this.apkMD5 = apkMD5
            return this
        }

        fun httpManager(httpManager: BaseHttpDownloadManager): Builder {
            this.httpManager = httpManager
            return this
        }

        fun notificationChannel(notificationChannel: NotificationChannel): Builder {
            this.notificationChannel = notificationChannel
            return this
        }

        fun onButtonClickListener(onButtonClickListener: OnButtonClickListener): Builder {
            this.onButtonClickListener = onButtonClickListener
            return this
        }

        fun onDownloadListener(onDownloadListener: OnDownloadListener): Builder {
            this.onDownloadListeners.add(onDownloadListener)
            return this
        }

        fun showNotification(showNotification: Boolean): Builder {
            this.showNotification = showNotification
            return this
        }

        fun jumpInstallPage(jumpInstallPage: Boolean): Builder {
            this.jumpInstallPage = jumpInstallPage
            return this
        }

        fun showBgdToast(showBgdToast: Boolean): Builder {
            this.showBgdToast = showBgdToast
            return this
        }

        fun forcedUpgrade(forcedUpgrade: Boolean): Builder {
            this.forcedUpgrade = forcedUpgrade
            return this
        }

        fun notifyId(notifyId: Int): Builder {
            this.notifyId = notifyId
            return this
        }

        fun dialogImage(dialogImage: Int): Builder {
            this.dialogImage = dialogImage
            return this
        }

        fun dialogButtonColor(dialogButtonColor: Int): Builder {
            this.dialogButtonColor = dialogButtonColor
            return this
        }

        fun dialogButtonTextColor(dialogButtonTextColor: Int): Builder {
            this.dialogButtonTextColor = dialogButtonTextColor
            return this
        }

        fun dialogProgressBarColor(dialogProgressBarColor: Int): Builder {
            this.dialogProgressBarColor = dialogProgressBarColor
            return this
        }

        fun enableLog(enable: Boolean): Builder {
            LogUtil.enable(enable)
            return this
        }

        fun build(): DownloadManager {
            return getInstance(this)!!
        }
    }
}