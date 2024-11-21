package com.azhon.appupdate.manager

import android.app.Activity
import android.app.NotificationChannel
import com.azhon.appupdate.base.BaseHttpDownloadManager
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
import java.io.Serializable

/**
 * createDate:  2022/4/7 on 10:36
 * desc:
 *
 * @author azhon
 */
class DownloadManager private constructor(builder: Builder) : Serializable {

    companion object {
        private var instance: DownloadManager? = null

        fun getInstance(builder: Builder? = null): DownloadManager? {
            if (instance == null) {
                if (builder == null) return null
                instance = DownloadManager(builder)
            }
            return instance!!
        }
    }


    /**
     * Start download
     */
    fun download() {
    }

    fun cancel() {
    }

    class Builder constructor(activity: Activity) {

        fun apkUrl(apkUrl: String): Builder {
            return this
        }

        fun apkName(apkName: String): Builder {
            return this
        }

        fun apkVersionCode(apkVersionCode: Int): Builder {
            return this
        }

        fun apkVersionName(apkVersionName: String): Builder {
            return this
        }

        fun showNewerToast(showNewerToast: Boolean): Builder {
            return this
        }

        fun smallIcon(smallIcon: Int): Builder {
            return this
        }

        fun apkDescription(apkDescription: String): Builder {
            return this
        }

        fun apkSize(apkSize: String): Builder {
            return this
        }

        fun apkMD5(apkMD5: String): Builder {
            return this
        }

        fun httpManager(httpManager: BaseHttpDownloadManager): Builder {
            return this
        }

        fun notificationChannel(notificationChannel: NotificationChannel): Builder {
            return this
        }

        fun onButtonClickListener(onButtonClickListener: OnButtonClickListener): Builder {
            return this
        }

        fun onDownloadListener(onDownloadListener: OnDownloadListener): Builder {
            return this
        }

        fun showNotification(showNotification: Boolean): Builder {
            return this
        }

        fun jumpInstallPage(jumpInstallPage: Boolean): Builder {
            return this
        }

        fun showBgdToast(showBgdToast: Boolean): Builder {
            return this
        }

        fun forcedUpgrade(forcedUpgrade: Boolean): Builder {
            return this
        }

        fun notifyId(notifyId: Int): Builder {
            return this
        }

        fun dialogImage(dialogImage: Int): Builder {
            return this
        }

        fun dialogButtonColor(dialogButtonColor: Int): Builder {
            return this
        }

        fun dialogButtonTextColor(dialogButtonTextColor: Int): Builder {
            return this
        }

        fun dialogProgressBarColor(dialogProgressBarColor: Int): Builder {
            return this
        }

        fun enableLog(enable: Boolean): Builder {
            return this
        }

        fun build(): DownloadManager {
            return getInstance(this)!!
        }
    }
}