package com.azhon.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.view.NumberProgressBar

class MainActivity : AppCompatActivity(), View.OnClickListener, OnButtonClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val url = "https://down.qq.com/qqweb/QQ_1/android_apk/Android_8.7.0.5295_537068059.apk"
    private var manager: DownloadManager? = null
    private lateinit var progressBar: NumberProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_title)
        progressBar = findViewById(R.id.number_progress_bar)
        findViewById<Button>(R.id.btn_1).setOnClickListener(this)
        findViewById<Button>(R.id.btn_2).setOnClickListener(this)
        findViewById<Button>(R.id.btn_3).setOnClickListener(this)
        findViewById<Button>(R.id.btn_4).setOnClickListener(this)

        //delete downloaded old Apk
//        val result = ApkUtil.deleteOldApk(this, "${externalCacheDir?.path}/QQ.apk")
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
        progressBar.progress = 0
        manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            apkName("QQ.apk")
            smallIcon(R.mipmap.ic_launcher)
            build()
        }
        manager!!.download()
    }


    private fun startUpdate3() {
        manager = DownloadManager.Builder(this).run {
            apkUrl(url)
            apkName("QQ.apk")
            smallIcon(R.mipmap.ic_launcher)
            showNewerToast(true)
            apkVersionCode(2)
            apkVersionName("v4.2.1")
            apkSize("7.7MB")
            apkDescription(getString(R.string.dialog_msg))
            //apkMD5("DC501F04BBAA458C9DC33008EFED5E7F")

            //flow are unimportant filed
            enableLog(true)
            //httpManager()
            jumpInstallPage(true)
//            dialogImage(R.drawable.ic_dialog)
//            dialogButtonColor(Color.parseColor("#E743DA"))
//            dialogProgressBarColor(Color.parseColor("#E743DA"))
            dialogButtonTextColor(Color.WHITE)
            showNotification(true)
            showBgdToast(false)
            forcedUpgrade(false)
            onDownloadListener(listenerAdapter)
            onButtonClickListener(this@MainActivity)
            build()
        }
        manager?.download()
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {

        override fun downloading(max: Int, progress: Int) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            progressBar.max = 100
            progressBar.progress = curr
        }
    }

    override fun onButtonClick(id: Int) {
        Log.e(TAG, "onButtonClick: $id")
    }
}