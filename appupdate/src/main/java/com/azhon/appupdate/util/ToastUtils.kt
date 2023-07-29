package com.azhon.appupdate.util

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun showLong(context: Context, s: String) {
        Toast.makeText(context.applicationContext, s, Toast.LENGTH_LONG).show()
    }

    fun showShot(context: Context, s: String) {
        Toast.makeText(context.applicationContext, s, Toast.LENGTH_SHORT).show()
    }
}