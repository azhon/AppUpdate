package com.azhon.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.util.ApkUtil

/**
 * createDate: 2022/04/08 on 14:52
 * desc:
 *
 * @author azhon
 */

class MainActivity : AppCompatActivity(), View.OnClickListener, OnButtonClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val url = "https://downv6.qq.com/qqweb/QQ_1/android_apk/Android_9.0.81_64.apk"
    private val apkName = "appupdate.apk"
    private var manager: DownloadManager? = null
    private lateinit var tvPercent: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_title)
        progressBar = findViewById(R.id.number_progress_bar)
        tvPercent = findViewById<Button>(R.id.tv_percent)
        findViewById<TextView>(R.id.tv_channel).text =
            String.format(getString(R.string.layout_channel), BuildConfig.FLAVOR)
        findViewById<Button>(R.id.btn_1).setOnClickListener(this)
        findViewById<Button>(R.id.btn_2).setOnClickListener(this)
        findViewById<Button>(R.id.btn_3).setOnClickListener(this)
        findViewById<Button>(R.id.btn_4).setOnClickListener(this)

        //delete downloaded old Apk
        val result = ApkUtil.deleteOldApk(this, "${externalCacheDir?.path}/$apkName")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_1 -> startUpdate1()
            R.id.btn_2 -> startUpdate2()
            R.id.btn_3 -> startUpdate3()
            R.id.btn_4 -> {
                manager?.cancel()
            }
        }
    }

    private fun startUpdate1() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_msg)
            .setPositiveButton(R.string.dialog_confirm) { _, _ ->
                startUpdate2()
            }.create()
            .show()
    }

    private fun startUpdate2() {
        resetPb()
        manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            apkName(apkName)
            smallIcon(R.mipmap.ic_launcher)
            build()
        }
        manager!!.download()
    }


    private fun startUpdate3() {
        manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            apkName(apkName)
            smallIcon(R.mipmap.ic_launcher)
            showNewerToast(true)
            apkVersionCode(2)
            apkVersionName("v4.2.1")
            apkSize("7.7MB")
            apkDescription(getString(R.string.dialog_msg))
            enableLog(true)
            jumpInstallPage(true)
            dialogButtonTextColor(Color.WHITE)
            showNotification(true)
            showBgdToast(false)
            forcedUpgrade(false)
            onDownloadListener(listenerAdapter)
//            apkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
//            httpManager()
//            dialogImage(R.drawable.ic_dialog)
//            dialogButtonColor(Color.parseColor("#E743DA"))
//            dialogProgressBarColor(Color.parseColor("#E743DA"))
//            notificationChannel()
//            notifyId(1011)
            onButtonClickListener(this@MainActivity)
            build()
        }
        manager?.download()
    }

    private fun resetPb() {
        progressBar.progress = 0
        tvPercent.text = "0%"
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {

        override fun downloading(max: Int, progress: Int) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            progressBar.max = 100
            progressBar.progress = curr
            tvPercent.text = "$curr%"
        }
    }


    override fun onButtonClick(id: Int) {
        Log.e(TAG, "onButtonClick: $id")
    }
}