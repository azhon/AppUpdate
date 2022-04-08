package com.azhon.appupdate.util

import android.content.Context


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.util
 * FileName:    DensityUtil
 * CreateDate:  2022/4/7 on 17:52
 * Desc:
 *
 * @author   azhon
 */

class DensityUtil {
    companion object {
        fun dip2px(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }
    }
}