package com.azhon.appupdate.util

import android.content.Context
import java.io.File


/**
 * createDate:  2022/4/7 on 17:02
 * desc:
 *
 * @author azhon
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