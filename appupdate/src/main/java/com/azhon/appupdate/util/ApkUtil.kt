package com.azhon.appupdate.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PatternMatcher
import androidx.core.content.FileProvider
import java.io.File


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.util
 * FileName:    ApkUtil
 * CreateDate:  2022/4/7 on 17:02
 * Desc:
 *
 * @author   azhon
 */

class ApkUtil {
    companion object {
        /**
         * install package form file
         */
        fun installApk(context: Context, authorities: String, apk: File) {
            context.startActivity(createInstallIntent(context, authorities, apk))
        }

        fun createInstallIntent(context: Context, authorities: String, apk: File): Intent {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            val uri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, authorities, apk)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                uri = Uri.fromFile(apk)
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            return intent
        }

        fun getVersionCode(context: Context): Long {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                return packageInfo.versionCode.toLong()
            }
        }

        fun deleteOldApk(context: Context, oldApkPath: String): Boolean {
            val curVersionCode = getVersionCode(context)
            try {
                val apk = File(oldApkPath)
                if (apk.exists()) {
                    val oldVersionCode = getVersionCodeByPath(context, oldApkPath)
                    if (curVersionCode > oldVersionCode) {
                        return apk.delete()
                    }
                }
            } catch (e: Exception) {
            }
            return false
        }

        private fun getVersionCodeByPath(context: Context, path: String): Long {
            val packageInfo =
                context.packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode ?: 1
            } else {
                return packageInfo?.versionCode?.toLong() ?: 1
            }
        }
    }
}