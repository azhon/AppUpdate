package com.azhon.appupdate.util

import android.content.Context
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
        fun installApk(context: Context, authorities: String, apk: File) {
        }

        fun deleteOldApk(context: Context, oldApkPath: String): Boolean {
            return true
        }
    }
}