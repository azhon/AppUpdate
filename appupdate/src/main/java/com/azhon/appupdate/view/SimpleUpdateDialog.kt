package com.azhon.appupdate.view

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import android.view.View
import com.azhon.appupdate.R
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.databinding.ItemUpdateDialogBinding
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class SimpleUpdateDialog {
    companion object {
        /**
         * 展示弹窗，以及下载安装更新
         */
        fun openAlertDialog(activity: Activity, manager: DownloadManager) {
            val TAG="SimpleUpdateDialog"
            var action = Action.downloading
            var apkFile: File? = null
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
                        manager.config.onButtonClickListener?.onButtonClick(OnButtonClickListener.CANCEL)
                        dialog_interface.dismiss()
                    }
                }
                mView.appUpdateTvSize.setText(
                    String.format(
                        activity.getString(R.string.update_size_tv),
                        manager.config.apkSize
                    )
                )
                mView.appUpdateTvDescription.text =
                    manager.config.apkDescription.replace("\\n", "\n")
            }
            dialogBuilder.show().apply {
                //dismiss listener
                this.setOnDismissListener {
                    manager.cancel()
                    manager.config.onDownloadListeners.remove(mOnDownloadListener)
                }
                getButton(AlertDialog.BUTTON_POSITIVE).also { positiveButton ->
                    mOnDownloadListener = object : OnDownloadListener {
                        override fun cancel() {}

                        override fun done(apk: File) {
                            if (!manager.config.jumpInstallPage) {
                                apkFile = apk
                                positiveButton.isEnabled = true
                                action = Action.readyInstall
                                positiveButton.setText(R.string.install)
                            } else {
                                dismiss()
                            }
                        }

                        override fun downloading(max: Int, progress: Int) {
//                        Log.d(TAG, "downloading:max-> $max ,progress-> $progress")
                            val curr = (progress / max.toDouble() * 100.0).toInt()
                            mView.appUpdateProgressBar.progress = curr
                        }

                        override fun error(e: Throwable) {
                            Log.e(TAG, "error: 下载错误", e)
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
                        manager.config.onButtonClickListener?.onButtonClick(OnButtonClickListener.UPDATE)
                        if (action == Action.readyInstall) {
                            apkFile?.let { it1 ->
                                ApkUtil.installApk(activity, Constant.AUTHORITIES!!, it1)
                            }
                        } else {
                            //下载并安装更新
                            manager.directDownload()
                        }

                    }
                }
            }
        }
    }
}