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
    private lateinit var manager: DownloadManager
    private lateinit var apk: File
    private lateinit var progressBar: NumberProgressBar
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        title = ""
        setContentView(R.layout.dialog_update)
        init()
    }

    private fun init() {
        manager = DownloadManager.getInstance()
        manager.onDownloadListeners.add(listenerAdapter)
        setWindowSize()
        initView()
    }

    private fun initView() {
        val ibClose = findViewById<View>(R.id.ib_close)
        val vLine = findViewById<View>(R.id.line)
        val ivBg = findViewById<ImageView>(R.id.iv_bg)
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        val tvSize = findViewById<TextView>(R.id.tv_size)
        val tvDescription = findViewById<TextView>(R.id.tv_description)
        progressBar = findViewById(R.id.np_bar)
        btnUpdate = findViewById(R.id.btn_update)
        progressBar.visibility = if (manager.forcedUpgrade) View.VISIBLE else View.GONE
        btnUpdate.tag = 0
        btnUpdate.setOnClickListener(this)
        ibClose.setOnClickListener(this)
        if (manager.dialogImage != -1) {
            ivBg.setBackgroundResource(manager.dialogImage)
        }
        if (manager.dialogButtonTextColor != -1) {
            btnUpdate.setTextColor(manager.dialogButtonTextColor)
        }
        if (manager.dialogProgressBarColor != -1) {
            progressBar.reachedBarColor = manager.dialogProgressBarColor
            progressBar.setProgressTextColor(manager.dialogProgressBarColor)
        }
        if (manager.dialogButtonColor != -1) {
            val colorDrawable = GradientDrawable().apply {
                setColor(manager.dialogButtonColor)
                cornerRadius = DensityUtil.dip2px(this@UpdateDialogActivity, 3f)
            }
            val drawable = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), colorDrawable)
                addState(IntArray(0), colorDrawable)
            }
            btnUpdate.background = drawable
        }
        if (manager.forcedUpgrade) {
            vLine.visibility = View.GONE
            ibClose.visibility = View.GONE
        }
        if (manager.apkVersionName.isNotEmpty()) {
            tvTitle.text =
                String.format(resources.getString(R.string.dialog_new), manager.apkVersionName)
        }
        if (manager.apkSize.isNotEmpty()) {
            tvSize.text =
                String.format(resources.getString(R.string.dialog_new_size), manager.apkSize)
            tvSize.visibility = View.VISIBLE
        }
        tvDescription.text = manager.apkDescription
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
            R.id.ib_close -> {
                if (!manager.forcedUpgrade) {
                    finish()
                }
                manager.onButtonClickListener?.onButtonClick(OnButtonClickListener.CANCEL)
            }
            R.id.btn_update -> {
                if (btnUpdate.tag == install) {
                    ApkUtil.installApk(this, Constant.AUTHORITIES!!, apk)
                    return
                }
                if (manager.forcedUpgrade) {
                    btnUpdate.isEnabled = false
                    btnUpdate.text = resources.getString(R.string.background_downloading)
                } else {
                    finish()
                }
                manager.onButtonClickListener?.onButtonClick(OnButtonClickListener.UPDATE)
                startService(Intent(this, DownloadService::class.java))
            }
        }
    }

    override fun onBackPressed() {
        if (manager.forcedUpgrade) return
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private val listenerAdapter: OnDownloadListenerAdapter = object : OnDownloadListenerAdapter() {

        override fun downloading(max: Int, progress: Int) {
            if (max != -1 && progressBar.visibility == View.VISIBLE) {
                val curr = (progress / max.toDouble() * 100.0).toInt()
                progressBar.progress = curr
            } else {
                progressBar.visibility = View.GONE
            }
        }

        override fun done(apk: File) {
            this@UpdateDialogActivity.apk = apk
            if (manager.forcedUpgrade) {
                btnUpdate.tag = install
                btnUpdate.isEnabled = true
                btnUpdate.text = resources.getString(R.string.click_hint)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.onDownloadListeners.remove(listenerAdapter)
    }
}