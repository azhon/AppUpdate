package com.azhon.appupdate.view

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.azhon.appupdate.R
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListenerAdapter
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.service.DownloadService
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.DensityUtil
import com.azhon.appupdate.util.LogUtil
import java.io.File


/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.view
 * FileName:    UpdateDialogActivity
 * CreateDate:  2022/4/7 on 17:40
 * Desc:
 *
 * @author   azhon
 */

class UpdateDialogActivity : AppCompatActivity(), View.OnClickListener {

    private val install = 0x45
    private val error = 0x46
    private var manager: DownloadManager? = null
    private lateinit var apk: File
    private lateinit var progressBar: NumberProgressBar
    private lateinit var btnUpdate: Button

    companion object {
        private const val TAG = "UpdateDialogActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        title = ""
        setContentView(R.layout.app_update_dialog_update)
        init()
    }

    private fun init() {
        manager = try {
            DownloadManager.getInstance()
        } catch (e: IllegalArgumentException) {
            LogUtil.e(TAG, "An exception occurred by DownloadManager=null,please check your code!")
            return
        }
        if (manager!!.config.forcedUpgrade) {
            manager!!.config.onDownloadListeners.add(listenerAdapter)
        }
        setWindowSize()
        initView(manager!!)
    }

    private fun initView(manager: DownloadManager) {
        val ibClose = findViewById<View>(R.id.app_update_ib_close)
        val vLine = findViewById<View>(R.id.app_update_line)
        val ivBg = findViewById<ImageView>(R.id.app_update_iv_bg)
        val tvTitle = findViewById<TextView>(R.id.app_update_tv_title)
        val tvSize = findViewById<TextView>(R.id.app_update_tv_size)
        val tvDescription = findViewById<TextView>(R.id.app_update_tv_description)
        progressBar = findViewById(R.id.app_update_progress_bar)
        btnUpdate = findViewById(R.id.app_update_btn_update)
        progressBar.visibility = if (manager.config.forcedUpgrade) View.VISIBLE else View.GONE
        btnUpdate.tag = 0
        btnUpdate.setOnClickListener(this)
        ibClose.setOnClickListener(this)
        if (manager.config.viewConfig.dialogImage != -1) {
            ivBg.setBackgroundResource(manager.config.viewConfig.dialogImage)
        }
        if (manager.config.viewConfig.dialogButtonTextColor != -1) {
            btnUpdate.setTextColor(manager.config.viewConfig.dialogButtonTextColor)
        }
        if (manager.config.viewConfig.dialogProgressBarColor != -1) {
            progressBar.reachedBarColor = manager.config.viewConfig.dialogProgressBarColor
            progressBar.setProgressTextColor(manager.config.viewConfig.dialogProgressBarColor)
        }
        if (manager.config.viewConfig.dialogButtonColor != -1) {
            val colorDrawable = GradientDrawable().apply {
                setColor(manager.config.viewConfig.dialogButtonColor)
                cornerRadius = DensityUtil.dip2px(this@UpdateDialogActivity, 3f)
            }
            val drawable = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), colorDrawable)
                addState(IntArray(0), colorDrawable)
            }
            btnUpdate.background = drawable
        }
        if (manager.config.forcedUpgrade) {
            vLine.visibility = View.GONE
            ibClose.visibility = View.GONE
        }
        if (manager.config.apkVersionName.isNotEmpty()) {
            tvTitle.text =
                String.format(
                    resources.getString(R.string.app_update_dialog_new),
                    manager.config.apkVersionName
                )
        }
        if (manager.config.apkSize.isNotEmpty()) {
            tvSize.text =
                String.format(
                    resources.getString(R.string.app_update_dialog_new_size),
                    manager.config.apkSize
                )
            tvSize.visibility = View.VISIBLE
        }
        tvDescription.text = manager.config.apkDescription
    }

    private fun setWindowSize() {
        val attributes = window.attributes
        attributes.width = (resources.displayMetrics.widthPixels * 0.75f).toInt()
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        attributes.gravity = Gravity.CENTER
        window.attributes = attributes
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.app_update_ib_close -> {
                if (manager?.config?.forcedUpgrade == false) {
                    finish()
                }
                manager?.config?.onButtonClickListener?.onButtonClick(OnButtonClickListener.CANCEL)
            }
            R.id.app_update_btn_update -> {
                if (btnUpdate.tag == install) {
                    ApkUtil.installApk(this, Constant.AUTHORITIES!!, apk)
                    return
                }
                if (manager?.config?.forcedUpgrade == true) {
                    btnUpdate.isEnabled = false
                    btnUpdate.text = resources.getString(R.string.app_update_background_downloading)
                } else {
                    finish()
                }
                manager?.config?.onButtonClickListener?.onButtonClick(OnButtonClickListener.UPDATE)
                startService(Intent(this, DownloadService::class.java))
            }
        }
    }

    override fun onBackPressed() {
        if (manager?.config?.forcedUpgrade == true) return
        super.onBackPressed()
        manager?.config?.onButtonClickListener?.onButtonClick(OnButtonClickListener.CANCEL)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {
        override fun start() {
            btnUpdate.isEnabled = false
            btnUpdate.text = resources.getString(R.string.app_update_background_downloading)
        }

        override fun downloading(max: Int, progress: Int) {
            if (max != -1) {
                val curr = (progress / max.toDouble() * 100.0).toInt()
                progressBar.progress = curr
            } else {
                progressBar.visibility = View.GONE
            }
        }

        override fun done(apk: File) {
            this@UpdateDialogActivity.apk = apk
            btnUpdate.tag = install
            btnUpdate.isEnabled = true
            btnUpdate.text = resources.getString(R.string.app_update_click_hint)
        }

        override fun error(e: Throwable) {
            btnUpdate.tag = error
            btnUpdate.isEnabled = true
            btnUpdate.text = resources.getString(R.string.app_update_continue_downloading)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager?.config?.onDownloadListeners?.remove(listenerAdapter)
    }
}