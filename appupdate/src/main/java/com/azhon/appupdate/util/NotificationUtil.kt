package com.azhon.appupdate.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.service.DownloadService
import java.io.File

/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.util
 * FileName:    NotificationUtil
 * CreateDate:  2022/4/7 on 13:36
 * Desc:
 *
 * @author azhon
 */
class NotificationUtil {
    companion object {

        fun notificationEnable(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun getNotificationChannelId(): String {
            val channel = DownloadManager.getInstance()?.notificationChannel
            return if (channel == null) {
                Constant.DEFAULT_CHANNEL_ID
            } else {
                channel.id
            }
        }

        private fun builderNotification(
            context: Context, icon: Int, title: String, content: String
        ): NotificationCompat.Builder {
            var channelId = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId = getNotificationChannelId()
            }
            return NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setContentText(content)
                .setAutoCancel(false)
                .setOngoing(true)
        }

        fun showNotification(context: Context, icon: Int, title: String, content: String) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                afterO(manager)
            }
            val notify = builderNotification(context, icon, title, content)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build()
            manager.notify(
                DownloadManager.getInstance()?.notifyId ?: Constant.DEFAULT_NOTIFY_ID,
                notify
            )
        }

        /**
         * send a downloading Notification
         */
        fun showProgressNotification(
            context: Context, icon: Int, title: String, content: String, max: Int, progress: Int
        ) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notify = builderNotification(context, icon, title, content)
                .setProgress(max, progress, max == -1).build()
            manager.notify(
                DownloadManager.getInstance()?.notifyId ?: Constant.DEFAULT_NOTIFY_ID,
                notify
            )
        }

        /**
         * send a downloaded Notification
         */
        fun showDoneNotification(
            context: Context, icon: Int, title: String, content: String,
            authorities: String, apk: File
        ) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                afterO(manager)
            }
            manager.cancel(DownloadManager.getInstance()?.notifyId ?: Constant.DEFAULT_NOTIFY_ID)
            val intent = ApkUtil.createInstallIntent(context, authorities, apk)
            val pi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }
            val notify = builderNotification(context, icon, title, content)
                .setContentIntent(pi)
                .build()
            notify.flags = notify.flags or Notification.FLAG_AUTO_CANCEL
            manager.notify(
                DownloadManager.getInstance()?.notifyId ?: Constant.DEFAULT_NOTIFY_ID,
                notify
            )
        }

        /**
         * send a error Notification
         */
        fun showErrorNotification(
            context: Context, icon: Int, title: String, content: String
        ) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                afterO(manager)
            }
            val intent = Intent(context, DownloadService::class.java)
            val pi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }
            val notify = builderNotification(context, icon, title, content)
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build()
            manager.notify(
                DownloadManager.getInstance()?.notifyId ?: Constant.DEFAULT_NOTIFY_ID,
                notify
            )
        }

        /**
         * cancel Notification by id
         */
        fun cancelNotification(context: Context) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(DownloadManager.getInstance()?.notifyId ?: Constant.DEFAULT_NOTIFY_ID)

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private fun afterO(manager: NotificationManager) {
            var channel = DownloadManager.getInstance()?.notificationChannel
            if (channel == null) {
                channel = NotificationChannel(
                    Constant.DEFAULT_CHANNEL_ID, Constant.DEFAULT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    enableLights(true)
                    setShowBadge(true)
                }
            }
            manager.createNotificationChannel(channel)
        }
    }
}