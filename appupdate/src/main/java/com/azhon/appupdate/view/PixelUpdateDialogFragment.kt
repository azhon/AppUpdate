package com.azhon.appupdate.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.azhon.appupdate.R
import com.azhon.appupdate.config.Constant
import com.azhon.appupdate.listener.OnButtonClickListener
import com.azhon.appupdate.listener.OnDownloadListener
import com.azhon.appupdate.manager.DownloadManager
import com.azhon.appupdate.util.ApkUtil
import com.azhon.appupdate.util.ToastUtils
import java.io.File

class Action {
    companion object {
        //mode
        const val ready = 0x01
        const val downloading = 0x02
        const val readyInstall = 0x04

        const val canceled = 0x05
        const val error = 0x06
    }
}

open class BaseUpdateDialogFragment : DialogFragment(), OnDownloadListener {
    lateinit var vm: BaseUpdateDialogViewModel
    lateinit var mView: View
    val views: SparseArray<View> = SparseArray()
    val manager: DownloadManager = DownloadManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[BaseUpdateDialogViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manager.config.registerDownloadListener(this)
        initView()
        observeLivedata()
        vm.stateLivedata.value = Action.ready
        //如果正在下载，更新一下界面状态
        if (manager.downloadState){
            start()
        }
    }

    private fun observeLivedata() {
        vm.run {
            stateLivedata.observe(viewLifecycleOwner){
                when(it){
                    Action.ready->{
                        //更新按钮的初始文本
                        vm.updateButtonLivedata.value = ButtonState(
                            enable = manager.canDownload(),
                            stringId = R.string.update
                        )
                    }
                    Action.downloading->{
                        vm.updateButtonLivedata.value = ButtonState(
                            enable = false,
                            stringId = R.string.app_update_start_downloading
                        )
                    }
                    Action.readyInstall->{
                        updateButtonLivedata.postValue(
                            ButtonState(enable = true, stringId = R.string.install)
                        )
                    }
                    Action.error->{
                        updateButtonLivedata.postValue(
                            ButtonState(enable = true, stringId = R.string.app_update_download_error)
                        )
                    }
                    Action.canceled->{
                        updateButtonLivedata.postValue(
                            ButtonState(enable = true, stringId = R.string.cancel)
                        )
                    }
                }
            }
            updateButtonLivedata.observe(viewLifecycleOwner) {
                btnUpdate().apply {
                    isEnabled = it.enable
                    setText(it.stringId)
                }
            }
            progressbarLivedata.observe(viewLifecycleOwner) {
                progressBar().apply {
                    progress = it.progress
                    visibility = it.visibility
                }

            }
        }
    }

    private fun initView() {
        //title
        tvDescription().text = manager.config.apkDescription.replace("\\n", "\n")
        //size
        tvSize().text = String.format(
            getString(R.string.update_size_tv), manager.config.apkSize
        )
        //update button
        btnUpdate().setOnClickListener {
            manager.config.onButtonClickListener?.onButtonClick(OnButtonClickListener.UPDATE)
            if (vm.stateLivedata.value == Action.readyInstall) {
                vm.installApk(this.requireContext())
            } else {
                //执行下载
                manager.directDownload()
            }
        }
        //cancel button
        if (!manager.config.forcedUpgrade) {
            btnCancel().setOnClickListener {
                vm.stateLivedata.postValue(Action.canceled)
                manager.config.onButtonClickListener?.onButtonClick(OnButtonClickListener.CANCEL)
                ToastUtils.showLong(requireContext(), getString(R.string.has_cancel_download))
                manager.cancel()
                dismiss()
            }
        } else {
            btnCancel().visibility = View.INVISIBLE
            isCancelable = false
        }
        //progressbar
        initProgressBar()
    }

    private fun initProgressBar() {
        progressBar().run {
            max = 100
            progress = 0
        }
    }

    //===============download listener start===================//
    override fun start() {
        vm.progressbarLivedata.postValue(ProgressState(visibility = View.VISIBLE))
        vm.stateLivedata.postValue(Action.downloading)
    }

    override fun downloading(max: Int, progress: Int) {
        val curr = (progress / max.toDouble() * 100.0).toInt()
        progressBar().progress = curr
    }

    override fun done(apk: File) {
        vm.downloadFinish(apk)
        //service在下载完成后，会判断是否要自动跳转安装，所以ui界面中不需要处理跳转安装
        if (manager.config.jumpInstallPage) {
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //save progressbar state
        vm.progressbarLivedata.value =
            ProgressState(progressBar().progress, progressBar().visibility)
    }

    override fun cancel() {}

    override fun error(e: Throwable) {
        Log.e(TAG, "error:", e)
        vm.stateLivedata.postValue(Action.error)
    }
    //===============download listener end===================//

    override fun onDestroyView() {
        super.onDestroyView()
        manager.config.onDownloadListeners.remove(this)
    }

    //some view function
    inline fun <reified T : View> findView(id: Int): T =
        views.get(id)?.let {
            it as T
        } ?: let {
            val view: T = mView.findViewById<T>(id)
            views[id] = view
            view
        }

    fun tvTitle(): TextView = findView(R.id.app_update_tv_title)
    fun tvSize(): TextView = findView(R.id.app_update_tv_size)
    fun tvDescription(): TextView = findView(R.id.app_update_tv_description)
    fun btnUpdate(): Button = findView(R.id.app_update_btn_update)
    fun btnCancel(): Button = findView(R.id.app_update_ib_close)
    fun progressBar(): ProgressBar = findView(R.id.app_update_progress_bar)

    companion object {
        const val TAG = "BaseUpdateDialog"
    }

}

data class ProgressState(
    val progress: Int = 0,
    val visibility: Int = View.VISIBLE
) : java.io.Serializable

data class ButtonState(
    val enable: Boolean = true,
    val visibility: Int = View.VISIBLE,
    val stringId: Int
) : java.io.Serializable

class BaseUpdateDialogViewModel : ViewModel() {
    var apkFile: File? = null

    //update button
    val updateButtonLivedata: MutableLiveData<ButtonState> =
        MutableLiveData()

    //progressbar
    val progressbarLivedata: MutableLiveData<ProgressState> =
        MutableLiveData()

    //download & display state
    val stateLivedata: MutableLiveData<Int> =
        MutableLiveData()

    fun installApk(context: Context) {
        apkFile?.let { it1 ->
            ApkUtil.installApk(context, Constant.AUTHORITIES!!, it1)
        }
    }

    /**
     * when download finish,
     * update button state changed,
     * state changed
     */
    fun downloadFinish(apk: File) {
        apkFile = apk
        stateLivedata.postValue(Action.readyInstall)
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
        tvTitle().setText(R.string.update_title_downloading)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("title", tvTitle().text.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            tvTitle().text = it.getString("title", getString(R.string.update_title))
        }
    }

    companion object {
        const val TAG = "PixelUpdateDialogFragment"

        fun open(host: FragmentActivity) {
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
        const val TAG = "Win8UpdateDialogFragment"

        fun open(host: FragmentActivity) {
            host.run {
                val dialog = Win8UpdateDialogFragment()
                val ft = supportFragmentManager.beginTransaction()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                dialog.show(ft, TAG)
            }
        }
    }

}