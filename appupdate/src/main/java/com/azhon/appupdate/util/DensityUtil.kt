package com.azhon.appupdate.util

import android.content.Context


/**
 * createDate:  2022/4/7 on 17:52
 * desc:
 *
 * @author azhon
 */

class DensityUtil {
    companion object {
        fun dip2px(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }
    }
}