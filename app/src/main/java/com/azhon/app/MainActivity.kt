package com.azhon.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.manager.ViewType
import com.azhon.appupdate.manager.downloadApp
import com.azhon.appupdate.util.ApkUtil

class MainActivity : AppCompatActivity(), View.OnClickListener, OnButtonClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val url = "http://s.duapps.com/apks/own/ESFileExplorer-cn.apk"
    private val apkName = "appupdate.apk"
    private var manager: DownloadManager? = null
    private lateinit var tvPercent: TextView
    private lateinit var progressBar: ProgressBar
    private var viewStyle = ViewType.Colorful

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_title)
        progressBar = findViewById(R.id.number_progress_bar)
        tvPercent = findViewById<Button>(R.id.tv_percent)
        findViewById<TextView>(R.id.tv_channel).text =
            String.format(getString(R.string.layout_channel), BuildConfig.FLAVOR)
        findViewById<Button>(R.id.btn_2).setOnClickListener(this)
        findViewById<Button>(R.id.btn_3).setOnClickListener(this)
        findViewById<Button>(R.id.btn_4).setOnClickListener(this)
        findViewById<RadioGroup>(R.id.radio_group).setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.colorful->{
                    viewStyle=ViewType.Colorful
                }
                R.id.simpledialog->{
                    viewStyle=ViewType.SimpleDialog
                }
                R.id.pixel->{
                    viewStyle=ViewType.Pixel
                }
                R.id.win->{
                    viewStyle=ViewType.Win8
                }
            }
        }
        //delete downloaded old Apk
        val result = ApkUtil.deleteOldApk(this, "${externalCacheDir?.path}/$apkName")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_2 -> startUpdate2()
            R.id.btn_3 -> startUpdate3()
            R.id.btn_4 -> {
                manager?.cancel()
            }
        }
    }

    /**
     * 不使用内置视图，通过[registerDownloadListener]自己实现界面
     */
    private fun startUpdate2() {
        resetPb()
        downloadApp {
            viewType = ViewType.None
            apkUrl = url
            apkName = this@MainActivity.apkName
            smallIcon = R.mipmap.ic_launcher
            apkVersionCode = 2
            apkVersionName = "v4.2.1"
            apkSize = "7.7MB"
            apkDescription = getString(R.string.dialog_msg)
            showNotification = true
            showBgdToast = false
            forcedUpgrade = false
            enableLog(true)
            jumpInstallPage = true
            registerDownloadListener(listenerAdapter)
        }
    }


    private fun startUpdate3() {
        manager = DownloadManager.config(application) {
            viewType = viewStyle
            apkUrl = url
            apkName = this@MainActivity.apkName
            smallIcon = R.mipmap.ic_launcher
            apkVersionCode = 2
            apkVersionName = "v4.2.1"
            apkSize = "7.7MB"
            apkDescription = getString(R.string.dialog_msg)
//            apkMD5="DC501F04BBAA458C9DC33008EFED5E7F"

            enableLog(true)
//            httpManager()
            jumpInstallPage = false
            configDialog {
//              dialogImage=R.drawable.ic_dialog
//              dialogButtonColor=Color.parseColor("#E743DA")
//              dialogProgressBarColor=Color.parseColor("#E743DA")
                showNewerToast = true
                dialogButtonTextColor = Color.WHITE
            }
            showNotification = true
            showBgdToast = false
            forcedUpgrade = false
            registerDownloadListener(listenerAdapter)
            onButtonClickListener = this@MainActivity
        }
        downloadApp(manager!!)
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