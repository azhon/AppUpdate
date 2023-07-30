package com.azhon.appupdate.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.azhon.appupdate.R
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.util.ToastUtils
import java.io.File

open class BaseUpdateDialogFragment : DialogFragment(), OnDownloadListener {
    internal val manager: DownloadManager = DownloadManager.getInstance()
    lateinit var mView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager.config.registerDownloadListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView.tvSize().text = String.format(
            getString(R.string.update_size_tv), manager.config.apkSize
        )
        mView.tvDescription().text = manager.config.apkDescription.replace("\\n", "\n")
        mView.btnUpdate().setOnClickListener {
            manager.directDownload()
            it.isEnabled = false
        }
        if (!manager.config.forcedUpgrade) {
            mView.btnCancel().setOnClickListener {
                manager.cancel(this){
                    dismiss()
                }
            }
        } else {
            mView.btnCancel().visibility = View.INVISIBLE
            isCancelable = false
        }
    }

    override fun start() {
        mView.progressBar().run {
            visibility = View.VISIBLE
            max = 100
            progress = 0
        }
    }

    override fun downloading(max: Int, progress: Int) {
        val curr = (progress / max.toDouble() * 100.0).toInt()
        mView.progressBar().progress = curr
    }

    override fun done(apk: File) {
        dismiss()
    }

    override fun cancel() {
        ToastUtils.showLong(requireContext(), getString(R.string.has_cancel_download))
        dismiss()
    }

    override fun error(e: Throwable) {
        Log.e(PixelUpdateDialogFragment.TAG, "error:", e)
    }

    companion object {
        fun View.tvTitle(): TextView {
            return this.findViewById(R.id.app_update_tv_title)
        }

        fun View.tvSize(): TextView {
            return this.findViewById(R.id.app_update_tv_size)
        }

        fun View.tvDescription(): TextView {
            return this.findViewById(R.id.app_update_tv_description)
        }

        fun View.btnUpdate(): Button {
            return this.findViewById(R.id.app_update_btn_update)
        }

        fun View.btnCancel(): Button {
            return this.findViewById(R.id.app_update_ib_close)
        }

        fun View.progressBar(): ProgressBar {
            return this.findViewById(R.id.app_update_progress_bar)
        }
    }
}

class PixelUpdateDialogFragment : BaseUpdateDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStyle(STYLE_NORMAL, R.style.M3AppTheme)
        mView = inflater.inflate(R.layout.app_update_dialog_pixel, container, false)
        //全屏
        dialog?.window?.let { window ->
            //这步是必须的
            window.setBackgroundDrawableResource(R.color.transparent)
            //必要，设置 padding，这一步也是必须的，内容不能填充全部宽度和高度
            window.decorView.setPadding(0, 0, 0, 0)
            // 关键是这句，其实跟xml里的配置长得几乎一样
            val wlp: WindowManager.LayoutParams = window.attributes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                wlp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            wlp.gravity = Gravity.CENTER
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            wlp.height = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = wlp
        }
        return mView
    }

    override fun start() {
        super.start()
        mView.tvTitle().setText(R.string.update_title_downloading)
    }

    companion object {
        const val TAG = "UpdateDialogFragment"

        fun open(host: AppCompatActivity) {
            host.run {
                val dialog = PixelUpdateDialogFragment()
                val ft = supportFragmentManager.beginTransaction()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                dialog.show(ft, TAG)
            }
        }
    }

}

class Win8UpdateDialogFragment : BaseUpdateDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStyle(STYLE_NORMAL, R.style.M3AppTheme)
        mView = inflater.inflate(R.layout.app_update_dialog_win8, container, false)
        //全屏
        dialog?.window?.let { window ->
            //这步是必须的
            window.setBackgroundDrawableResource(R.color.transparent)
            //必要，设置 padding，这一步也是必须的，内容不能填充全部宽度和高度
            window.decorView.setPadding(0, 0, 0, 0)
            // 关键是这句，其实跟xml里的配置长得几乎一样
            val wlp: WindowManager.LayoutParams = window.attributes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                wlp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            wlp.gravity = Gravity.CENTER
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = wlp
        }
        return mView
    }


    companion object {
        const val TAG = "UpdateDialogFragment"

        fun open(host: AppCompatActivity) {
            host.run {
                val dialog = Win8UpdateDialogFragment()
                val ft = supportFragmentManager.beginTransaction()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                dialog.show(ft, TAG)
            }
        }
    }

}