package com.azhon.appupdate.util

import android.util.Log
import com.azhon.appupdate.config.Constant


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.util
 * FileName:    LogUtil
 * CreateDate:  2022/4/7 on 11:23
 * Desc:
 *
 * @author azhon
 */

class LogUtil {

    companion object {
        var b = true

        fun enable(enable: Boolean) {
            b = enable
        }

        fun e(tag: String, msg: String) {
            if (b) Log.e(Constant.TAG + tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (b) Log.d(Constant.TAG + tag, msg)
        }

        fun i(tag: String, msg: String) {
            if (b) Log.i(Constant.TAG + tag, msg)
        }

    }
}