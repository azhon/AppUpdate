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
            var mOnDownloadListener: OnDownloadListener? = null
            val mView = ItemUpdateDialogBinding.inflate(activity.layoutInflater)
            //弹窗
            val dialogBuilder = MaterialAlertDialogBuilder(
                activity,
                com.google.android.material.R.style.MaterialAlertDialog_Material3
            ).apply {
                setTitle(
                    String.format(
                        activity.getString(R.string.app_update_dialog_new),
                        manager.config.apkVersionName
                    )
                )
                setView(mView.root)
                setPositiveButton(R.string.update, null)
                setCancelable(false)
                if (!manager.config.forcedUpgrade) {
                    setNegativeButton(R.string.cancel) { dialog_interface, _ ->
                        manager.cancel {
                            dialog_interface.dismiss()
                        }
                    }
                }
                mView.appUpdateTvSize.setText(
                    String.format(
                        activity.getString(R.string.update_size_tv),
                        manager.config.apkSize
                    )
                )
                mView.appUpdateTvDescription.text=
                    manager.config.apkDescription.replace("\\n", "\n")
            }
            dialogBuilder.show().apply {
                getButton(AlertDialog.BUTTON_POSITIVE).also { positiveButton ->
                    mOnDownloadListener = object : OnDownloadListener {
                        override fun cancel() {
                            manager.clearListener()
                            ToastUtils.showLong(activity, activity.getString(R.string.has_cancel_download))
                            dismiss()
                        }

                        override fun done(apk: File) {
                            manager.clearListener()
                            dismiss()
                        }

                        override fun downloading(max: Int, progress: Int) {
//                        Log.d(TAG, "downloading:max-> $max ,progress-> $progress")
                            val curr = (progress / max.toDouble() * 100.0).toInt()
                            mView.appUpdateProgressBar.progress = curr
                        }

                        override fun error(e: Throwable) {
                            Log.e(PixelUpdateDialogFragment.TAG, "error: 下载错误", e)
                        }

                        override fun start() {
                            mView.appUpdateProgressBar.visibility = View.VISIBLE
                            mView.appUpdateProgressBar.max = 100
                            mView.appUpdateProgressBar.progress = 0
                            positiveButton.isEnabled = false
                        }
                    }
                    mOnDownloadListener?.let {
                        manager.config.registerDownloadListener(it)
                    }
                    positiveButton.setOnClickListener {
                        //下载并安装更新
                        manager.directDownload()
                    }
                }
            }
        }
    }
}