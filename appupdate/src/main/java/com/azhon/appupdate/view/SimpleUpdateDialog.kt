package com.azhon.appupdate.view

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.View
import com.azhon.appupdate.R
import com.azhon.appupdate.databinding.ItemUpdateDialogBinding
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class SimpleUpdateDialog {
    companion object {
        /**
         * 展示弹窗，以及下载安装更新
         */
        fun openAlertDialog(activity: Activity, manager: DownloadManager) {
            val mView = ItemUpdateDialogBinding.inflate(activity.layoutInflater)
            //弹窗
            val dialogBuilder = MaterialAlertDialogBuilder(
                activity,
                com.google.android.material.R.style.MaterialAlertDialog_Material3
            ).apply {
                setTitle("发现新版本 ${manager.config.apkVersionName}")
                setView(mView.root)
                setPositiveButton("更新", null)
                setCancelable(false)
                if (!manager.config.forcedUpgrade) {
                    setNegativeButton("取消") { dialog_interface, _ ->
                        dialog_interface.dismiss()
                    }
                }
                mView.appUpdateTvSize.setText("文件大小: ${manager.config.apkSize}")
                mView.appUpdateTvDescription.setText(
                    manager.config.apkDescription.replace(
                        "\\n",
                        "\n"
                    )
                )
            }
            dialogBuilder.show().apply {
                getButton(AlertDialog.BUTTON_POSITIVE).also { positiveButton ->
                    manager.config.registerDownloadListener(object : OnDownloadListener {
                        override fun cancel() {
                            manager.cancel()
                            ToastUtils.showLong(activity, "已取消下载")
                            dismiss()
                        }

                        override fun done(apk: File) {
                            ToastUtils.showLong(activity, "完成")
                            dismiss()
                        }

                        override fun downloading(max: Int, progress: Int) {
//                        Log.d(TAG, "downloading:max-> $max ,progress-> $progress")
                            val curr = (progress / max.toDouble() * 100.0).toInt()
                            mView.appUpdateProgressBar.progress = curr
                        }

                        override fun error(e: Throwable) {
                            Log.e(UpdateDialogFragment.TAG, "error: 下载错误", e)
                        }

                        override fun start() {
                            mView.appUpdateProgressBar.visibility = View.VISIBLE
                            mView.appUpdateProgressBar.max = 100
                            mView.appUpdateProgressBar.progress = 0
                            positiveButton.isEnabled = false
                        }
                    })
                    positiveButton.setOnClickListener {
                        //下载并安装更新
                        manager.directDownload()
                    }
                }
            }
        }
    }
}